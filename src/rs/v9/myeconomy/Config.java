package rs.v9.myeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rs.v9.myeconomy.Main.plugin;

public class Config {

    private static Location spawn, endSpawn;
    private static Map<String, Location> warps = new HashMap<>();
    private static Map<Player, Player> playerTeleport = new HashMap<>();
    private static Map<Player, Long> afkList = new HashMap<>();

    private static String[] ranks = { "Member", "Recruit", "Admin", "Owner" };

    private static int teleportDelay = 2,
            createPower = 20,
            joinPower = 20,
            claimCost = 1,
            createWarpCost = 5,
            maxShops = 6;

    private static boolean backTeleport = true,
            homeTeleport = true,
            groupHome = true,
            groupWarp = true,
            shops = true;

    public Config(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File configFile = new File(plugin.getDataFolder()+File.separator+"config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if(configFile.exists()){
                teleportDelay = config.getInt("teleportation.delay");
                backTeleport = config.getBoolean("teleportation.back");
                homeTeleport = config.getBoolean("teleportation.home");

                createPower = config.getInt("group.create-power");
                joinPower = config.getInt("group.join-power");
                claimCost = config.getInt("group.claim-cost");
                createWarpCost = config.getInt("group.create-warp-cost");
                groupHome = config.getBoolean("group.home");
                groupWarp = config.getBoolean("group.warp");
                ranks = config.getStringList("group.ranks").toArray(new String[0]);

                if(config.contains("spawn")){
                    spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")),
                            config.getDouble("spawn.x"),
                            config.getDouble("spawn.y"),
                            config.getDouble("spawn.z"),
                            (float)config.getDouble("spawn.yaw"),
                            (float)config.getDouble("spawn.pitch"));
                }

                if(config.contains("end-spawn")){
                    endSpawn = new Location(Bukkit.getWorld(config.getString("end-spawn.world")),
                            config.getDouble("end-spawn.x"),
                            config.getDouble("end-spawn.y"),
                            config.getDouble("end-spawn.z"),
                            (float)config.getDouble("end-spawn.yaw"),
                            (float)config.getDouble("end-spawn.pitch"));
                }

            }else{
                config.options().header("teleportation.delay and faction.periodic-increase-time are in seconds.");
                config.set("teleportation.delay", 2);
                config.set("teleportation.back", true);
                config.set("teleportation.home", true);

                config.set("group.create-power", 20);
                config.set("group.join-power", 20);
                config.set("group.claim-cost", 1);
                config.set("group.create-warp-cost", 5);
                config.set("group.home", true);
                config.set("group.warp", true);
                config.set("group.ranks", new String[]{ "Member", "Recruit", "Admin", "Owner" });

                config.set("shop.enabled", true);
                config.set("shop.max-per-player", 6);

                config.save(configFile);
            }

            File warpsFile = new File(plugin.getDataFolder()+File.separator+"warps.yml");
            config = YamlConfiguration.loadConfiguration(warpsFile);

            for(String warpKey : config.getKeys(false)){
                Location location = new Location(Bukkit.getWorld(config.getString(warpKey+".world")),
                        config.getDouble(warpKey+".x"),
                        config.getDouble(warpKey+".y"),
                        config.getDouble(warpKey+".z"),
                        (float)config.getDouble(warpKey+".yaw"),
                        (float)config.getDouble(warpKey+".pitch"));

                warps.put(warpKey, location);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getTeleportDelay(){
        return teleportDelay*20;
    }

    public static boolean isBackTeleport(){
        return backTeleport;
    }

    public static boolean isHomeTeleport(){
        return homeTeleport;
    }

    public static int getCreatePower(){
        return createPower;
    }

    public static int getJoinPower(){
        return joinPower;
    }

    public static int getClaimCost(){
        return claimCost;
    }

    public static int getCreateWarpCost(){
        return createWarpCost;
    }

    public static boolean isGroupHome(){
        return groupHome;
    }

    public static boolean isGroupWarp(){
        return groupWarp;
    }

    public static String[] getRanks(){
        return ranks;
    }

    public static boolean isShops(){
        return shops;
    }

    public static int getMaxShops(){
        return maxShops;
    }

    public static void setSpawn(Location location){
        spawn = location;
        writeConfig();
    }

    public static Location getSpawn(){
        return spawn;
    }

    public static void setEndSpawn(Location location){
        endSpawn = location;
        writeConfig();
    }

    public static Location getEndSpawn(){
        return endSpawn;
    }

    public static Location getWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            return warps.get(name.toLowerCase());
        }
        return null;
    }

    public static ArrayList<String> getWarps(){
        return new ArrayList<>(warps.keySet());
    }

    public static void setWarp(String name, Location location){
        warps.put(name.toLowerCase(), location);
        writeWarps();
    }

    public static void removeWarp(String name){
        if(warps.containsKey(name.toLowerCase())){
            warps.remove(name.toLowerCase());
            writeWarps();
        }
    }

    public static boolean isWarp(String name){
        return warps.containsKey(name.toLowerCase());
    }

    public static HashMap<Player, Integer> delayedTask = new HashMap<>();

    public static void setPlayerTeleportTask(Player player, int task){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
        }
        delayedTask.put(player, task);
    }

    public static void removePlayerTeleportTask(Player player){
        if(delayedTask.containsKey(player)){
            plugin.getServer().getScheduler().cancelTask(delayedTask.get(player));
            delayedTask.remove(player);
        }
    }

    public static void removePlayerTeleport(Player player){
        if(playerTeleport.containsKey(player)){
            playerTeleport.remove(player);
        }
    }

    public static void setPlayerTeleport(Player sender, Player receiver){
        //if(playerTeleport.containsKey(sender)){
            playerTeleport.put(receiver, sender);
        //}
    }

    public static boolean hasPlayerTeleport(Player player){
        return playerTeleport.containsKey(player);
    }

    public static Player getPlayerTeleport(Player player){
        if(playerTeleport.containsKey(player)){
            return playerTeleport.get(player);
        }
        return null;
    }

    public static void setPlayerAFK(Player player){
        afkList.put(player, System.currentTimeMillis()+60_000);//600_000);
    }

    public static boolean isPlayerAFK(Player player){
        return afkList.containsKey(player);
    }

    public static long getPlayerAFK(Player player){
        return afkList.get(player);
    }

    public static List<Player> getAFKPlayers(){
        List<Player> afkPlayers = new ArrayList<>();
        long now = System.currentTimeMillis();

        for(Player player : afkList.keySet()){
            if(afkList.get(player) < now){
                afkPlayers.add(player);
            }
        }

        return afkPlayers;
    }

    private static void writeConfig(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File configFile = new File(plugin.getDataFolder()+File.separator+"config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if(spawn != null){
                config.set("spawn.world", spawn.getWorld().getName());
                config.set("spawn.x", spawn.getX());
                config.set("spawn.y", spawn.getY());
                config.set("spawn.z", spawn.getZ());
                config.set("spawn.yaw", spawn.getYaw());
                config.set("spawn.pitch", spawn.getPitch());
            }

            if(endSpawn != null){
                config.set("end-spawn.world", endSpawn.getWorld().getName());
                config.set("end-spawn.x", endSpawn.getX());
                config.set("end-spawn.y", endSpawn.getY());
                config.set("end-spawn.z", endSpawn.getZ());
                config.set("end-spawn.yaw", endSpawn.getYaw());
                config.set("end-spawn.pitch", endSpawn.getPitch());
            }

            config.save(configFile);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeWarps(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }

        try{
            File warpsFile = new File(plugin.getDataFolder()+File.separator+"warps.yml");
            if(warpsFile.exists()){
                OutputStream out = new FileOutputStream(warpsFile);
                out.flush();
                out.close();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(warpsFile);

            for(String warpKey : warps.keySet()){
                config.set(warpKey+".world", warps.get(warpKey).getWorld().getName());
                config.set(warpKey+".x", warps.get(warpKey).getX());
                config.set(warpKey+".y", warps.get(warpKey).getY());
                config.set(warpKey+".z", warps.get(warpKey).getZ());
                config.set(warpKey+".yaw", warps.get(warpKey).getYaw());
                config.set(warpKey+".pitch", warps.get(warpKey).getPitch());
            }

            config.save(warpsFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
