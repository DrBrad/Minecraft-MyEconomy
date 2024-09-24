package rs.v9.myeconomy.shop;

import org.bukkit.entity.Player;
import org.dynmap.markers.MarkerSet;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Main.dynmap;
import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static HashMap<UUID, MyShop> shops = new HashMap<>();
    private static HashMap<UUID, HashMap<String, UUID>> playersShopsByName = new HashMap<>();
    private static HashMap<UUID, UUID> shopsByEntityUUID = new HashMap<>();
    protected static HashMap<UUID, MyShop> trading = new HashMap<>();
    private static MarkerSet markerSet;

    public ShopHandler(){
        File groupFolder = new File(plugin.getDataFolder()+File.separator+"shop");
        if(groupFolder.exists()){
            for(File groupFile : groupFolder.listFiles()){
                MyShop shop = new MyShop(groupFile.getName());
            }
        }

        if(dynmap != null){
            initDynmap();
        }
    }

    public static void createShop(Player player, MyShop shop){
        shops.put(shop.getKey(), shop);
        shopsByEntityUUID.put(shop.getEntityUUID(), shop.getKey());

        if(playersShopsByName.containsKey(player.getUniqueId())){
            playersShopsByName.get(player.getUniqueId()).put(shop.getName(), shop.getKey());

        }else{
            HashMap<String, UUID> s = new HashMap<>();
            s.put(shop.getName(), shop.getKey());
            playersShopsByName.put(player.getUniqueId(), s);
        }

        if(dynmap != null){
            markerSet.createMarker(shop.getKey().toString(),
                    "Shop",
                    player.getLocation().getWorld().getName(),
                    player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    dynmap.getMarkerAPI().getMarkerIcon("building"),
                    false);
        }
    }

    private void initDynmap(){
        markerSet = dynmap.getMarkerAPI().getMarkerSet("myeconomy");
        if(markerSet == null){
            markerSet = dynmap.getMarkerAPI().createMarkerSet("myeconomy", "shops", null, false);
        }
    }

    public static void deleteShop(Player player, MyShop shop){
        shopsByEntityUUID.remove(shop.getEntityUUID());
        shops.remove(playersShopsByName.get(player.getUniqueId()).remove(shop.getName()));
    }

    public static MyShop getShop(UUID uuid){
        return shops.get(uuid);
    }

    public static MyShop getShopByEntityUUID(UUID uuid){
        if(!shopsByEntityUUID.containsKey(uuid)){
            return null;
        }

        return shops.get(shopsByEntityUUID.get(uuid));
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
        return trading.get(player.getUniqueId());
    }

    public static void removeTrader(Player player){
        trading.remove(player.getUniqueId());
    }
}
