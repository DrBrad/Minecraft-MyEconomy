package rs.v9.myeconomy.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Main.plugin;

public class PlayerResolver {

    //private static JSONObject players = new JSONObject();
    private static HashMap<String, UUID> players = new HashMap<>();

    public PlayerResolver(){
        if(plugin.getDataFolder().exists()){
            try{
                File playersFile = new File(plugin.getDataFolder()+File.separator+"player_resolver.ser");
                if(playersFile.exists()){
                    DataInputStream in = new DataInputStream(new FileInputStream(playersFile));

                    while(in.available() > 0){
                        byte[] b = new byte[in.readInt()];
                        in.read(b);

                        String key = new String(b);

                        b = new byte[in.readInt()];
                        in.read(b);
                        UUID value = UUID.fromString(new String(b));

                        players.put(key, value);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void setPlayer(String name, UUID uuid){
        players.put(name, uuid);
        write();
    }

    public static OfflinePlayer getPlayer(String name){
        if(players.containsKey(name)){
            return Bukkit.getOfflinePlayer(players.get(name));
        }
        return null;
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
                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"player_resolver.json"));
                    out.write(players.toString());
                    out.flush();
                    out.close();
                    */

                    DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(plugin.getDataFolder()+File.separator+"player_resolver.ser")));

                    for(String key : players.keySet()){
                        byte[] b = key.getBytes();
                        out.writeInt(b.length);
                        out.write(b);

                        b = players.get(key).toString().getBytes();
                        out.writeInt(b.length);
                        out.write(b);
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
