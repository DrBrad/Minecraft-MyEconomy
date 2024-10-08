package rs.v9.myeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import rs.v9.myeconomy.claim.ClaimHandler;
import rs.v9.myeconomy.group.GroupHandler;
import rs.v9.myeconomy.group.MyGroup;
import rs.v9.myeconomy.chunkloader.ChunkLoadHandler;
import rs.v9.myeconomy.handlers.PlayerCooldown;
import rs.v9.myeconomy.handlers.PlayerResolver;
import rs.v9.myeconomy.shop.ShopHandler;

import static rs.v9.myeconomy.Config.*;
import static rs.v9.myeconomy.group.GroupHandler.getPlayersGroup;
import static rs.v9.myeconomy.handlers.Colors.getChatColor;
import static rs.v9.myeconomy.handlers.MapHandler.isMapping;
import static rs.v9.myeconomy.handlers.MapHandler.stopMapping;
import static rs.v9.myeconomy.holo.ConnectionInjecter.injectPlayer;
import static rs.v9.myeconomy.holo.ConnectionInjecter.removePlayer;

public class Main extends JavaPlugin {

    public static Plugin plugin;
    public static DynmapCommonAPI dynmap;
    private int task;

    //Attempt player chunk loading not forceChunkLoad
    //add close to chat...

    @Override
    public void onLoad(){
        plugin = this;

        DynmapCommonAPIListener.register(new DynmapCommonAPIListener(){
            @Override
            public void apiEnabled(DynmapCommonAPI dynmapCommonAPI){
                dynmap = dynmapCommonAPI;
            }
        });
    }

    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(new MyEventHandler(), this);
        getCommand("g").setExecutor(new GroupCommands());
        getCommand("s").setExecutor(new ShopCommands());

        getCommand("warp").setExecutor(new MyCommands());
        getCommand("warps").setExecutor(new MyCommands());
        getCommand("setwarp").setExecutor(new MyCommands());
        getCommand("delwarp").setExecutor(new MyCommands());
        getCommand("home").setExecutor(new MyCommands());
        getCommand("sethome").setExecutor(new MyCommands());
        getCommand("spawn").setExecutor(new MyCommands());
        getCommand("setspawn").setExecutor(new MyCommands());
        getCommand("tpa").setExecutor(new MyCommands());
        getCommand("tpaa").setExecutor(new MyCommands());
        getCommand("tpad").setExecutor(new MyCommands());
        getCommand("msg").setExecutor(new MyCommands());
        getCommand("gamemode").setExecutor(new MyCommands());
        getCommand("back").setExecutor(new MyCommands());
        getCommand("chunkload").setExecutor(new MyCommands());

        new Config();
        new GroupHandler();
        new ClaimHandler();
        new ChunkLoadHandler();
        new PlayerResolver();
        new PlayerCooldown();
        new ShopHandler();

        createRecipes();

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            @Override
            public void run(){
                String[] names = getRanks();

                for(Player player : getPlayersAFK()){
                    MyGroup group = getPlayersGroup(player.getUniqueId());
                    if(group != null){
                        String color = getChatColor(group.getColor());
                        player.setPlayerListName("§6[§7AFK§6]§6["+color+group.getName()+"§6]["+color+names[group.getRank(player.getUniqueId())]+"§6]["+color+player.getName()+"§6]");

                    }else{
                        player.setPlayerListName("§6[§7AFK§6]§c"+player.getName());
                    }
                }
            }
        }, 200, 200);//100

        for(Player player : Bukkit.getOnlinePlayers()){
            injectPlayer(player);
        }
    }

    @Override
    public void onDisable(){
        Bukkit.getServer().getScheduler().cancelTask(task);

        for(Player player : Bukkit.getOnlinePlayers()){
            if(isMapping(player.getUniqueId())){
                stopMapping(player);
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()){
            removePlayer(player);
        }

        clearEntities();
    }

    private void createRecipes(){
        ShapedRecipe spawner = new ShapedRecipe(new ItemStack(Material.SPAWNER, 1));

        spawner.shape("NNN","NTN","NNN");

        spawner.setIngredient('N', Material.NETHERITE_INGOT);
        spawner.setIngredient('T', Material.TOTEM_OF_UNDYING);

        getServer().addRecipe(spawner);

        getServer().addRecipe(new FurnaceRecipe(new ItemStack(Material.LEATHER), Material.ROTTEN_FLESH));
    }
}
