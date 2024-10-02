package rs.v9.myeconomy.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rs.v9.myeconomy.holo.FakePlayer;

import java.util.ArrayList;
import java.util.List;

import static rs.v9.myeconomy.Config.renderedEntities;

public class LoaderHandler {

    private static List<FakePlayer> npcList = new ArrayList<>();

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
