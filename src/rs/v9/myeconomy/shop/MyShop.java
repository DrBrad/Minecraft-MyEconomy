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

import java.io.*;
import java.util.*;

import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.shop.ShopHandler.*;

public class MyShop {

    private UUID key, playerUUID, entityUUID;
    private String name;
    private Merchant merchant;
    private Inventory stock, received;
    private EntityType entityType;
    private Location location;

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

        trading.put(player.getUniqueId(), key);
        player.openMerchant(merchant, true);
    }

    public void openStock(Player player){
        if(merchant.isTrading() || !player.getUniqueId().equals(playerUUID)){
            return;
        }

        player.openInventory(stock);
    }

    public void openReceive(Player player){
        if(!player.getUniqueId().equals(playerUUID)){
            return;
        }

        player.openInventory(received);
    }

    public boolean addTrade(ItemStack receive, ItemStack give){
        if(receive.getType().isAir() || give.getType().isAir() || merchant.getRecipeCount() >= 16){
            return false;
        }

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
        return true;
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

            for(MerchantRecipe r : merchant.getRecipes()){
                if(r.equals(recipe) || !r.getResult().getType().equals(recipe.getResult().getType())){
                    continue;
                }

                r.setMaxUses(total/r.getResult().getAmount());
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
        return entityUUID;
    }

    public MyShop create(Player player, String name, EntityType entityType){
        this.name = name;
        this.location = player.getLocation();
        this.entityType = entityType;

        key = UUID.randomUUID();
        playerUUID = player.getUniqueId();

        merchant = Bukkit.createMerchant("Shop");
        stock = Bukkit.createInventory(null, 36, "Stock");
        inventories.put(stock, key);
        received = Bukkit.createInventory(null, 36, "Received");
        inventories.put(received, key);

        spawn();

        return this;
    }

    public void spawn(){
        if(!location.getWorld().isChunkLoaded(location.getChunk())){
            location.getWorld().loadChunk(location.getChunk());
        }

        Entity entity = Bukkit.getServer().getEntity(entityUUID);
        if(entity != null && entity.isValid()){
            return;
        }

        LivingEntity livingEntity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entityUUID = livingEntity.getUniqueId();
        if(livingEntity instanceof Ageable){
            ((Ageable) livingEntity).setAdult();
        }
        if(livingEntity.getEquipment() != null){
            livingEntity.getEquipment().setHelmet(null);
            livingEntity.getEquipment().setChestplate(null);
            livingEntity.getEquipment().setLeggings(null);
            livingEntity.getEquipment().setBoots(null);
        }
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCustomName(name);
        livingEntity.setCustomNameVisible(true);
        livingEntity.setInvulnerable(true);
        livingEntity.setPersistent(true);
        livingEntity.setVisualFire(false);
        livingEntity.setAI(false);
        livingEntity.setGravity(false);
        livingEntity.setCanPickupItems(false);
        livingEntity.setInvisible(false);
        livingEntity.setSilent(true);
        livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        livingEntity.setFreezeTicks(0);
        writeData();
    }

    public Location getLocation(){
        return location;
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

    public boolean delete(){
        if(!stock.isEmpty() || !received.isEmpty() || merchant.isTrading()){
            return false;
        }

        inventories.remove(stock);
        inventories.remove(received);

        Entity entity = Bukkit.getServer().getEntity(entityUUID);
        if(entity != null && entity.isValid()){
            entity.remove();
        }

        File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key.toString());
        if(shopFolder.exists()){
            shopFolder.mkdirs();
        }

        return true;
    }

    public void read(String key){
        try{
            File shopFolder = new File(plugin.getDataFolder()+File.separator+"shop"+File.separator+key);

            if(!shopFolder.exists()){
                return;
            }

            File configFile = new File(shopFolder.getPath()+File.separator+"data.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            this.key = UUID.fromString(config.getString("key"));
            playerUUID = UUID.fromString(config.getString("player.uuid"));
            entityUUID = UUID.fromString(config.getString("entity.uuid"));
            name = config.getString("name");
            entityType = EntityType.valueOf(config.getString("entity.type"));

            String world = config.getString("location.world");
            double x = config.getDouble("location.x");
            double y = config.getDouble("location.y");
            double z = config.getDouble("location.z");
            float yaw = (float) config.getDouble("location.yaw");
            float pitch = (float) config.getDouble("location.pitch");

            location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

            spawn();

            merchant = Bukkit.createMerchant("Shop");

            File recipesFile = new File(shopFolder+File.separator+"recipes.ser");
            if(recipesFile.exists()){
                DataInputStream in = new DataInputStream(new FileInputStream(recipesFile));

                List<MerchantRecipe> recipes = new ArrayList<>();

                while(in.available() > 0){
                    byte[] b = new byte[in.readInt()];
                    in.read(b);

                    Material mat = Material.valueOf(new String(b));
                    int amount = in.readInt();

                    MerchantRecipe recipe = new MerchantRecipe(new ItemStack(mat, amount), 0);
                    int ingredients = in.readInt();


                    for(int i = 0; i < ingredients; i++){
                        b = new byte[in.readInt()];
                        in.read(b);

                        mat = Material.valueOf(new String(b));
                        amount = in.readInt();

                        recipe.addIngredient(new ItemStack(mat, amount));
                    }

                    recipes.add(recipe);
                }

                merchant.setRecipes(recipes);
            }


            File stockFile = new File(shopFolder+File.separator+"stock.ser");
            stock = Bukkit.createInventory(null, 36, "Stock");
            if(stockFile.exists()){
                DataInputStream in = new DataInputStream(new FileInputStream(stockFile));

                int i = 0;
                while(in.available() > 0){
                    byte[] b = new byte[in.readInt()];
                    in.read(b);

                    Material mat = Material.valueOf(new String(b));
                    int amount = in.readInt();

                    stock.setItem(i, new ItemStack(mat, amount));
                    i++;
                }
            }

            inventories.put(stock, this.key);


            File receivedFile = new File(shopFolder+File.separator+"received.ser");
            received = Bukkit.createInventory(null, 36, "Received");
            if(receivedFile.exists()){
                DataInputStream in = new DataInputStream(new FileInputStream(receivedFile));

                int i = 0;
                while(in.available() > 0){
                    byte[] b = new byte[in.readInt()];
                    in.read(b);

                    Material mat = Material.valueOf(new String(b));
                    int amount = in.readInt();

                    received.setItem(i, new ItemStack(mat, amount));
                    i++;
                }
            }

            inventories.put(received, this.key);

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
            File configFile = new File(shopFolder.getPath()+File.separator+"data.yml");
            if(configFile.exists()){
                OutputStream out = new FileOutputStream(configFile);
                out.flush();
                out.close();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("key", key.toString());
            config.set("player.uuid", playerUUID.toString());
            config.set("entity.uuid", entityUUID.toString());
            config.set("entity.type", entityType.name());
            config.set("name", name);

            config.set("location.world", location.getWorld().getName());
            config.set("location.x", location.getX());
            config.set("location.y", location.getY());
            config.set("location.z", location.getZ());
            config.set("location.yaw", location.getYaw());
            config.set("location.pitch", location.getPitch());

            config.save(configFile);

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
                byte[] b = recipe.getResult().getType().name().getBytes();
                out.writeInt(b.length);
                out.write(b);

                out.writeInt(recipe.getResult().getAmount());

                out.writeInt(recipe.getIngredients().size());

                for(ItemStack item : recipe.getIngredients()){
                    b = item.getType().name().getBytes();
                    out.writeInt(b.length);
                    out.write(b);

                    out.writeInt(item.getAmount());
                }
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
