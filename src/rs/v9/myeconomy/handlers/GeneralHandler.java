package rs.v9.myeconomy.handlers;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static rs.v9.myeconomy.Config.*;
import static rs.v9.myeconomy.Main.plugin;

public class GeneralHandler {

    private static ArrayList<Player> teleport = new ArrayList<>();
    public static HashMap<Player, Location> lastTeleport = new HashMap();

    public static void teleport(Player player, Location location, String type, Color color){
        teleport.add(player);
        spawnCircle(player, player.getLocation(), color);
        player.sendMessage("§7Preparing to teleport to §c"+type+"§7 don't move!");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                if(teleport.contains(player)){
                    teleport.remove(player);
                    lastTeleport.put(player, player.getLocation());
                    player.teleport(location);
                }
            }
        }, getTeleportDelay());
    }

    public static boolean hasLastTeleport(Player player){
        return lastTeleport.containsKey(player);
    }

    public static Location getLastTeleport(Player player){
        if(lastTeleport.containsKey(player)){
            return lastTeleport.get(player);
        }
        return null;
    }

    public static void spawnCircle(Player player, Location location, Color color){
        Particle.DustOptions dust = new Particle.DustOptions(color, 1);
        double size = 1;
        int points = 20;

        for(double y = 0; y < 2.2; y+=(0.1)){
            for(int i = 0; i < 360; i += 360/points){
                double angle = (i * Math.PI/180);
                double x = size * Math.cos(angle);
                double z = size * Math.sin(angle);
                Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                loc.add(x, y, z);

                player.getWorld().spawnParticle(Particle.DUST, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 1, dust);
                //player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 1, dust);
            }
        }
    }
}
