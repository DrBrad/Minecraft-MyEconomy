package rs.v9.myeconomy.shop;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static HashMap<UUID, MyShop> shops = new HashMap<>();
    private static HashMap<UUID, HashMap<String, UUID>> playersShopsByName = new HashMap<>();
    //private static HashMap<String, List<UUID>> playersShopsByName = new HashMap<>();

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

    public static boolean isShop(UUID uuid){
        return shops.containsKey(uuid);
    }

    public static MyShop getShop(UUID uuid){
        System.out.println("TOTAL: "+shops.size()+"   "+uuid.toString());
        return shops.get(uuid);
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
        //return shops.get(shops.keySet().toArray()[0]);
    }
}
