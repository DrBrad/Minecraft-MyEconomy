package rs.v9.myeconomy;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import rs.v9.myeconomy.claim.Claim;
import rs.v9.myeconomy.group.MyGroup;
import rs.v9.myeconomy.group.Zone;
import rs.v9.myeconomy.shop.MyShop;

import java.util.*;

import static rs.v9.myeconomy.Config.*;
import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.claim.ClaimHandler.*;
import static rs.v9.myeconomy.group.GroupHandler.*;
import static rs.v9.myeconomy.handlers.BlockHandler.*;
import static rs.v9.myeconomy.handlers.Colors.getChatColor;
import static rs.v9.myeconomy.handlers.Colors.getColorRGB;
import static rs.v9.myeconomy.handlers.GeneralHandler.*;
import static rs.v9.myeconomy.handlers.MapHandler.isMapping;
import static rs.v9.myeconomy.handlers.MapHandler.mapLandscape;
import static rs.v9.myeconomy.handlers.PlayerCooldown.*;
import static rs.v9.myeconomy.handlers.PlayerResolver.*;
import static rs.v9.myeconomy.shop.ShopHandler.*;

public class MyEventHandler implements Listener {

    private static HashMap<Player, UUID> enteredClaim = new HashMap<>();
    private HashMap<UUID, ArrayList<Location>> xray = new HashMap<>();
    private Random random = new Random();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        setPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId());

        MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
        if(group != null){
            String color = getChatColor(group.getColor());
            String[] names = getRanks();

            event.getPlayer().setPlayerListName("§6["+color+group.getName()+"§6]["+color+names[group.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]");
            event.setJoinMessage(getChatColor(group.getColor())+event.getPlayer().getName()+"§7 Has joined the server!");

        }else{
            event.getPlayer().setPlayerListName("§c"+event.getPlayer().getName());
            event.setJoinMessage("§c"+event.getPlayer().getName()+"§7 Has joined the server!");
        }

        setPlayerCooldown(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
        if(group != null){
            event.setQuitMessage(getChatColor(group.getColor())+event.getPlayer().getName()+"§7 Has left the server!");

            if(isChatting(event.getPlayer().getUniqueId())){
                stopChatting(event.getPlayer().getUniqueId());
            }

            if(isAutoClaiming(event.getPlayer().getUniqueId())){
                stopAutoClaiming(event.getPlayer().getUniqueId());
            }

        }else{
            event.setQuitMessage("§c"+event.getPlayer().getName()+"§7 Has left the server!");
        }

        setPlayerCooldown(event.getPlayer().getUniqueId());

        if(xray.containsKey(event.getPlayer().getUniqueId())){
            xray.remove(event.getPlayer().getUniqueId());
        }

        removeTrader(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        lastTeleport.put(event.getEntity().getPlayer(), Objects.requireNonNull(event.getEntity().getPlayer()).getLocation());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
        if(group != null){
            spawnCircle(event.getPlayer(), event.getTo(), getColorRGB(group.getColor()));
        }else{
            spawnCircle(event.getPlayer(), event.getTo(), Color.fromRGB(0, 0, 255));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPortalTravel(PlayerPortalEvent event){
        if(event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL){
            Location endSpawn = getEndSpawn();
            if(endSpawn != null){
                event.setTo(endSpawn);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
        if(group != null){
            String color = getChatColor(group.getColor());
            String[] names = getRanks();

            if(isChatting(event.getPlayer().getUniqueId())){
                for(String uuid : group.getPlayers()){
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if(player != null && player.isOnline()){
                        player.getPlayer().sendMessage("§6["+color+group.getName()+"§6]["+color+names[group.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]§7: §a"+event.getMessage());
                    }
                }

                event.setCancelled(true);

            }else{
                event.setFormat("§6["+color+group.getName()+"§6]["+color+names[group.getRank(event.getPlayer().getUniqueId())]+"§6]["+color+event.getPlayer().getName()+"§6]§7: "+event.getMessage());
            }
        }else{
            event.setFormat("§6[§c"+event.getPlayer().getName()+"§6]§7: "+event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlockPlaced();
        Material material = block.getType();

        if(material == Material.SPAWNER){
            CreatureSpawner type = (CreatureSpawner) block.getState();
            type.setSpawnedType(EntityType.AREA_EFFECT_CLOUD);
            type.update(true, false);
        }

        Chunk chunk = event.getBlock().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(claim.getType() > 0){
                if(event.getPlayer().isOp()){
                    return;
                }

                if(!event.getPlayer().hasPermission("f.admin")){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cOnly server admins can place blocks in zones.");
                }

            }else{
                MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
                if(group != null){
                    if(!claim.getKey().equals(group.getKey())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks in other groups claims.");

                    }else if(!group.canBuild(event.getPlayer().getUniqueId())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot place blocks as a member.");
                    }
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot place blocks in other groups claims.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.PHYSICAL){
            Block block = event.getClickedBlock();
            if(block == null || block.getType() != Material.FARMLAND){
                return;
            }

            Claim claim = getClaim(event.getPlayer().getLocation().getChunk());
            if(claim == null || claim.getType() == 0){
                return;
            }

            if(!getPlayersGroup(event.getPlayer().getUniqueId()).getKey().equals(claim.getKey())){
                event.setCancelled(true);
            }
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getClickedBlock().getType() == Material.SPAWNER){
                Player player = event.getPlayer();
                Material material = player.getItemInHand().getType();

                if(material == Material.BLAZE_SPAWN_EGG){
                    if(event.getClickedBlock().getWorld().getEnvironment() != World.Environment.NETHER){
                        player.sendMessage("§7Sorry, you can make blaze spawners in the §cNether§7.");
                        event.setCancelled(true);
                        return;
                    }
                }else if(material != Material.SKELETON_SPAWN_EGG && material != Material.ZOMBIE_SPAWN_EGG && material != Material.BLAZE_SPAWN_EGG &&
                        material != Material.SPIDER_SPAWN_EGG && material != Material.SILVERFISH_SPAWN_EGG && material != Material.MAGMA_CUBE_SPAWN_EGG &&
                        material != Material.CAVE_SPIDER_SPAWN_EGG && material != Material.PIG_SPAWN_EGG){
                    player.sendMessage("§cSorry, you cant make a spawner with this creature.");
                    event.setCancelled(true);
                    return;
                }
            }

            Block block = event.getClickedBlock();

            Chunk chunk = block.getChunk();
            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim.getType() > 0){
                    if(!getSafeNoEdit().contains(block.getType())){
                        if(event.getPlayer().isOp()){
                            return;
                        }

                        if(!event.getPlayer().hasPermission("f.admin")){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cOnly server admins can interact with blocks in zones.");
                        }
                    }

                }else if(getNoEdit().contains(block.getType())){
                    MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
                    if(group != null){
                        if(!group.getKey().equals(claim.getKey())){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks in other groups claims.");

                        }else if(!group.canBuild(event.getPlayer().getUniqueId())){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cYou cannot interact with blocks as a member.");
                        }
                    }else{
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot interact with blocks in other groups claims.");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Material material = block.getType();

        if(material == Material.SPAWNER &&
                event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)){
            CreatureSpawner type = (CreatureSpawner) block.getState();
            EntityType mobtype = type.getSpawnedType();

            event.getBlock().getDrops().clear();

            switch(mobtype.name()){
                case "SKELETON":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SKELETON_SPAWN_EGG));
                    break;

                case "ZOMBIE":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.ZOMBIE_SPAWN_EGG));
                    break;

                case "BLAZE":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.BLAZE_SPAWN_EGG));
                    break;

                case "SPIDER":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SPIDER_SPAWN_EGG));
                    break;

                case "SILVERFISH":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SILVERFISH_SPAWN_EGG));
                    break;

                case "MAGMA_CUBE":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG));
                    break;

                case "CAVE_SPIDER":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.CAVE_SPIDER_SPAWN_EGG));
                    break;

                case "PIG":
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.PIG_SPAWN_EGG));
                    break;
            }
        }

        Chunk chunk = event.getBlock().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(claim.getType() > 0){
                if(event.getPlayer().isOp()){
                    return;
                }

                if(!event.getPlayer().hasPermission("f.admin")){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cOnly server admins can break blocks in zones.");
                    return;
                }

            }else{
                MyGroup group = getPlayersGroup(event.getPlayer().getUniqueId());
                if(group != null){
                    if(!claim.getKey().equals(group.getKey())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot break blocks in other groups claims.");
                        return;

                    }else if(!group.canBuild(event.getPlayer().getUniqueId())){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cYou cannot break blocks as a member.");
                        return;
                    }
                }else{
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot break blocks in other groups claims.");
                    return;
                }
            }
        }

        if(isXRay()){
            if(!event.isCancelled()){
                if(xray.containsKey(event.getPlayer().getUniqueId())){
                    isNextToOre(event.getPlayer(), event.getBlock());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if((event.getEntity().getCustomName() != null && !event.getEntity().getType().equals(EntityType.PLAYER)) ||
                event.getEntity() instanceof MushroomCow ||
                event.getEntity() instanceof Golem ||
                event.getEntity() instanceof Snowman ||
                event.getEntity() instanceof Breedable ||
                event.getEntity() instanceof ItemFrame ||
                event.getEntity() instanceof Minecart ||
                event.getEntity() instanceof Boat ||
                event.getEntity() instanceof Painting){
            Player player = null;

            if(event.getDamager() instanceof Player){
                player = ((Player) event.getDamager()).getPlayer();
            }

            if(event.getDamager().getType() == EntityType.ARROW &&
                    ((Projectile) event.getDamager()).getShooter() instanceof Player){
                player = (Player) ((Projectile) event.getDamager()).getShooter();
            }

            if(player == null){
                return;
            }

            Claim claim = getClaim(event.getEntity().getLocation().getChunk());
            if(claim == null){
                return;
            }

            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group == null){
                event.setCancelled(true);
                return;
            }

            if(!claim.getKey().equals(group.getKey())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamageEvent(VehicleDamageEvent event){
        Player player = null;

        if(event.getAttacker() instanceof Player){
            player = ((Player) event.getAttacker()).getPlayer();
        }

        if(event.getAttacker().getType() == EntityType.ARROW &&
                ((Projectile) event.getAttacker()).getShooter() instanceof Player){
            player = (Player) ((Projectile) event.getAttacker()).getShooter();
        }

        if(player == null){
            return;
        }

        Claim claim = getClaim(event.getVehicle().getLocation().getChunk());
        if(claim == null){
            return;
        }

        MyGroup group = getPlayersGroup(player.getUniqueId());
        if(group == null){
            event.setCancelled(true);
            return;
        }

        if(!claim.getKey().equals(group.getKey())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity().isInvulnerable()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event){
        if(event.getEntity().isInvulnerable()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) event.getRightClicked();

            MyShop shop = getShopByEntityUUID(entity.getUniqueId());
            if(shop != null){
                shop.openMerchant(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event){
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) ||
                event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DISPENSE_EGG)){
            event.setCancelled(true);
            return;
        }

        Chunk chunk = event.getLocation().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);
            if(claim != null && claim.getType() > 0){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            Chunk chunk = player.getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);

                if(claim != null && claim.getType() == 2){
                    event.setDamage(0.0F);
                    event.setCancelled(true);
                    if(event.getDamager() instanceof Player){
                        Player attacker = (Player) event.getDamager();
                        attacker.sendMessage("§cYou cannot attack players §aSafe Zones§7.");
                        return;
                    }
                }
            }

            if(event.getDamager() instanceof Player){
                Player attacker = (Player) event.getDamager();

                MyGroup victomsGroup = getPlayersGroup(player.getUniqueId());
                MyGroup attackerGroup = getPlayersGroup(attacker.getUniqueId());

                if(victomsGroup != null && attackerGroup != null){
                    if(victomsGroup.getKey().equals(attackerGroup.getKey())){
                        event.setDamage(0.0F);
                        event.setCancelled(true);
                        attacker.sendMessage("§cYou cannot attack players in your own group.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            if(event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()){
                Chunk chunk = player.getLocation().getChunk();

                if(inClaim(chunk)){
                    Claim claim = getClaim(chunk);
                    if(claim != null && claim.getType() == 2){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            Chunk chunk = player.getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim != null && claim.getType() == 2){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event){
        Chunk chunk = event.getLocation().getChunk();

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);
            if(claim != null && claim.getType() > 0){
                List<Block> blocks = new ArrayList<>(event.blockList());
                event.blockList().clear();
                regenBlocks(event.getLocation(), blocks);
                return;
            }
        }

        List<Block> blocks = new ArrayList<>(event.blockList());
        event.blockList().clear();
        List<Block> regen = new ArrayList<>();

        for(Block block : blocks){
            if(block.getType().equals(Material.AIR)){
                continue;
            }

            Claim claim = getClaim(block.getChunk());
            if(claim != null){
                regen.add(block);
                continue;
            }

            event.blockList().add(block);
        }

        if(!regen.isEmpty()){
            regenBlocks(event.getLocation(), regen);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory() instanceof MerchantInventory){
            removeTrader((Player) event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory() instanceof MerchantInventory){
            if(event.getSlot() != 2){
                return;
            }
            MyShop shop = getShopByTrader((Player) event.getWhoClicked());

            if(shop != null){
                if(((MerchantInventory) event.getInventory()).getSelectedRecipe() == null){
                    return;
                }

                MerchantRecipe recipe = ((MerchantInventory) event.getInventory()).getSelectedRecipe();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    @Override
                    public void run(){
                        shop.notifyTrade(recipe);
                    }
                },0);
            }

            return;
        }

        if(event.getInventory().getSize() == 36){
            MyShop shop = getInventory(event.getInventory());

            if(shop != null){
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    @Override
                    public void run(){
                        shop.notifyStorage(event.getInventory());
                    }
                },0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChangeBlock(EntityChangeBlockEvent event){
        if(event.getEntity().getType().equals(EntityType.ENDERMAN)){
            Chunk chunk = event.getBlock().getLocation().getChunk();

            if(inClaim(chunk)){
                Claim claim = getClaim(chunk);
                if(claim.getType() > 0){
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.setDeathMessage("§7"+event.getDeathMessage());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Chunk chunk = event.getPlayer().getLocation().getChunk();

        if(isAutoClaiming(event.getPlayer().getUniqueId())){
            if(!autoClaimChunk(event.getPlayer(), chunk) && isMapping(event.getPlayer().getUniqueId())){
                mapLandscape(event.getPlayer(), chunk);
            }
        }else if(isMapping(event.getPlayer().getUniqueId())){
            mapLandscape(event.getPlayer(), chunk);
        }

        UUID key = null;
        if(enteredClaim.containsKey(event.getPlayer())){
            key = enteredClaim.get(event.getPlayer());
        }

        if(inClaim(chunk)){
            Claim claim = getClaim(chunk);

            if(key == null || !key.equals(claim.getKey())){
                enteredClaim.put(event.getPlayer(), claim.getKey());

                if(claim.getType() > 0){
                    Zone zone = getZoneByUUID(claim.getKey());

                    if(zone != null){
                        event.getPlayer().sendTitle(getChatColor(zone.getColor())+zone.getName(), "", 0, 60, 0);
                    }

                }else{
                    MyGroup group = getGroupFromUUID(claim.getKey());

                    if(group != null){
                        event.getPlayer().sendTitle(getChatColor(group.getColor())+group.getName(), "", 0, 60, 0);
                    }
                }
            }

        }else if(key != null){
            enteredClaim.put(event.getPlayer(), null);
            event.getPlayer().sendTitle("§2Wilderness", "", 0, 60, 0);
        }

        if(isXRay()){
            antiXRay(event.getPlayer(), event.getPlayer().getLocation().getChunk());
        }
    }

    public void antiXRay(Player player, Chunk centerChunk){
        if(xray.containsKey(player.getUniqueId())){
            int size = xray.get(player.getUniqueId()).size();
            if(size > 0){
                for(int i = size; i > 0; i--){
                    if(xray.get(player.getUniqueId()).size() > i){
                        Location location = xray.get(player.getUniqueId()).get(i);
                        if(location.getWorld() == centerChunk.getWorld()){
                            if(player.getLocation().distance(location) > 100){
                                xray.get(player.getUniqueId()).remove(i);
                            }
                        }else{
                            xray.get(player.getUniqueId()).remove(i);
                        }
                    }
                }
            }
        }else{
            xray.put(player.getUniqueId(), new ArrayList<>());
        }

        ArrayList<Chunk> chunks = new ArrayList<>();

        for(int x = -getXRayRadius(); x < getXRayRadius()+1; x++){
            for(int z = -getXRayRadius(); z < getXRayRadius()+1; z++){
                chunks.add(centerChunk.getBlock(0, 0, 0).getLocation().add(x*16, 0, z*16).getChunk());
            }
        }

        List<Material> xrayBlocks = getXRayBlocks();

        for(Chunk chunk : chunks){
            if(!xray.get(player.getUniqueId()).contains(chunk.getBlock(0, 0, 0).getLocation())){
                if(!xray.get(player.getUniqueId()).contains(chunk.getBlock(0, 0, 0).getLocation())){
                    xray.get(player.getUniqueId()).add(chunk.getBlock(0, 0, 0).getLocation());
                }

                for(int x = 0; x < 16; x++){
                    for(int z = 0; z < 16; z++){
                        int height = chunk.getWorld().getHighestBlockAt(
                                (int)chunk.getBlock(x, 0, z).getLocation().getX(),
                                (int)chunk.getBlock(x, 0, z).getLocation().getZ()).getY();
                        for(int y = chunk.getWorld().getMinHeight(); y < height; y++){
                            Block block = chunk.getBlock(x, y, z);
                            if(xrayBlocks.contains(block.getType())){
                                if(!isOreExposed(block)){
                                    player.sendBlockChange(block.getLocation(), Material.STONE.createBlockData());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isOreExposed(Block block){
        List<Material> air = getAir();

        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().add(1, 0, 0)).getType())){
            return true;
        }
        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(1, 0, 0)).getType())){
            return true;
        }

        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).getType())){
            return true;
        }
        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(0, 1, 0)).getType())){
            return true;
        }

        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, 0, 1)).getType())){
            return true;
        }
        if(air.contains(block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(0, 0, 1)).getType())){
            return true;
        }

        return false;
    }

    private void isNextToOre(Player player, Block block){
        List<Material> ores = getXRayBlocks();

        Block sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }
        sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(1, 0, 0));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }

        sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }
        sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(0, 1, 0));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }

        sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }
        sideBlock = block.getLocation().getWorld().getBlockAt(block.getLocation().subtract(0, 0, 1));
        if(ores.contains(sideBlock.getType())){
            player.sendBlockChange(sideBlock.getLocation(), sideBlock.getBlockData());
        }
    }

    private void regenBlocks(Location location, List<Block> blocks){
        Collection<Player> players = location.getWorld().getPlayersSeeingChunk(location.getChunk());

        for(Block block : blocks){
            for(Player player : players){
                player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
            }

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                @Override
                public void run(){
                    for(Player player : players){
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                }
            }, random.nextInt(121)+40);
        }
    }
}
