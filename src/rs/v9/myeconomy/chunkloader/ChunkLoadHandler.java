package rs.v9.myeconomy.chunkloader;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import rs.v9.myeconomy.holo.FakePlayer;

import java.io.*;
import java.util.*;

import static rs.v9.myeconomy.Config.renderedEntities;
import static rs.v9.myeconomy.Main.dynmap;
import static rs.v9.myeconomy.Main.plugin;

public class ChunkLoadHandler {

    private static Map<UUID, Location> chunks = new HashMap<>();
    private static Map<Chunk, FakePlayer> chunkToNpc = new HashMap<>();
    private static List<FakePlayer> npcList = new ArrayList<>();
    private static Map<UUID, Marker> markers = new HashMap<>();
    private static MarkerSet markerSet;

    public ChunkLoadHandler(){
        if(plugin.getDataFolder().exists()){
            try{
                File chunkFile = new File(plugin.getDataFolder()+ File.separator+"chunk_loader.ser");
                if(chunkFile.exists()){
                    DataInputStream in = new DataInputStream(new FileInputStream(chunkFile));

                    while(in.available() > 0){
                        byte[] b = new byte[in.readInt()];
                        in.read(b);
                        UUID uuid = UUID.fromString(new String(b));

                        b = new byte[in.readInt()];
                        in.read(b);
                        String world = new String(b);

                        Location loc = new Location(Bukkit.getWorld(world),
                                in.readDouble(), //x
                                in.readDouble(), //y
                                in.readDouble(), //z
                                in.readFloat(), //yaw
                                in.readFloat()); //pitch

                        chunks.put(uuid, loc);
                    }
                }

                for(UUID uuid : chunks.keySet()){
                    chunks.get(uuid).getChunk().setForceLoaded(true);

                    FakePlayer npc = new FakePlayer(chunks.get(uuid), "ChunkLoader");
                    npcList.add(npc);
                    chunkToNpc.put(chunks.get(uuid).getChunk(), npc);

                    if(dynmap != null){
                        markerSet = dynmap.getMarkerAPI().getMarkerSet("myeconomy");
                        if(markerSet == null){
                            markerSet = dynmap.getMarkerAPI().createMarkerSet("myeconomy", "chunkloaders", null, false);
                        }

                        markers.put(uuid, markerSet.createMarker(
                                uuid.toString(),
                                "ChunkLoader - "+Bukkit.getOfflinePlayer(uuid).getName(),
                                chunks.get(uuid).getWorld().getName(),
                                chunks.get(uuid).getX(),
                                chunks.get(uuid).getY(),
                                chunks.get(uuid).getZ(),
                                dynmap.getMarkerAPI().getMarkerIcon("offline"),
                                false));
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static boolean addChunkLoad(Player player){
        Location loc = player.getLocation();
        if(chunks.containsKey(player.getUniqueId()) && !chunkToNpc.containsKey(loc.getChunk())){
            return false;
        }

        chunks.put(player.getUniqueId(), loc);
        FakePlayer npc = new FakePlayer(loc, "ChunkLoader");
        npcList.add(npc);
        chunkToNpc.put(loc.getChunk(), npc);

        write();
        loc.getChunk().setForceLoaded(true);

        npc.display(player);

        if(dynmap != null){
            markerSet = dynmap.getMarkerAPI().getMarkerSet("myeconomy");
            if(markerSet == null){
                markerSet = dynmap.getMarkerAPI().createMarkerSet("myeconomy", "chunkloaders", null, false);
            }

            markers.put(player.getUniqueId(), markerSet.createMarker(
                    player.getUniqueId().toString(),
                    "ChunkLoader - "+player.getName(),
                    loc.getWorld().getName(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    dynmap.getMarkerAPI().getMarkerIcon("offline"),
                    false));
        }

        return true;
    }

    public static void removeChunkLoad(Player player){
        if(!chunks.containsKey(player.getUniqueId())){
            return;
        }

        Chunk chunk = chunks.remove(player.getUniqueId()).getChunk();
        chunk.setForceLoaded(false);

        write();

        FakePlayer npc = chunkToNpc.remove(chunk);
        npcList.remove(npc);
        npc.kill();

        if(dynmap != null){
            markers.remove(player.getUniqueId()).deleteMarker();
        }
    }

    public static void checkDistanceFakePlayers(Player player, Location location){
        for(FakePlayer fakePlayer : npcList){
            int distance = (int) fakePlayer.getLocation().distance(location);
            if(distance > Bukkit.getViewDistance() && renderedEntities.get(player).contains(fakePlayer)){
                renderedEntities.get(player).remove(fakePlayer);

            }else if(distance < Bukkit.getViewDistance() && !renderedEntities.get(player).contains(fakePlayer)){
                renderedEntities.get(player).add(fakePlayer);
                fakePlayer.display(player);
            }
        }
    }

    public static void clearFakePlayers(){
        if(!npcList.isEmpty()){
            for(FakePlayer fakePlayer : npcList){
                fakePlayer.kill();
            }
        }

        npcList.clear();
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                if(!plugin.getDataFolder().exists()){
                    plugin.getDataFolder().mkdirs();
                }

                try{
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(plugin.getDataFolder()+File.separator+"chunk_loader.ser")));

                    for(UUID uuid : chunks.keySet()){
                        byte[] b = uuid.toString().getBytes();
                        out.writeInt(b.length);
                        out.write(b);

                        b = chunks.get(uuid).getWorld().getName().getBytes();
                        out.writeInt(b.length);
                        out.write(b);

                        out.writeDouble(chunks.get(uuid).getX());
                        out.writeDouble(chunks.get(uuid).getY());
                        out.writeDouble(chunks.get(uuid).getZ());
                        out.writeFloat(chunks.get(uuid).getYaw());
                        out.writeFloat(chunks.get(uuid).getPitch());
                    }

                    out.flush();
                    out.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
