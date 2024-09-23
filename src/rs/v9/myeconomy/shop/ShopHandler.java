package rs.v9.myeconomy.shop;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static rs.v9.myeconomy.Main.plugin;

public class ShopHandler {

    private static HashMap<UUID, MyShop> shops = new HashMap<>();

    public ShopHandler(){
        File groupFolder = new File(plugin.getDataFolder()+File.separator+"shop");
        if(groupFolder.exists()){
            for(File groupFile : groupFolder.listFiles()){
                MyShop shop = new MyShop(groupFile.getName());
            }
        }
    }

    public static void createShop(UUID uuid, MyShop shop){
        System.out.println("ADDING");
        shops.put(uuid, shop);
    }

    public static boolean isShop(UUID uuid){
        return shops.containsKey(uuid);
    }

    public static MyShop getShop(UUID uuid){
        System.out.println("TOTAL: "+shops.size()+"   "+uuid.toString());
        return shops.get(uuid);
    }

}
