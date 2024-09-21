package rs.v9.myeconomy.group;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.claim.ClaimHandler.*;

public class GroupHandler {

    private static ArrayList<UUID> chat = new ArrayList<>();

    private static HashMap<UUID, UUID> invites = new HashMap<>();
    private static JSONObject players = new JSONObject();
    private static HashMap<UUID, MyGroup> groupsByUUID = new HashMap<>();
    private static HashMap<String, UUID> groupsByName = new HashMap<>();
    private static Zone safeZone, pvpZone;

    public GroupHandler(){
        safeZone = new Zone(UUID.fromString("10b33fd4-ead7-4aa1-84d6-59aedfbd71ed"), "Safe Zone", 2, 1);
        pvpZone = new Zone(UUID.fromString("80b251bd-a999-4c0a-97eb-f2f631c9a7e3"), "PVP Zone", 1, 5);

        //READING TIME
        try{
            File playersFile = new File(plugin.getDataFolder()+File.separator+"players.json");
            if(playersFile.exists()){
                players = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        File groupFolder = new File(plugin.getDataFolder()+File.separator+"group");
        if(groupFolder.exists()){
            for(File groupFile : groupFolder.listFiles()){
                MyGroup group = new MyGroup();
                group.init(groupFile.getName());
                groupsByUUID.put(group.getKey(), group);
                groupsByName.put(group.getName().toLowerCase(), group.getKey());
            }
        }
    }

    public static Zone getSafeZone(){
        return safeZone;
    }

    public static Zone getPvpZone(){
        return pvpZone;
    }

    public static Zone getZoneByUUID(UUID uuid){
        if(uuid.equals(UUID.fromString("10b33fd4-ead7-4aa1-84d6-59aedfbd71ed"))){
            return safeZone;

        }else if(uuid.equals(UUID.fromString("80b251bd-a999-4c0a-97eb-f2f631c9a7e3"))){
            return pvpZone;
        }
        return null;
    }

    public static ArrayList<MyGroup> getListOfGroups(){
        if(groupsByUUID.size() > 0){
            return new ArrayList<>(groupsByUUID.values());
        }
        return null;
    }

    public static ArrayList<String> getListOfGroupNames(){
        if(groupsByName.size() > 0){
            return new ArrayList<>(groupsByName.keySet());
        }
        return null;
    }

    public static MyGroup getGroupFromUUID(UUID uuid){
        if(groupsByUUID.containsKey(uuid)){
            return groupsByUUID.get(uuid);
        }
        return null;
    }

    public static MyGroup getGroupFromName(String name){
        if(groupsByName.containsKey(name.toLowerCase())){
            if(groupsByUUID.containsKey(groupsByName.get(name.toLowerCase()))){
                return groupsByUUID.get(groupsByName.get(name.toLowerCase()));
            }
        }
        return null;
    }

    public static MyGroup getPlayersGroup(UUID uuid){
        if(players.has(uuid.toString())){
            if(groupsByUUID.containsKey(UUID.fromString(players.getString(uuid.toString())))){
                return groupsByUUID.get(UUID.fromString(players.getString(uuid.toString())));
            }
        }
        return null;
    }

    public static boolean isPlayerInGroup(UUID uuid){
        return players.has(uuid.toString());
    }

    public static boolean isGroup(String name){
        if(groupsByName.containsKey(name.toLowerCase())){
            if(groupsByUUID.containsKey(groupsByName.get(name.toLowerCase()))){
                return true;
            }else{
                groupsByName.remove(name.toLowerCase());
            }
        }
        return false;
    }

    public static void createGroup(UUID uuid, MyGroup group){
        //if(!factionsByUUID.containsKey(faction.getKey())){
        groupsByUUID.put(group.getKey(), group);
        //}

        //if(!factionsByName.containsKey(faction.getName())){
        groupsByName.put(group.getName().toLowerCase(), group.getKey());
        //}

        players.put(uuid.toString(), group.getKey().toString());

        writePlayers();
    }

    public static void renameGroup(String oldName, String name){
        if(groupsByName.containsKey(oldName.toLowerCase())){
            UUID key = groupsByName.get(oldName.toLowerCase());
            groupsByName.remove(oldName.toLowerCase());
            groupsByName.put(name.toLowerCase(), key);
        }
    }

    public static void deleteGroup(MyGroup group){
        if(groupsByUUID.containsKey(group.getKey())){
            groupsByUUID.remove(group.getKey());
        }

        if(groupsByName.containsKey(group.getName().toLowerCase())){
            groupsByName.remove(group.getName().toLowerCase());
        }

        unclaimAllForGroup(group.getKey());

        for(String suuid : group.getPlayers()){
            UUID uuid = UUID.fromString(suuid);
            if(players.has(uuid.toString())){
                players.remove(uuid.toString());
            }

            if(isAutoClaiming(uuid)){
                stopAutoClaiming(uuid);
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if(player != null && player.isOnline()){
                player.getPlayer().sendMessage("§cYour group has been disbanded, you are no longer a part of a group!");
            }
        }
        writePlayers();

        File groupFolder = new File(plugin.getDataFolder()+File.separator+"group"+File.separator+group.getKey());
        plugin.getServer().broadcastMessage(groupFolder.getPath().toString());

        deleteFolder(groupFolder);
    }

    private static void deleteFolder(File folder){
        File[] files = folder.listFiles();
        if(files != null){
            for(File f: files){
                if(f.isDirectory()){
                    deleteFolder(f);
                }else{
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void addPlayerToGroup(UUID uuid, UUID groupName){
        players.put(uuid.toString(), groupName.toString());
        writePlayers();
    }

    public static void removePlayerFromGroup(UUID uuid){
        if(players.has(uuid.toString())){
            players.remove(uuid.toString());

            if(isAutoClaiming(uuid)){
                stopAutoClaiming(uuid);
            }

            writePlayers();
        }
    }

    public static boolean hasInviteToGroup(UUID uuid){
        return invites.containsKey(uuid);
    }

    public static void removeInviteToGroup(UUID uuid){
        if(invites.containsKey(uuid)){
            invites.remove(uuid);
        }
    }

    public static void inviteToGroup(UUID uuid, UUID key){
        invites.put(uuid, key);
    }

    public static MyGroup getGroupInvite(UUID uuid){
        if(invites.containsKey(uuid)){
            UUID key = invites.get(uuid);
            if(groupsByUUID.containsKey(key)){
                invites.remove(uuid);
                return groupsByUUID.get(key);
            }
        }
        return null;
    }

    public static HashMap<Player, Integer> delayedTask = new HashMap<>();

    public static void setPlayerInviteTask(Player player, int task){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
        }
        delayedTask.put(player, task);
    }

    public static void removePlayerInviteTask(Player player){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
            delayedTask.remove(player);
        }
    }


    public static void startChatting(UUID uuid){
        if(!chat.contains(uuid)){
            chat.add(uuid);
        }
    }

    public static void stopChatting(UUID uuid){
        if(chat.contains(uuid)){
            chat.remove(uuid);
        }
    }

    public static boolean isChatting(UUID uuid){
        return chat.contains(uuid);
    }


    public static boolean isBannedName(String name){
        String[] tmp = {
                "safe-zone",
                "safezone",
                "pvp-zone",
                "pvpzone",
                "wilderness"
        };

        List<String> names = Arrays.asList(tmp);

        return names.contains(name.toLowerCase());
    }

    public static void writePlayers(){
        try{
            if(!plugin.getDataFolder().exists()){
                plugin.getDataFolder().mkdirs();
            }

            FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"players.json"));
            out.write(players.toString());
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
