package rs.v9.myeconomy.shop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.*;

import static rs.v9.myeconomy.shop.ShopHandler.trading;

public class MyShop {

    private UUID uuid;
    private String name;
    private Merchant merchant;
    private Inventory stock, received;
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
            recipe.setUses(1);

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
    }

    public void removeTrade(int i){
        if(merchant.getRecipeCount() < 1){
            return;
        }

        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        recipes.remove(i);
        merchant.setRecipes(recipes);
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
            recipe.setUses(1);
            stock.removeItem(new ItemStack(recipe.getResult().getType(), count-total));
            received.addItem(new ItemStack(recipe.getIngredients().get(0).getType(), count-total));
        }
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public MyShop create(String name, Location location, EntityType type){
        this.name = name;

        entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
        entity.setCustomName(name);
        entity.setInvulnerable(true);
        entity.setPersistent(true);
        entity.setAI(false);
        entity.setGravity(false);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        uuid = entity.getUniqueId();

        merchant = Bukkit.createMerchant("Shop");
        stock = Bukkit.createInventory(null, 36, "Stock");
        received = Bukkit.createInventory(null, 36, "Received");

        return this;
    }

    public boolean delete(){
        if(!stock.isEmpty() || !received.isEmpty()){
            return false;
        }

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
        /*
        File groupFolder = new File(plugin.getDataFolder()+File.separator+"group"+File.separator+key);
        if(!groupFolder.exists()){
            groupFolder.mkdirs();
        }

        try{
            File data = new File(groupFolder.getPath()+File.separator+"data.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(data);

            config.set("key", key.toString());
            config.set("name", name);
            config.set("color", color);
            config.set("power", power);
            config.set("description", description);

            if(home != null){
                config.set("home.world", home.getWorld().getName());
                config.set("home.x", home.getX());
                config.set("home.y", home.getY());
                config.set("home.z", home.getZ());
                config.set("home.yaw", home.getYaw());
                config.set("home.pitch", home.getPitch());
            }

            config.save(data);
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }
}
