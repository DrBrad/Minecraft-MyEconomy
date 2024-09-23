package rs.v9.myeconomy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rs.v9.myeconomy.handlers.MobHandler;
import rs.v9.myeconomy.shop.MyShop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static rs.v9.myeconomy.group.GroupHandler.createGroup;
import static rs.v9.myeconomy.handlers.MobHandler.getAllowedShops;
import static rs.v9.myeconomy.shop.ShopHandler.createShop;
import static rs.v9.myeconomy.shop.ShopHandler.getShopByName;

public class ShopCommands implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
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
                                        tabComplete.add(mat.toString());
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
                                tabComplete.add(mob.name());
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
            if(args.length > 1){
                if(args[1].equals("2")){
                    player.sendMessage("§c------- §fGroup commands (2/5) §c-------");
                    player.sendMessage("§c/g unclaim: §7Removes claim from your group.");
                    player.sendMessage("§c/g autoclaim: §7Automatically claim a chunk for your group.");
                    player.sendMessage("§c/g unclaim: §7Automatically removes claim from your group.");
                    player.sendMessage("§c/g home: §7Teleport to your groups home.");
                    player.sendMessage("§c/g sethome: §7Set your groups home.");
                    player.sendMessage("§c/g warp: §7Teleport to one of your groups warps.");
                    player.sendMessage("§c/g warps: §7Lists all warps for your group.");
                    player.sendMessage("§c/g setwarp: §7Set a warp for your group.");
                    player.sendMessage("§c/g delwarp: §7Removes a warp from your group.");
                    return true;

                }else if(args[1].equals("3")){
                    player.sendMessage("§c------- §fGroup commands (3/5) §c-------");
                    player.sendMessage("§c/g power: §7Check yours or another groups power.");
                    player.sendMessage("§c/g list: §7List of all of the groups.");
                    player.sendMessage("§c/g rename: §7Rename your group something else.");
                    player.sendMessage("§c/g chat: §7Chat with only group members or globally.");
                    player.sendMessage("§c/g map: §7See all group claims chunks visually.");
                    player.sendMessage("§c/g rank: §7Get your rank in group.");
                    player.sendMessage("§c/g chown: §7Change group ownership.");
                    player.sendMessage("§c/g setpower: §7Set groups power.");
                    player.sendMessage("§c/g claim safezone: §7Claim Safe-Zone for server.");
                    return true;

                }else if(args[1].equals("4")){
                    player.sendMessage("§c------- §fGroup commands (4/5) §c-------");
                    player.sendMessage("§c/g unclaim safezone: §7Unclaim Safe-Zone for server.");
                    player.sendMessage("§c/g autoclaim safezone: §7Automatically claims safezone.");
                    player.sendMessage("§c/g autounclaim safezone: §7Automatically removes safezone claims.");
                    player.sendMessage("§c/g claim pvpzone: §7Claim Pvp-Zone for server.");
                    player.sendMessage("§c/g unclaim pvpzone: §7Unclaim Pvp-Zone for server.");
                    player.sendMessage("§c/g autoclaim pvpzone: §7Automatically claims pvpzone.");
                    player.sendMessage("§c/g autounclaim pvpzone: §7Automatically removes pvpzone claims.");
                    player.sendMessage("§c/g setdesc: §7Set groups description.");
                    player.sendMessage("§c/g setcolor: §7Set groups color.");
                    return true;

                }else if(args[1].equals("5")){
                    player.sendMessage("§c------- §fGroup commands (5/5) §c-------");
                    player.sendMessage("§c/g buypower: §7Buy power for diamond, hold the diamond in your hand.");
                    player.sendMessage("§c/g version: §7Get the version of this plugin.");
                    return true;
                }
            }

            player.sendMessage("§c------- §fGroup commands (1/5) §c-------");
            player.sendMessage("§c/g create: §7Creates a group.");
            player.sendMessage("§c/g invite: §7Invites player to group.");
            player.sendMessage("§c/g join: §7Join group from invite.");
            player.sendMessage("§c/g leave: §7Leave your group.");
            player.sendMessage("§c/g remove: §7Remove player from your group.");
            player.sendMessage("§c/g promote: §7Promote player in group.");
            player.sendMessage("§c/g demote: §7Demote player in group.");
            player.sendMessage("§c/g disband: §7Deletes your group.");
            player.sendMessage("§c/g claim: §7Claim a chunk for your group.");

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
                    EntityType type = EntityType.valueOf(args[2]);

                    if(type != null){
                        if(getAllowedShops().contains(type)){
                            MyShop shop = new MyShop(name).create(name, player.getLocation(), type);
                            if(shop != null){
                                createShop(player, shop);
                                player.sendMessage("§7You have successfully created the shop §c"+name+"§7.");

                            }else{
                                player.sendMessage("§cFailed to create shop.");
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
                    System.out.println(shop.getName()+" NAME");
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
                                    System.out.println(args[3]+"  "+args[4]);
                                    System.out.println(args[5]+"  "+args[6]);
                                    ItemStack receive = new ItemStack(Material.valueOf(args[3]), Integer.parseInt(args[4]));
                                    ItemStack give = new ItemStack(Material.valueOf(args[5]), Integer.parseInt(args[6]));
                                    shop.addTrade(receive, give);
                                    //shop.addTrade(new ItemStack(Material.valueOf(args[3]), Integer.parseInt(args[4])),
                                    //        new ItemStack(Material.valueOf(args[5]), Integer.parseInt(args[6])));
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

                            }catch(Exception e){
                                player.sendMessage("§cIndex was not a number.");
                            }
                            break;
                    }

                }else{
                    player.sendMessage("§cShop doesn't exist.");
                }

                /*
                String type = args[1];
                String name = args[2];
                MyShop shop = getShopByName(player, name);

                if(shop != null){
                    System.out.println(shop.getName()+" NAME");
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
                */
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
