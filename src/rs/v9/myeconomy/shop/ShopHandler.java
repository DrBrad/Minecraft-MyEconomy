package rs.v9.myeconomy.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Config.getMaxShops;
import static rs.v9.myeconomy.Main.dynmap;
import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static HashMap<UUID, MyShop> shops = new HashMap<>();
    private static HashMap<UUID, HashMap<String, UUID>> playersShopsByName = new HashMap<>();
    private static HashMap<UUID, UUID> shopsByEntityUUID = new HashMap<>();;
    protected static HashMap<UUID, UUID> trading = new HashMap<>();
    protected static HashMap<Inventory, UUID> inventories = new HashMap<>();
    private static HashMap<UUID, Marker> markers = new HashMap<>();
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
        shopsByEntityUUID.put(shop.getEntityUUID(), shop.getKey());

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

            markers.put(shop.getKey(), markerSet.createMarker(shop.getKey().toString(),
                    "Shop - "+shop.getName(),
                    shop.getLocation().getWorld().getName(),
                    shop.getLocation().getX(),
                    shop.getLocation().getY(),
                    shop.getLocation().getZ(),
                    dynmap.getMarkerAPI().getMarkerIcon("building"),
                    false));
        }
    }

    public static void deleteShop(Player player, MyShop shop){
        shopsByEntityUUID.remove(shop.getEntityUUID());
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
}
