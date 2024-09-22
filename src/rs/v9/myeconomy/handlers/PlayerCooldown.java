package rs.v9.myeconomy.handlers;

import org.bukkit.Bukkit;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Main.plugin;

public class PlayerCooldown {

    private static HashMap<UUID, Long> players = new HashMap<>();
    //private static JSONObject players = new JSONObject();

    public PlayerCooldown(){
        if(plugin.getDataFolder().exists()){
            try{
                File playersFile = new File(plugin.getDataFolder()+File.separator+"player_cooldown.json");
                if(playersFile.exists()){
                    JSONObject players = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));

                    for(String key : players.keySet()){
                        this.players.put(UUID.fromString(key), players.getLong(key));
                    }

                    write();


                    /*
                    DataInputStream in = new DataInputStream(new FileInputStream(playersFile));
                    while(in.available() > 0){
                        byte[] b = new byte[in.readInt()];
                        in.read(b);

                        UUID key = UUID.fromString(new String(b));

                        long value = in.readLong();

                        players.put(key, value);
                    }*/
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setPlayerCooldown(UUID uuid){
        players.put(uuid, new Date().getTime());
        write();
    }

    public static long getPlayerCooldown(UUID uuid){
        if(players.containsKey(uuid)){
            return players.get(uuid);
        }
        return 0;
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    if(!plugin.getDataFolder().exists()){
                        plugin.getDataFolder().mkdirs();
                    }

                    /*
                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"player_cooldown.json"));
                    out.write(players.toString());
                    out.flush();
                    out.close();
                    */

                    DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(plugin.getDataFolder()+File.separator+"player_cooldown.ser")));

                    for(UUID key : players.keySet()){
                        byte[] b = key.toString().getBytes();
                        out.writeInt(b.length);
                        out.write(b);

                        out.writeLong(players.get(key));
                    }


                    //FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"players.json"));
                    //out.write(players.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
