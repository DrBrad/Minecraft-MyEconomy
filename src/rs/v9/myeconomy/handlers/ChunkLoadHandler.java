package rs.v9.myeconomy.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rs.v9.myeconomy.claim.Claim;
import rs.v9.myeconomy.claim.Flags;
import rs.v9.myeconomy.holo.FakePlayer;

import java.util.ArrayList;
import java.util.List;

import static rs.v9.myeconomy.Config.renderedEntities;
import static rs.v9.myeconomy.Main.plugin;

public class ChunkLoadHandler {

    private static List<FakePlayer> npcList = new ArrayList<>();

    public ChunkLoadHandler(){
        if(plugin.getDataFolder().exists()){
            try{
                /*
                File claimsFile = new File(plugin.getDataFolder()+File.separator+"claims.ser");
                if(claimsFile.exists()){
                    DataInputStream in = new DataInputStream(new FileInputStream(claimsFile));

                    while(in.available() > 0){
                        byte[] b = new byte[in.readInt()];
                        in.read(b);

                        String key = new String(b);

                        b = new byte[in.readInt()];
                        in.read(b);
                        UUID uuid = UUID.fromString(new String(b));

                        int type = in.readByte();

                        List<Flags> flags = new ArrayList<>();
                        int totalFlags = in.readByte();
                        for(int i = 0; i < totalFlags; i++){
                            Flags flag = Flags.fromByteValue(in.readByte());
                            if(flag != Flags.NONE){
                                flags.add(flag);
                            }
                        }

                        claims.put(key, new Claim(uuid, type, flags));
                    }
                }

                if(dynmap != null){
                    initDynmap();
                }
                */
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void addFakePlayer(Player player){
        FakePlayer npc = new FakePlayer(player.getLocation(), "ChunkLoader");
        npcList.add(npc);
        npc.display(player);
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
}
