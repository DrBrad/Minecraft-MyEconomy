package rs.v9.myeconomy.shop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.shop.ShopHandler.inventories;
import static rs.v9.myeconomy.shop.ShopHandler.trading;

public class MyShop {

    private UUID key;
    private String name;
    private Merchant merchant;
    private Inventory stock, received;
    private EntityType type;
    private Location location;
    private LivingEntity entity;

    public MyShop(){
    }

    public MyShop(String key){
        read(key);
    }

    public void openMerchant(Player player){
        if(merchant.isTrading()){
            return;
        }

        Map<Material, Integer> mats = new HashMap<>();
        for(int i = 0; i < stock.getSize(); i++){
            if(stock.getItem(i) == null || stock.getItem(i).getType().isAir()){
                continue;
            }

            if(mats.containsKey(stock.getItem(i).getType())){
                mats.put(stock.getItem(i).getType(), mats.get(stock.getItem(i).getType())+stock.getItem(i).getAmount());
                continue;
            }

            mats.put(stock.getItem(i).getType(), stock.getItem(i).getAmount());
        }

        for(MerchantRecipe recipe : merchant.getRecipes()){
            recipe.setUses(0);

            if(!mats.containsKey(recipe.getResult().getType()) || mats.get(recipe.getResult().getType()) < recipe.getResult().getAmount()){
                recipe.setMaxUses(0);
                continue;
            }

            recipe.setMaxUses(mats.get(recipe.getResult().getType())/recipe.getResult().getAmount());
        }

        trading.put(player.getUniqueId(), this);
        player.openMerchant(merchant, true);
    }

    public void openStock(Player player){
        if(merchant.isTrading()){
            return;
        }

        player.openInventory(stock);
    }

    public void openReceive(Player player){
        player.openInventory(received);
    }

    public void addTrade(ItemStack receive, ItemStack give){
        MerchantRecipe recipe = new MerchantRecipe(receive, 0);
        recipe.setExperienceReward(false);
        recipe.addIngredient(give);

        List<MerchantRecipe> recipes = new ArrayList<>();
        if(merchant.getRecipeCount() > 0){
            recipes.addAll(merchant.getRecipes());
        }

        recipes.add(recipe);
        merchant.setRecipes(recipes);

        writeTrades();
    }

    public void removeTrade(int i){
        if(merchant.getRecipeCount() < 1){
            return;
        }

        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        recipes.remove(i);
        merchant.setRecipes(recipes);
        writeTrades();
    }

    public void notifyTrade(MerchantRecipe recipe){
        int total = (recipe.getMaxUses()-recipe.getUses())*recipe.getResult().getAmount();
        int count = 0;

        for(int i = 0; i < stock.getSize(); i++){
            if(stock.getItem(i) == null || stock.getItem(i).getType().isAir()){
                continue;
            }

            if(stock.getItem(i).getType().equals(recipe.getResult().getType())){
                count += stock.getItem(i).getAmount();
            }
        }

        if(count > total){
            recipe.setMaxUses(recipe.getMaxUses()-recipe.getUses());
            recipe.setUses(0);
            stock.removeItem(new ItemStack(recipe.getResult().getType(), count-total));

            for(ItemStack item : recipe.getIngredients()){
                received.addItem(new ItemStack(item.getType(), count-total));
            }
        }

        writeStock();
        writeReceived();
    }

    public void notifyStorage(Inventory inventory){
        if(inventory == stock){
            writeStock();
            return;
        }

        if(inventory == received){
            writeReceived();
        }
    }

    public UUID getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

    public UUID getEntityUUID(){
        return entity.getUniqueId();
    }

    public MyShop create(String name, Location location, EntityType type){
        this.name = name;
        this.location = location;
        this.type = type;

        key = UUID.randomUUID();

        merchant = Bukkit.createMerchant("Shop");
        stock = Bukkit.createInventory(null, 36, "Stock");
        inventories.put(stock, key);
        received = Bukkit.createInventory(null, 36, "Received");
        inventories.put(received, key);

        spawn();
        writeData();

        return this;
    }

    public void spawn(){
        entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
        if(entity instanceof Ageable){
            ((Ageable) entity).setAdult();
        }
        if(entity.getEquipment() != null){
            entity.getEquipment().setHelmet(null);
            entity.getEquipment().setChestplate(null);
            entity.getEquipment().setLeggings(null);
            entity.getEquipment().setBoots(null);
        }
        entity.setCustomName(name);
        entity.setInvulnerable(true);
        entity.setPersistent(true);
        entity.setVisualFire(false);
        entity.setAI(false);
        entity.setGravity(false);
        entity.setCanPickupItems(false);
        entity.setInvisible(false);
        entity.setSilent(true);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        entity.setFreezeTicks(0);
    }

    public boolean delete(){
        if(!stock.isEmpty() || !received.isEmpty() || merchant.isTrading()){
            return false;
        }

        inventories.remove(stock);
        inventories.remove(received);

        entity.setInvulnerable(false);
        entity.remove();

        return true;
    }

    public void read(String key){
        try{
            /*
            File groupFolder = new File(plugin.getDataFolder()+File.separator+"group"+File.separator+key);

            if(groupFolder.exists()){
                File data = new File(groupFolder.getPath()+File.separator+"data.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(data);

                this.key = UUID.fromString(config.getString("key"));
                name = config.getString("name");
                color = config.getInt("color");
                power = config.getInt("power");
                description = config.getString("description");

                if(config.contains("home")){
                    String world = config.getString("home.world");
                    double x = config.getDouble("home.x");
                    double y = config.getDouble("home.y");
                    double z = config.getDouble("home.z");
                    float yaw = (float) config.getDouble("home.yaw");
                    float pitch = (float) config.getDouble("home.pitch");
                    home = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                }

                //READ ALL THE RANKS
                File ranksFile = new File(groupFolder.getPath()+File.separator+"ranks.json");
                if(ranksFile.exists()){
                    ranks = new JSONObject(new JSONTokener(new FileInputStream(ranksFile)));
                }

                //READ ALL THE WARPS
                File warpsFile = new File(groupFolder.getPath()+File.separator+"warps.yml");
                config = YamlConfiguration.loadConfiguration(warpsFile);

                for(String warpKey : config.getKeys(false)){
                    Location location = new Location(Bukkit.getWorld(config.getString(warpKey+".world")),
                            config.getDouble(warpKey+".x"),
                            config.getDouble(warpKey+".y"),
                            config.getDouble(warpKey+".z"),
                            (float)config.getDouble(warpKey+".yaw"),
                            (float)config.getDouble(warpKey+".pitch"));

                    warps.put(warpKey, location);
                }
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeData(){
        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key.toString());
        if(!shopFolder.exists()){
            shopFolder.mkdirs();
        }

        try{
            File warpsFile = new File(shopFolder.getPath()+File.separator+"config.yml");
            if(warpsFile.exists()){
                OutputStream out = new FileOutputStream(warpsFile);
                out.flush();
                out.close();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(warpsFile);
            config.set("key", key.toString());
            config.set("entity.uuid", entity.getUniqueId().toString());
            config.set("entity.type", entity.getType());
            config.set("name", name);

            config.set("location.world", location.getWorld().getName());
            config.set("location.x", location.getX());
            config.set("location.y", location.getY());
            config.set("location.z", location.getZ());
            config.set("location.yaw", location.getYaw());
            config.set("location.pitch", location.getPitch());

            config.save(warpsFile);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeTrades(){
        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key.toString());
        if(!shopFolder.exists()){
            shopFolder.mkdirs();
        }

        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(shopFolder+File.separator+"recipes.ser")));

            for(MerchantRecipe recipe : merchant.getRecipes()){
                for(ItemStack item : recipe.getIngredients()){
                    byte[] b = item.getType().name().getBytes();
                    out.writeInt(b.length);
                    out.write(b);

                    out.writeInt(item.getAmount());
                }

                out.write("|".getBytes());

                byte[] b = recipe.getResult().getType().name().getBytes();
                out.writeInt(b.length);
                out.write(b);

                out.writeInt(recipe.getResult().getAmount());
            }

            out.flush();
            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeStock(){
        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key.toString());
        if(!shopFolder.exists()){
            shopFolder.mkdirs();
        }

        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(shopFolder+File.separator+"stock.ser")));

            for(int i = 0; i < stock.getSize(); i++){
                if(stock.getItem(i) == null || stock.getItem(i).getType().isAir()){
                    continue;
                }

                byte[] b = stock.getItem(i).getType().name().getBytes();
                out.writeInt(b.length);
                out.write(b);

                out.writeInt(stock.getItem(i).getAmount());
            }

            out.flush();
            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeReceived(){
        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key.toString());
        if(!shopFolder.exists()){
            shopFolder.mkdirs();
        }

        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(shopFolder+File.separator+"received.ser")));

            for(int i = 0; i < received.getSize(); i++){
                if(received.getItem(i) == null || received.getItem(i).getType().isAir()){
                    continue;
                }

                byte[] b = received.getItem(i).getType().name().getBytes();
                out.writeInt(b.length);
                out.write(b);

                out.writeInt(received.getItem(i).getAmount());
            }

            out.flush();
            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
