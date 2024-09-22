package rs.v9.myeconomy.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.json.JSONObject;
import org.json.JSONTokener;
import rs.v9.myeconomy.group.Group;
import rs.v9.myeconomy.group.MyGroup;
import rs.v9.myeconomy.group.Zone;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static rs.v9.myeconomy.Config.getClaimCost;
import static rs.v9.myeconomy.Main.dynmap;
import static rs.v9.myeconomy.handlers.Colors.getColorRGB;
import static rs.v9.myeconomy.handlers.MapHandler.*;
import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.group.GroupHandler.*;

public class ClaimHandler {

    private static JSONObject claims = new JSONObject();
    private static HashMap<String, AreaMarker> markers = new HashMap<>();
    private static HashMap<UUID, AutoClaim> autoClaiming = new HashMap<>();
    private static MarkerSet markerSet;

    public ClaimHandler(){
        if(plugin.getDataFolder().exists()){
            try{
                File claimsFile = new File(plugin.getDataFolder()+File.separator+"claims.json");
                if(claimsFile.exists()){
                    claims = new JSONObject(new JSONTokener(new FileInputStream(claimsFile)));
                }

                if(dynmap != null){
                    initDynmap();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initDynmap(){
        markerSet = dynmap.getMarkerAPI().getMarkerSet("myeconomy");
        if(markerSet == null){
            markerSet = dynmap.getMarkerAPI().createMarkerSet("myeconomy", "claims", null, false);
        }

        System.out.println("Loading markers into Dynmap!");

        JSONObject claims = getClaims();

        Iterator<String> it = claims.keys();

        while(it.hasNext()){
            String key = it.next();
            String[] tokens = key.split("\\|");

            Chunk chunk = getServer().getWorld(tokens[0]).getChunkAt(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            String worldName = chunk.getWorld().getName();
            double[] xPoints = { chunk.getX()*16, (chunk.getX()*16)+16, (chunk.getX()*16)+16, chunk.getX()*16 };
            double[] zPoints = { chunk.getZ()*16, chunk.getZ()*16, (chunk.getZ()*16)+16, (chunk.getZ()*16)+16 };
            UUID uuid = UUID.fromString(claims.getJSONObject(key).getString("k"));

            if(claims.getJSONObject(key).getInt("t") > 0){
                Zone zone = getZoneByUUID(uuid);

                if(zone != null){
                    AreaMarker areaMarker = markerSet.createAreaMarker(
                            "myeconomy|"+key,
                            zone.getName(),
                            false,
                            worldName,
                            xPoints,
                            zPoints,
                            false
                    );

                    areaMarker.setLineStyle(1, 1.0, getColorRGB(zone.getColor()).asRGB());
                    areaMarker.setFillStyle(0.5, getColorRGB(zone.getColor()).asRGB());
                    markers.put(key, areaMarker);
                }

            }else{
                MyGroup group = getGroupFromUUID(uuid);

                if(group != null){
                    AreaMarker areaMarker = markerSet.createAreaMarker(
                            "myeconomy|"+key,
                            group.getName(),
                            false,
                            worldName,
                            xPoints,
                            zPoints,
                            false
                    );

                    areaMarker.setLineStyle(1, 1.0, getColorRGB(group.getColor()).asRGB());
                    areaMarker.setFillStyle(0.5, getColorRGB(group.getColor()).asRGB());
                    markers.put(key, areaMarker);
                }
            }
        }
    }

    public static boolean claimChunk(Player player, Chunk chunk, Group group){
        if(group.canClaim(player.getUniqueId())){
            String key = getChunkName(chunk);

            if(group.getType() > 0){
                if(claimForZone(player, key, (Zone) group)){
                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }

                    if(dynmap != null){
                        AreaMarker marker = markers.remove(key);
                        if(marker != null){
                            marker.deleteMarker();
                        }

                        double[] xPoints = { chunk.getX()*16, (chunk.getX()*16)+16, (chunk.getX()*16)+16, chunk.getX()*16 };
                        double[] zPoints = { chunk.getZ()*16, chunk.getZ()*16, (chunk.getZ()*16)+16, (chunk.getZ()*16)+16 };

                        AreaMarker areaMarker = markerSet.createAreaMarker(
                                "myeconomy|"+key,
                                group.getName(),
                                false,
                                chunk.getWorld().getName(),
                                xPoints,
                                zPoints,
                                false
                        );

                        areaMarker.setLineStyle(1, 1.0, getColorRGB(group.getColor()).asRGB());
                        areaMarker.setFillStyle(0.5, getColorRGB(group.getColor()).asRGB());
                        markers.put(key, areaMarker);
                    }
                    return true;
                }

            }else{
                if(claimForGroup(player, key, (MyGroup) group)){
                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }

                    if(dynmap != null){
                        AreaMarker marker = markers.remove(key);
                        if(marker != null){
                            marker.deleteMarker();
                        }

                        double[] xPoints = { chunk.getX()*16, (chunk.getX()*16)+16, (chunk.getX()*16)+16, chunk.getX()*16 };
                        double[] zPoints = { chunk.getZ()*16, chunk.getZ()*16, (chunk.getZ()*16)+16, (chunk.getZ()*16)+16 };

                        AreaMarker areaMarker = markerSet.createAreaMarker(
                                "myeconomy|"+key,
                                group.getName(),
                                false,
                                chunk.getWorld().getName(),
                                xPoints,
                                zPoints,
                                false
                        );

                        areaMarker.setLineStyle(1, 1.0, getColorRGB(group.getColor()).asRGB());
                        areaMarker.setFillStyle(0.5, getColorRGB(group.getColor()).asRGB());
                        markers.put(key, areaMarker);
                    }
                    return true;
                }
            }
        }else{
            player.sendMessage("§cYou must be at least a group admin to claim land.");
        }
        return false;
    }

    private static boolean claimForGroup(Player player, String key, MyGroup group){
        if(group.getPower() > 0){
            if(claims.has(key)){
                Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
                //Claim claim = claims.get(key);
                if(claim.getType() == 0){
                    Group claimedGroup = getGroupFromUUID(claim.getKey());
                    if(claimedGroup != null){
                        if(claimedGroup.getType() == 0){
                            if(!claimedGroup.getKey().equals(group.getKey())){
                                if(((MyGroup) claimedGroup).getPower() < 0){
                                    claims.getJSONObject(key).put("k", group.getKey().toString());
                                    claims.getJSONObject(key).put("t", group.getType());
                                    group.setPower(group.getPower()-getClaimCost());
                                    ((MyGroup) claimedGroup).setPower(((MyGroup) claimedGroup).getPower()+getClaimCost());
                                    write();
                                    player.sendMessage("§7You have over §aclaimed§7 the chunk from "+claimedGroup.getName()+".");
                                    return true;

                                }else{
                                    player.sendMessage("§cAnother group owns this chunk and is stron enough to keep it.");
                                }
                            }else{
                                player.sendMessage("§cYour group already claimed this chunk.");
                            }
                        }else{
                            player.sendMessage("§cThis chunk is claimed by a zone, you cannot over claim this chunk.");
                        }
                    }else{
                        claims.getJSONObject(key).put("k", group.getKey().toString());
                        claims.getJSONObject(key).put("t", group.getType());
                        group.setPower(group.getPower()-getClaimCost());
                        write();
                        player.sendMessage("§7You have §aclaimed§7 this chunk for "+group.getName()+".");
                        return true;
                    }
                }else{
                    player.sendMessage("§cThis chunk is claimed by a zone, you cannot over claim this chunk.");
                }
            }else{
                JSONObject jclaim = new JSONObject();
                jclaim.put("k", group.getKey().toString());
                jclaim.put("t", group.getType());
                claims.put(key, jclaim);
                group.setPower(group.getPower()-getClaimCost());
                write();
                player.sendMessage("§7You have §aclaimed§7 this chunk for "+group.getName()+".");
                return true;
            }
        }else{
            player.sendMessage("§cYour group doesn't have enough power to claim.");
        }
        return false;
    }


    private static boolean claimForZone(Player player, String key, Zone zone){
        if(claims.has(key)){
            Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
            //Claim claim = claims.get(key);
            if(claim.getType() != zone.getType()){
                Group claimedGroup = getGroupFromUUID(claim.getKey());
                if(claimedGroup != null){
                    if(claimedGroup.getType() == 0){
                        ((MyGroup) claimedGroup).setPower(((MyGroup) claimedGroup).getPower()+getClaimCost());
                    }
                }

                claims.getJSONObject(key).put("k", zone.getKey().toString());
                claims.getJSONObject(key).put("t", zone.getType());

                write();
                player.sendMessage("§7You have §aclaimed§7 this chunk for "+zone.getName()+".");
                return true;
            }
        }else{
            JSONObject jclaim = new JSONObject();
            jclaim.put("k", zone.getKey().toString());
            jclaim.put("t", zone.getType());
            claims.put(key, jclaim);

            write();
            player.sendMessage("§7You have §aclaimed§7 this chunk for "+zone.getName()+".");
            return true;
        }
        return false;
    }

    public static boolean autoClaimChunk(Player player, Chunk chunk){
        if(autoClaiming.containsKey(player.getUniqueId())){
            AutoClaim autoClaim = autoClaiming.get(player.getUniqueId());

            String key = getChunkName(chunk);
            if(autoClaim.getLastLocation() == null || !autoClaim.getLastLocation().equals(key)){
                autoClaim.setLastLocation(key);

                if(autoClaim.isClaiming()){
                    return claimChunk(player, chunk, autoClaim.getGroup());
                }else{
                    return unclaimChunk(player, chunk, autoClaim.getGroup());
                }
            }
        }
        return false;
    }

    public static boolean unclaimChunk(Player player, Chunk chunk, Group group){
        if(group.canClaim(player.getUniqueId())){
            String key = getChunkName(chunk);

            if(claims.has(key)){
                Claim claim = new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
                //Claim claim = claims.get(key);
                if(claim.getType() == group.getType() && claim.getKey().equals(group.getKey())){
                    claims.remove(key);

                    //AREA MARKER
                    if(dynmap != null){
                        AreaMarker marker = markers.remove(key);
                        if(marker != null){
                            marker.deleteMarker();
                        }
                    }

                    if(group.getType() == 0){
                        ((MyGroup) group).setPower(((MyGroup) group).getPower()+getClaimCost());
                    }
                    write();

                    if(isMapping(player.getUniqueId())){
                        removeMappedChunk(player.getUniqueId(), chunk);
                        mapLandscape(player, chunk);
                    }

                    player.sendMessage("§7You have §cunclaimed§7 this chunk for "+group.getName()+".");
                    return true;

                }else{
                    player.sendMessage("§cYour group doesn't own this chunk.");
                }
            }else{
                player.sendMessage("§cThis chunk isn't claimed by any group.");
            }
        }else{
            player.sendMessage("§cYou must be at least a group admin to unclaim land.");
        }
        return false;
    }

    public static boolean inClaim(Chunk chunk){
        String key = getChunkName(chunk);
        return claims.has(key);
    }

    public static Claim getClaim(Chunk chunk){
        String key = getChunkName(chunk);
        if(claims.has(key)){
            return new Claim(UUID.fromString(claims.getJSONObject(key).getString("k")), claims.getJSONObject(key).getInt("t"));
            //return claims.get(key);
        }
        return null;
    }

    public static void startAutoClaiming(UUID uuid, Group group, boolean claiming){
        if(!autoClaiming.containsKey(uuid)){
            autoClaiming.put(uuid, new AutoClaim(group, claiming, null));
        }
    }

    public static AutoClaim getAutoClaim(UUID uuid){
        if(autoClaiming.containsKey(uuid)){
            return autoClaiming.get(uuid);
        }
        return null;
    }

    public static boolean isAutoClaiming(UUID uuid){
        return autoClaiming.containsKey(uuid);
    }

    public static void stopAutoClaiming(UUID uuid){
        if(autoClaiming.containsKey(uuid)){
            autoClaiming.remove(uuid);
        }
    }

    public static void unclaimAllForGroup(UUID uuid){
        if(claims.length() > 0){
            for(int i = claims.names().length()-1; i > -1; i--){
                String key = claims.names().getString(i);
                if(claims.getJSONObject(claims.names().getString(i)).getString("k").equals(uuid.toString())){
                    claims.remove(key);

                    //AREA MARKER
                    if(dynmap != null){
                        AreaMarker marker = markers.remove(key);
                        if(marker != null){
                            marker.deleteMarker();
                        }
                    }
                }
            }

            write();
        }
    }

    public static JSONObject getClaims(){
        return claims;
    }




    private static String getChunkName(Chunk chunk){
        /*
        try{
            String key = chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            return messageDigest.digest(key.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
        */
        return chunk.getWorld().getName()+"|"+chunk.getX()+"|"+chunk.getZ();
        //return key.getBytes();
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                if(!plugin.getDataFolder().exists()){
                    plugin.getDataFolder().mkdirs();
                }

                try{
                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"claims.json"));
                    out.write(claims.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
