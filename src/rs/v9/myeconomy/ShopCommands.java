package rs.v9.myeconomy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rs.v9.myeconomy.claim.Claim;
import rs.v9.myeconomy.group.MyGroup;
import rs.v9.myeconomy.shop.MyShop;

import java.util.ArrayList;
import java.util.List;

import static rs.v9.myeconomy.Config.isShops;
import static rs.v9.myeconomy.claim.ClaimHandler.getClaim;
import static rs.v9.myeconomy.group.GroupHandler.getPlayersGroup;
import static rs.v9.myeconomy.handlers.MobHandler.getAllowedShops;
import static rs.v9.myeconomy.shop.ShopHandler.*;

public class ShopCommands implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(!isShops()){
                commandSender.sendMessage("§cShops are disabled.");
                return true;
            }

            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                switch(cmd){
                    case "help":
                        return help(((Player) commandSender), args);

                    case "?":
                        return help(((Player) commandSender), args);

                    case "create":
                        return create(((Player) commandSender), args);

                    case "remove":
                        return remove(((Player) commandSender), args);

                    case "open":
                        return open(((Player) commandSender), args);

                    case "trade":
                        return trade(((Player) commandSender), args);
                }

            }else{
                commandSender.sendMessage("§7Type §c/g help§7 to see a list of commands.");
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = args[0].toLowerCase();
                ArrayList<String> tabComplete = new ArrayList<>();

                if(args.length == 5 || args.length == 7){
                    switch(cmd){
                        case "trade":
                            switch(args[1]){
                                case "add":
                                    tabComplete.add("AMOUNT");
                                    break;
                            }
                            break;
                    }

                }else if(args.length == 4 || args.length == 6){
                    switch(cmd){
                        case "trade":
                            switch(args[1]){
                                case "add":
                                    for(Material mat : Material.values()){
                                        if(mat.name().startsWith(args[args.length-1].toUpperCase()) || args[args.length-1].equals("")){
                                            tabComplete.add(mat.toString().toLowerCase());
                                        }
                                    }
                                    break;

                                case "remove":
                                    tabComplete.add("INDEX");
                                    break;
                            }
                            break;
                    }

                }else if(args.length == 3){
                    switch(cmd){
                        case "create":
                            for(EntityType mob : getAllowedShops()){
                                if(mob.name().startsWith(args[2].toUpperCase()) || args[2].equals("")){
                                    tabComplete.add(mob.name().toLowerCase());
                                }
                            }
                            break;

                        case "remove":
                        case "open":
                        case "trade":
                            tabComplete.add("SHOP_NAME");
                            break;
                    }

                }else if(args.length == 2){
                    switch(cmd){
                        case "create":
                        case "remove":
                            tabComplete.add("SHOP_NAME");
                            break;

                        case "open":
                            tabComplete.add("stock");
                            tabComplete.add("received");
                            break;

                        case "trade":
                            tabComplete.add("add");
                            tabComplete.add("remove");
                            break;
                    }

                }else{
                    tabComplete.add("help");
                    tabComplete.add("?");
                    tabComplete.add("create");
                    tabComplete.add("remove");
                    tabComplete.add("open");
                    tabComplete.add("trade");
                }

                return tabComplete;
            }
        }

        return null;
    }

    private boolean help(Player player, String[] args){
        if(player.hasPermission("g.help")){
            player.sendMessage("§c------- §fShop commands (1/1) §c-------");
            player.sendMessage("§c/g create: §7Creates a shop.");
            player.sendMessage("§c/g remove: §7Remove a shop.");
            player.sendMessage("§c/g open: §7Open shop stock and received.");
            player.sendMessage("§c/g trade: §7Add or Remove a trade.");
            player.sendMessage("§c/g remove: §7Remove player from your group.");

        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean create(Player player, String[] args){
        if(player.hasPermission("s.create")){
            if(args.length > 2){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){
                    EntityType type = EntityType.valueOf(args[2].toUpperCase());

                    if(isPlayerShopCapped(player)){
                        player.sendMessage("§cYou have hit the max number of shops per player.");
                        return true;
                    }

                    MyGroup group = getPlayersGroup(player.getUniqueId());
                    Claim claim = getClaim(player.getLocation().getChunk());
                    if(claim != null){
                        if(group == null || !claim.getKey().equals(group.getKey())){
                            player.sendMessage("§cCannot create a shop in another groups claim.");
                            return true;
                        }
                    }

                    if(type != null){
                        if(getAllowedShops().contains(type)){
                            if(!hasShopName(player, name)){
                                MyShop shop = new MyShop(name).create(player, name, type);
                                if(shop != null){
                                    createShop(shop);
                                    player.sendMessage("§7You have successfully created the shop §a"+name+"§7.");

                                }else{
                                    player.sendMessage("§cFailed to create shop.");
                                }
                            }else{
                                player.sendMessage("§cYou already have a shop with this name.");
                            }
                        }else{
                            player.sendMessage("§cEntity type is not allowed.");
                        }
                    }else{
                        player.sendMessage("§cEntity type doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cShop name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a shop name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean remove(Player player, String[] args){
        if(player.hasPermission("s.remove")){
            if(args.length > 1){
                String name = args[1];

                MyShop shop = getShopByName(player, name);
                if(shop != null){
                    if(shop.delete()){
                        deleteShop(player, shop);
                        player.sendMessage("§7You have successfully deleted shop §c"+shop.getName()+"§7.");

                    }else{
                        player.sendMessage("§cFailed to delete shop.");
                    }
                }else{
                    player.sendMessage("§cYou don't have a shop with this name.");
                }

                return true;
            }else{
                player.sendMessage("§cPlease specify a shop name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return false;
        }
    }

    public boolean open(Player player, String[] args){
        if(player.hasPermission("s.open")){
            if(args.length > 2){
                String type = args[1];
                String name = args[2];
                MyShop shop = getShopByName(player, name);

                if(shop != null){
                    switch(type){
                        case "stock":
                            shop.openStock(player);
                            break;

                        case "received":
                            shop.openReceive(player);
                            break;
                    }
                }else{
                    player.sendMessage("§cShop doesn't exist.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a shop name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return false;
        }
    }

    public boolean trade(Player player, String[] args){
        if(player.hasPermission("s.trade")){
            if(args.length > 3){
                String type = args[1];
                String name = args[2];
                MyShop shop = getShopByName(player, name);

                if(shop != null){
                    switch(type){
                        case "add":
                            if(args.length > 6){
                                try{
                                    shop.addTrade(new ItemStack(Material.valueOf(args[3].toUpperCase()), Integer.parseInt(args[4])), new ItemStack(Material.valueOf(args[5].toUpperCase()), Integer.parseInt(args[6])));
                                    player.sendMessage("§7Added trade of §a"+args[3]+"§7 for §a"+args[5]);
                                }catch(Exception e){
                                    e.printStackTrace();
                                    player.sendMessage("§cFailed to parse amount.");
                                }

                            }else{
                                player.sendMessage("§cYou must specify what you want to trade for.");
                            }
                            break;

                        case "remove":
                            try{
                                int index = Integer.parseInt(args[3]);
                                shop.removeTrade(index);
                                player.sendMessage("§7Removed trade: §c"+args[3]);

                            }catch(Exception e){
                                player.sendMessage("§cIndex was not a number.");
                            }
                            break;
                    }

                }else{
                    player.sendMessage("§cShop doesn't exist.");
                }

                return true;
            }else{
                player.sendMessage("§cPlease specify a shop name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return false;
        }
    }
}
