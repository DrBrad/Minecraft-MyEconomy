package rs.v9.myeconomy.shop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import rs.v9.myeconomy.holo.FakeMob;

import java.io.File;
import java.util.*;

import static rs.v9.myeconomy.Config.getMaxShops;
import static rs.v9.myeconomy.Config.renderedEntities;
import static rs.v9.myeconomy.Main.dynmap;
import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static Map<UUID, MyShop> shops = new HashMap<>();
    private static Map<UUID, Map<String, UUID>> playersShopsByName = new HashMap<>();
    private static Map<Integer, UUID> shopsByEntityId = new HashMap<>();
    protected static Map<UUID, UUID> trading = new HashMap<>();
    protected static Map<Inventory, UUID> inventories = new HashMap<>();
    private static Map<UUID, Marker> markers = new HashMap<>();
    private static List<FakeMob> mobList = new ArrayList<>();
    private static MarkerSet markerSet;

    public ShopHandler(){
        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop");
        if(shopFolder.exists()){
            for(File groupFile : shopFolder.listFiles()){
                MyShop shop = new MyShop(groupFile.getName());
                createShop(shop);
            }
        }
    }

    public static void createShop(MyShop shop){
        shops.put(shop.getKey(), shop);
        shopsByEntityId.put(shop.getFakeMob().getEntityId(), shop.getKey());
        mobList.add(shop.getFakeMob());

        if(playersShopsByName.containsKey(shop.getPlayerUUID())){
            playersShopsByName.get(shop.getPlayerUUID()).put(shop.getName(), shop.getKey());

        }else{
            HashMap<String, UUID> s = new HashMap<>();
            s.put(shop.getName(), shop.getKey());
            playersShopsByName.put(shop.getPlayerUUID(), s);
        }

        if(dynmap != null){
            markerSet = dynmap.getMarkerAPI().getMarkerSet("myeconomy");
            if(markerSet == null){
                markerSet = dynmap.getMarkerAPI().createMarkerSet("myeconomy", "shops", null, false);
            }

            markers.put(shop.getKey(), markerSet.createMarker(
                    shop.getKey().toString(),
                    "Shop - "+shop.getName(),
                    shop.getFakeMob().getLocation().getWorld().getName(),
                    shop.getFakeMob().getLocation().getX(),
                    shop.getFakeMob().getLocation().getY(),
                    shop.getFakeMob().getLocation().getZ(),
                    dynmap.getMarkerAPI().getMarkerIcon("building"),
                    false));
        }
    }

    public static void deleteShop(Player player, MyShop shop){
        shopsByEntityId.remove(shop.getFakeMob().getEntityId());
        mobList.remove(shop.getFakeMob());
        shops.remove(playersShopsByName.get(player.getUniqueId()).remove(shop.getName()));

        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+shop.getKey());

        deleteFolder(shopFolder);

        if(dynmap != null){
            markers.remove(shop.getKey()).deleteMarker();
        }
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

    public static MyShop getShop(UUID uuid){
        return shops.get(uuid);
    }

    public static MyShop getShopByEntityId(int id){
        if(!shopsByEntityId.containsKey(id)){
            return null;
        }

        return shops.get(shopsByEntityId.get(id));
    }

    public static boolean hasShopName(Player player, String name){
        if(!playersShopsByName.containsKey(player.getUniqueId())){
            return false;
        }

        return playersShopsByName.get(player.getUniqueId()).containsKey(name);
    }

    public static MyShop getShopByName(Player player, String name){
        if(!playersShopsByName.containsKey(player.getUniqueId())){
            return null;
        }

        if(!playersShopsByName.get(player.getUniqueId()).containsKey(name)){
            return null;
        }

        if(!shops.containsKey(playersShopsByName.get(player.getUniqueId()).get(name))){
            return null;
        }

        return shops.get(playersShopsByName.get(player.getUniqueId()).get(name));
    }

    public static MyShop getShopByTrader(Player player){
        if(!trading.containsKey(player.getUniqueId())){
            return null;
        }

        return shops.get(trading.get(player.getUniqueId()));
    }

    public static void removeTrader(Player player){
        trading.remove(player.getUniqueId());
    }

    public static boolean isPlayerShopCapped(Player player){
        if(!playersShopsByName.containsKey(player.getUniqueId())){
            return false;
        }

        return (getMaxShops()-playersShopsByName.get(player.getUniqueId()).size() <= 0);
    }

    public static boolean containsInventory(Inventory inventory){
        return inventories.containsKey(inventory);
    }

    public static MyShop getInventory(Inventory inventory){
        if(!inventories.containsKey(inventory)){
            return null;
        }

        return shops.get(inventories.get(inventory));
    }

    public static List<MyShop> getPlayersShops(Player player){
        List<MyShop> r = new ArrayList<>();

        if(playersShopsByName.containsKey(player.getUniqueId())){
            for(String shop : playersShopsByName.get(player.getUniqueId()).keySet()){
                r.add(shops.get(playersShopsByName.get(player.getUniqueId()).get(shop)));
            }
        }

        return r;
    }

    public static void checkDistanceFakeMobs(Player player, Location location){
        for(FakeMob fakeMob : mobList){
            if(!fakeMob.getLocation().getWorld().equals(location.getWorld())){
                renderedEntities.get(player).remove(fakeMob);
                continue;
            }

            int distance = (int) fakeMob.getLocation().distance(location);
            if(distance > Bukkit.getViewDistance() && renderedEntities.get(player).contains(fakeMob)){
                renderedEntities.get(player).remove(fakeMob);

            }else if(distance < Bukkit.getViewDistance() && !renderedEntities.get(player).contains(fakeMob)){
                renderedEntities.get(player).add(fakeMob);
                fakeMob.display(player);
            }
        }
    }

    public static void clearFakeMobs(){
        if(!mobList.isEmpty()){
            for(FakeMob fakeMob : mobList){
                fakeMob.kill();
            }
        }

        mobList.clear();
    }
}
