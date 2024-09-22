package rs.v9.myeconomy;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import rs.v9.myeconomy.claim.Claim;
import rs.v9.myeconomy.group.MyGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static rs.v9.myeconomy.Config.*;
import static rs.v9.myeconomy.Main.plugin;
import static rs.v9.myeconomy.claim.ClaimHandler.*;
import static rs.v9.myeconomy.group.GroupHandler.*;
import static rs.v9.myeconomy.handlers.Colors.*;
import static rs.v9.myeconomy.handlers.GeneralHandler.teleport;
import static rs.v9.myeconomy.handlers.MapHandler.*;
import static rs.v9.myeconomy.handlers.PlayerResolver.getPlayer;

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

                if(args.length > 1){
                    switch(cmd){
                        case "create":
                            tabComplete.add("SHOP_NAME");
                            break;

                        case "remove":
                            tabComplete.add("SHOP_NAME");
                            break;
                    }
                }else{
                    tabComplete.add("help");
                    tabComplete.add("?");
                    tabComplete.add("create");
                    tabComplete.add("remove");
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
            if(args.length > 1){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){



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
}
