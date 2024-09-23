package rs.v9.myeconomy.shop;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static HashMap<UUID, MyShop> shops = new HashMap<>();
    private static HashMap<UUID, HashMap<String, UUID>> playersShopsByName = new HashMap<>();
    protected static HashMap<UUID, MyShop> trading = new HashMap<>();

    public ShopHandler(){
        File groupFolder = new File(plugin.getDataFolder()+File.separator+"shop");
        if(groupFolder.exists()){
            for(File groupFile : groupFolder.listFiles()){
                MyShop shop = new MyShop(groupFile.getName());
            }
        }
    }

    public static void createShop(Player player, MyShop shop){
        shops.put(shop.getUUID(), shop);

        if(playersShopsByName.containsKey(player.getUniqueId())){
            playersShopsByName.get(player.getUniqueId()).put(shop.getName(), shop.getUUID());

        }else{
            HashMap<String, UUID> s = new HashMap<>();
            s.put(shop.getName(), shop.getUUID());
            playersShopsByName.put(player.getUniqueId(), s);
        }
    }

    public static void deleteShop(Player player, MyShop shop){
        shops.remove(playersShopsByName.get(player.getUniqueId()).remove(shop.getName()));
    }

    public static MyShop getShop(UUID uuid){
        return shops.get(uuid);
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
