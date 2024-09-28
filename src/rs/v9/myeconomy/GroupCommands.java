package rs.v9.myeconomy;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import rs.v9.myeconomy.claim.Claim;
import rs.v9.myeconomy.claim.Flags;
import rs.v9.myeconomy.group.MyGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static rs.v9.myeconomy.Config.*;
import static rs.v9.myeconomy.group.GroupHandler.getListOfGroupNames;
import static rs.v9.myeconomy.handlers.GeneralHandler.*;
import static rs.v9.myeconomy.claim.ClaimHandler.*;
import static rs.v9.myeconomy.group.GroupHandler.*;
import static rs.v9.myeconomy.handlers.Colors.*;
import static rs.v9.myeconomy.handlers.MapHandler.*;
import static rs.v9.myeconomy.handlers.PlayerResolver.*;
import static rs.v9.myeconomy.Main.plugin;

public class GroupCommands implements CommandExecutor, TabExecutor {

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

                    case "invite":
                        return invite(((Player) commandSender), args);

                    case "join":
                        return join(((Player) commandSender));

                    case "leave":
                        return leave(((Player) commandSender));

                    case "remove":
                        return remove(((Player) commandSender), args);

                    case "promote":
                        return promote(((Player) commandSender), args);

                    case "demote":
                        return demote(((Player) commandSender), args);

                    case "disband":
                        return disband(((Player) commandSender));

                    case "claim":
                        return claim(((Player) commandSender), args);

                    case "unclaim":
                        return unclaim(((Player) commandSender), args);

                    case "autoclaim":
                        return autoClaim(((Player) commandSender), args);

                    case "autounclaim":
                        return autoUnclaim(((Player) commandSender), args);

                    case "home":
                        return home(((Player) commandSender));

                    case "sethome":
                        return setHome(((Player) commandSender));

                    case "warps":
                        return warps(((Player) commandSender), args);

                    case "warp":
                        return warp(((Player) commandSender), args);

                    case "setwarp":
                        return setWarp(((Player) commandSender), args);

                    case "delwarp":
                        return removeWarp(((Player) commandSender), args);

                    case "power":
                        return power(((Player) commandSender));

                    case "list":
                        return list(((Player) commandSender), args);

                    case "rename":
                        return rename(((Player) commandSender), args);

                    case "chat":
                        return chat(((Player) commandSender));

                    case "setcolor":
                        return setColor(((Player) commandSender), args);

                    case "setdesc":
                        return setDescription(((Player) commandSender), args);

                    case "map":
                        return map(((Player) commandSender));

                    case "rank":
                        return rank(((Player) commandSender));

                    case "chown":
                        return changeOwnership(((Player) commandSender), args);

                    case "setpower":
                        return setPower(((Player) commandSender), args);

                    case "buypower":
                        return buyPower(((Player) commandSender), args);

                    case "setflag":
                        return setFlag(((Player) commandSender), args);

                    case "flags":
                        return getFlags((Player) commandSender);

                    case "version":
                        commandSender.sendMessage("§7MyEconomy version §c"+plugin.getDescription().getVersion()+"§7 by DrBrad.");
                        return true;
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
                MyGroup group = getPlayersGroup(((Player) commandSender).getUniqueId());
                ArrayList<String> tabComplete = new ArrayList<>();

                if(args.length > 1){
                    switch(cmd){
                        case "create":
                            tabComplete.add("GROUP_NAME");
                            break;

                        case "invite":
                        case "remove":
                        case "promote":
                        case "demote":
                        case "chown":
                            if(group != null){
                                for(String uuid : group.getPlayers()){
                                    String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                                    if(name.startsWith(args[1]) || args[1].equals("")){
                                        tabComplete.add(name);
                                    }
                                }
                            }
                            break;

                        case "warp":
                            if(group != null){
                                tabComplete.addAll(group.getWarps());
                            }
                            break;

                        case "setwarp":
                            if(group != null){
                                tabComplete.addAll(group.getWarps());
                            }
                            break;

                        case "delwarp":
                            if(group != null){
                                tabComplete.addAll(group.getWarps());
                            }
                            break;

                        case "rename":
                            if(group != null){
                                tabComplete.add(group.getName());
                            }
                            break;

                        case "setcolor":
                            if(group != null){
                                for(String color : getAllColors()){
                                    if(color.startsWith(args[1].toUpperCase()) || args[1].equals("")){
                                        tabComplete.add(color.toLowerCase());
                                    }
                                }
                            }
                            break;

                        case "setdesc":
                            if(group != null){
                                tabComplete.add(group.getDescription());
                            }
                            break;

                        case "setpower":
                            if(args.length == 3){
                                if(isGroup(args[1].toLowerCase())){
                                    tabComplete.add(getGroupFromName(args[1].toLowerCase()).getPower()+"");
                                }

                            }else if(args.length == 2){
                                for(String name : getListOfGroupNames()){
                                    if(name.startsWith(args[1]) || args[1].equals("")){
                                        tabComplete.add(name);
                                    }
                                }
                            }
                            break;

                        case "setflag":
                            if(args.length == 3){
                                for(Flags flag : Flags.values()){
                                    if(flag.name().startsWith(args[2].toUpperCase()) || args[2].equals("")){
                                        tabComplete.add(flag.name().toLowerCase());
                                    }
                                }
                            }else if(args.length == 2){
                                tabComplete.add("add");
                                tabComplete.add("remove");
                            }
                            break;

                        case "claim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "unclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "autoclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;

                        case "autounclaim":
                            tabComplete.add("safezone");
                            tabComplete.add("pvpzone");
                            break;
                    }
                }else{
                    tabComplete.add("help");
                    tabComplete.add("?");
                    tabComplete.add("create");
                    tabComplete.add("invite");
                    tabComplete.add("join");
                    tabComplete.add("leave");
                    tabComplete.add("remove");
                    tabComplete.add("promote");
                    tabComplete.add("demote");
                    tabComplete.add("disband");
                    tabComplete.add("claim");
                    tabComplete.add("unclaim");
                    tabComplete.add("autoclaim");
                    tabComplete.add("autounclaim");
                    tabComplete.add("home");
                    tabComplete.add("sethome");
                    tabComplete.add("warps");
                    tabComplete.add("warp");
                    tabComplete.add("setwarp");
                    tabComplete.add("delwarp");
                    tabComplete.add("power");
                    tabComplete.add("list");
                    tabComplete.add("rename");
                    tabComplete.add("chat");
                    tabComplete.add("setcolor");
                    tabComplete.add("setdesc");
                    tabComplete.add("map");
                    tabComplete.add("rank");
                    tabComplete.add("setpower");
                    tabComplete.add("buypower");
                    tabComplete.add("setflag");
                    tabComplete.add("flags");
                    tabComplete.add("version");
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
                    player.sendMessage("§c/g setflag: §7Set flags for claim.");
                    player.sendMessage("§c/g flags: §7Get a list of flags for claim.");
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

    private boolean list(Player player, String[] args){
        if(player.hasPermission("g.list")){
            int page = 0;
            if(args.length > 1){
                page = Integer.parseInt(args[1]);
            }

            ArrayList<MyGroup> groups = getListOfGroups();

            if(groups != null && groups.size() > 0){
                player.sendMessage("§c------- §fList of Groups (1/"+(((groups.size()/9)*page)+1)+") §c-------");

                for(int i = page*9; i < (page+1)*9; i++){
                    if(i < groups.size()){
                        int power = groups.get(i).getPower();
                        if(power > 0){
                            player.sendMessage("§c"+groups.get(i).getName()+"§7 power §a"+power+"§7 "+groups.get(i).getDescription());
                        }else{
                            player.sendMessage("§c"+groups.get(i).getName()+"§7 power §c"+power+"§7 "+groups.get(i).getDescription());
                        }
                    }else{
                        break;
                    }
                }
            }else{
                player.sendMessage("§cThere are no groups currently.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean create(Player player, String[] args){
        if(player.hasPermission("g.create")){
            if(args.length > 1){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){
                    if(!isBannedName(name)){
                        MyGroup group = new MyGroup().create(name, player.getUniqueId());
                        if(group != null){
                            createGroup(player.getUniqueId(), group);
                            player.sendMessage("§7You have successfully created the group §c"+name+"§7.");
                        }else{
                            player.sendMessage("§cFailed to create group.");
                        }
                    }else{
                        player.sendMessage("§cGroup name is not allowed.");
                    }
                }else{
                    player.sendMessage("§cGroup name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a group name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean rename(Player player, String[] args){
        if(player.hasPermission("g.rename")){
            if(args.length > 1){
                String name = args[1];
                if(name.length() < 13 && name.length() > 1){
                    if(!isBannedName(name)){
                        MyGroup group = getPlayersGroup(player.getUniqueId());
                        if(group != null){
                            String oldName = group.getName();
                            if(group.rename(name, player.getUniqueId())){
                                renameGroup(oldName, name);
                                player.sendMessage("§7You have successfully created the group §c"+name+"§7.");

                            }else{
                                player.sendMessage("§cFailed to rename group.");
                            }
                        }else{
                            player.sendMessage("§cYou aren't a part of a group.");
                        }
                    }else{
                        player.sendMessage("§cGroup name is not allowed.");
                    }
                }else{
                    player.sendMessage("§cGroup name exceeds character requirements.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a group name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean changeOwnership(Player player, String[] args){
        if(player.hasPermission("g.chown")){
            if(args.length > 1){
                String name = args[1];

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        group.changeOwnership(player.getUniqueId(), receiver.getUniqueId());
                    }else{
                        player.sendMessage("§cPlayer specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to change group ownership to.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean disband(Player player){
        if(player.hasPermission("g.disband")){
            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group != null){
                if(group.disband(player.getUniqueId())){
                    deleteGroup(group);
                    player.sendMessage("§7You have successfully disbanded the group §c"+group.getName()+"§7.");

                }else{
                    player.sendMessage("§cYou must be group owner to disband group.");
                }
            }else{
                player.sendMessage("§cYou are not a part of a group.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean invite(Player player, String[] args){
        if(player.hasPermission("g.invite")){
            if(args.length > 1){
                String name = args[1];

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    Player receiver = Bukkit.getPlayer(name);
                    if(receiver != null && player.isOnline()){
                        if(group.canInvite(player.getUniqueId(), receiver.getUniqueId())){
                            inviteToGroup(receiver.getUniqueId(), group.getKey());

                            player.sendMessage("§7You have invited §a"+receiver.getName()+"§7 to the group.");
                            receiver.sendMessage("§a"+player.getName()+"§7 has invited you to the group §c"+group.getName()+"§7.");

                            setPlayerInviteTask(receiver, Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                                @Override
                                public void run(){
                                    if(hasInviteToGroup(receiver.getUniqueId())){
                                        removeInviteToGroup(receiver.getUniqueId());
                                        removePlayerInviteTask(receiver);

                                        player.getPlayer().sendMessage("§c"+receiver.getName()+"§7 invitation has expired.");
                                        player.getPlayer().sendMessage("§7Invite to the group §c"+group.getName()+"§7 has expired.");
                                    }
                                }
                            }, 600));

                        }else{
                            player.sendMessage("§cYou must be a group recruit to invite players.");
                        }
                    }else{
                        player.sendMessage("§cThe player you specified ether doesn't exist or is not online.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a group name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean join(Player player){
        if(player.hasPermission("g.join")){
            MyGroup group = getGroupInvite(player.getUniqueId());
            if(group != null){
                group.join(player.getUniqueId());
                addPlayerToGroup(player.getUniqueId(), group.getKey());
                player.sendMessage("§7You have joined the group §a"+group.getName()+"§7.");

                for(String uuid : group.getPlayers()){
                    OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId())){
                        member.getPlayer().sendMessage("§a"+player.getName()+"§7 has joined the group!");
                    }
                }
            }else{
                player.sendMessage("§cYou have no group invites.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean remove(Player player, String[] args){
        if(player.hasPermission("g.remove")){
            if(args.length > 1){
                String name = args[1];

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(group.remove(player.getUniqueId(), receiver.getUniqueId())){
                            removePlayerFromGroup(receiver.getUniqueId());
                            player.sendMessage("§7You have removed §a"+receiver.getName()+" from the group.");

                            for(String uuid : group.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§c"+receiver.getName()+"§7 has been kicked from the group!");
                                }
                            }

                            if(receiver.isOnline()){
                                receiver.getPlayer().sendMessage("§cYou have been kicked from the group!");
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a group admin to remove players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to remove from the group.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean leave(Player player){
        if(player.hasPermission("g.leave")){
            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group != null){
                if(group.leave(player.getUniqueId())){
                    removePlayerFromGroup(player.getUniqueId());
                    player.sendMessage("§7You have left the group §c"+group.getName()+"§7!");

                    for(String uuid : group.getPlayers()){
                        OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                        if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId())){
                            member.getPlayer().sendMessage("§c"+player.getName()+"§7 has been left the group!");
                        }
                    }
                }else{
                    player.sendMessage("§cGroup owners cannot leave the group, only change ownership or disband.");
                }
            }else{
                player.sendMessage("§cYou are not a part of a group.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean promote(Player player, String[] args){
        if(player.hasPermission("g.promote")){
            if(args.length > 1){
                String name = args[1];

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(group.promote(player.getUniqueId(), receiver.getUniqueId())){
                            String[] names = getRanks();
                            int rank = group.getRank(receiver.getUniqueId());
                            player.sendMessage("§7You have promoted §a"+receiver.getName()+"§7 to §a"+names[rank]+"§7.");

                            for(String uuid : group.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§a"+receiver.getName()+"§7 has been promoted to §a"+names[rank]+"§7.");
                                }
                            }
                            return true;

                        }else{
                            player.sendMessage("§cYou must be at least a group admin to promote players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to promote.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean demote(Player player, String[] args){
        if(player.hasPermission("g.demote")){
            if(args.length > 1){
                String name = args[1];

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    OfflinePlayer receiver = getPlayer(name);

                    if(receiver != null){
                        if(group.demote(player.getUniqueId(), receiver.getUniqueId())){
                            String[] names = getRanks();
                            int rank = group.getRank(receiver.getUniqueId());
                            player.sendMessage("§7You have demoted §a"+receiver.getName()+"§7 to §a"+names[rank]+"§7.");

                            for(String uuid : group.getPlayers()){
                                OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(member != null && member.isOnline() && !uuid.equals(player.getUniqueId().toString())){
                                    member.getPlayer().sendMessage("§a"+receiver.getName()+"§7 has been demoted to §a"+names[rank]+"§7.");
                                }
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a group admin to demote players.");
                        }
                    }else{
                        player.sendMessage("§cThe player specified doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a player you wish to demote.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean claim(Player player, String[] args){
        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("g.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    claimChunk(player, chunk, getSafeZone());
                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    claimChunk(player, chunk, getPvpZone());
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("g.claim")){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    claimChunk(player, chunk, group);
                }else{
                    player.sendMessage("§cYou must be a part of a group to claim.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean autoClaim(Player player, String[] args){
        if(isAutoClaiming(player.getUniqueId())){
            if(getAutoClaim(player.getUniqueId()).isClaiming()){
                stopAutoClaiming(player.getUniqueId());
                player.sendMessage("§7You are no longer §cauto claiming§7.");
            }else{
                player.sendMessage("§7You must turn off §cauto unclaim§7 to auto claim.");
            }
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("g.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    startAutoClaiming(player.getUniqueId(), getSafeZone(), true);
                    claimChunk(player, chunk, getSafeZone());
                    player.sendMessage("§7You are now §aauto claiming§7 for Safe Zones.");

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    startAutoClaiming(player.getUniqueId(), getPvpZone(), true);
                    claimChunk(player, chunk, getPvpZone());
                    player.sendMessage("§7You are now §aauto claiming§7 for PVP Zones.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("g.autoclaim")){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    startAutoClaiming(player.getUniqueId(), group, true);
                    claimChunk(player, chunk, group);
                    player.sendMessage("§7You are now §aauto claiming§7 for "+group.getName()+".");
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean unclaim(Player player, String[] args){
        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("g.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    unclaimChunk(player, chunk, getSafeZone());

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    unclaimChunk(player, chunk, getPvpZone());
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("g.unclaim")){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    unclaimChunk(player, chunk, group);
                }else{
                    player.sendMessage("§cYou must be a part of a group to unclaim.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean autoUnclaim(Player player, String[] args){
        if(isAutoClaiming(player.getUniqueId())){
            if(!getAutoClaim(player.getUniqueId()).isClaiming()){
                stopAutoClaiming(player.getUniqueId());
                player.sendMessage("§7You are no longer §cauto unclaiming§7.");
            }else{
                player.sendMessage("§7You must turn off §cauto claim§7 to auto unclaim.");
            }
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(args.length > 1){
            if(player.hasPermission("g.admin") || player.isOp()){
                if(args[1].equalsIgnoreCase("safezone") || args[1].equalsIgnoreCase("safe-zone")){
                    startAutoClaiming(player.getUniqueId(), getSafeZone(), false);
                    claimChunk(player, chunk, getSafeZone());
                    player.sendMessage("§7You are now §aauto unclaiming§7 for Safe Zones.");

                }else if(args[1].equalsIgnoreCase("pvpzone") || args[1].equalsIgnoreCase("pvp-zone")){
                    startAutoClaiming(player.getUniqueId(), getPvpZone(), false);
                    claimChunk(player, chunk, getPvpZone());
                    player.sendMessage("§7You are now §aauto unclaiming§7 for PVP Zones.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }

        }else{
            if(player.hasPermission("g.autounclaim")){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    startAutoClaiming(player.getUniqueId(), group, false);
                    claimChunk(player, chunk, group);
                    player.sendMessage("§7You are now §aauto unclaiming§7 for "+group.getName()+".");
                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
        }
        return true;
    }

    private boolean rank(Player player){
        if(player.hasPermission("g.rank")){
            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group != null){
                String[] names = getRanks();
                player.sendMessage("§7Your rank in the group is: §a"+names[group.getRank(player.getUniqueId())]+"§7.");

            }else{
                player.sendMessage("§cYou are not a part of a group.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean power(Player player){
        if(player.hasPermission("g.power")){
            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group != null){
                if(group.getPower() > 0){
                    player.sendMessage("§7Your groups power level is: §a"+group.getPower()+"§7.");
                }else{
                    player.sendMessage("§7Your groups power level is: §c"+group.getPower()+"§7.");
                }

            }else{
                player.sendMessage("§cYou are not a part of a group.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setPower(Player player, String[] args){
        if(args.length > 2){
            if(player.hasPermission("g.admin") || player.isOp()){
                String name = args[1];

                MyGroup group = getGroupFromName(name);
                if(group != null){
                    int power = Integer.parseInt(args[2]);
                    group.setPower(power);
                    player.sendMessage("§7You have set §c"+group.getName()+"§7 power level to §a"+power+"§7.");

                }else{
                    player.sendMessage("§cGroup specified doesn't exist.");
                }
            }else{
                player.sendMessage("§cYou don't have permission to perform this command.");
            }
            return true;
        }else{
            player.sendMessage("§cPlease specify a group name and a power level.");
        }
        return false;
    }

    private boolean buyPower(Player player, String[] args){
        if(args.length > 1){
            MyGroup group = getPlayersGroup(player.getUniqueId());
            if(group != null){
                try{
                    int amount = Integer.parseInt(args[1]);

                    if(player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)){
                        if(player.getInventory().getItemInMainHand().getAmount() >= amount){
                            int power = group.getPower()+amount;
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-amount);
                            group.setPower(group.getPower()+amount);
                            player.sendMessage("§7You have purchased power, your power is now: §a"+power+".");
                            return true;

                        }else{
                            player.sendMessage("§cYou are holding less diamonds than the amount specified.");
                        }
                    }else{
                        player.sendMessage("§cYou are not holding any diamonds.");
                    }
                }catch(Exception e){
                    player.sendMessage("§cAmount specified was not a number.");
                }
            }else{
                player.sendMessage("§cYou are not a part of any group.");
            }
        }else{
            player.sendMessage("§cPlease specify the amount of diamonds you wish to trade for.");
        }

        return false;
    }

    private boolean setDescription(Player player, String[] args){
        if(player.hasPermission("g.setdesc")){
            if(args.length > 1){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    String builder = "";
                    for(int i = 1; i < args.length; i++){
                        builder += args[i]+" ";
                    }

                    builder = builder.replaceAll("&", "§");
                    if(group.setDescription(player.getUniqueId(), builder.substring(0, builder.length()-1))){
                        player.sendMessage("§7You have set the groups description§7.");
                    }else{
                        player.sendMessage("§cYou must be at least a group admin to set groups color.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease state a description for your group.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean setColor(Player player, String[] args){
        if(player.hasPermission("g.setcolor")){
            if(args.length > 1){
                String color = args[1].toUpperCase();

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    int colorCode = getColorCode(color);
                    if(group.setColor(player.getUniqueId(), colorCode)){
                        player.sendMessage("§7You have set the group color to "+getChatColor(colorCode)+color+"§7.");

                    }else{
                        player.sendMessage("§cYou must be at least a group admin to set groups color.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a color for your group.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean chat(Player player){
        if(player.hasPermission("g.chat")){
            if(isChatting(player.getUniqueId())){
                stopChatting(player.getUniqueId());
                player.sendMessage("§7Your now chatting §aglobally§7.");

            }else if(isPlayerInGroup(player.getUniqueId())){
                startChatting(player.getUniqueId());
                player.sendMessage("§7Your now chatting with only §agroup§7 members of your group.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean map(Player player){
        if(player.hasPermission("g.map")){
            if(isMapping(player.getUniqueId())){
                stopMapping(player);
                player.sendMessage("§7Your are no longer §cmapping§7 claimed chunks.");

            }else{
                startMapping(player);
                player.sendMessage("§7Your are now §amapping§7 claimed chunks.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean home(Player player){
        if(player.hasPermission("g.home")){
            if(isGroupHome()){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    Location home = group.getHome();
                    if(home != null){
                        teleport(player, home, "group Home", getColorRGB(group.getColor()));

                    }else{
                        player.sendMessage("§cYour group doesn't have a home set.");
                    }
                }else{
                    player.sendMessage("§cYour not a part of a group.");
                }
            }else{
                player.sendMessage("§cServer has group homes disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setHome(Player player){
        if(player.hasPermission("g.sethome")){
            if(isGroupHome()){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    Chunk chunk = player.getLocation().getChunk();
                    if(inClaim(chunk)){
                        Claim claim = getClaim(chunk);

                        if(group.getKey().equals(claim.getKey())){
                            if(group.canClaim(player.getUniqueId())){
                                group.setHome(player.getLocation());
                                player.sendMessage("§7Your have set your groups §ahome§7.");

                            }else{
                                player.sendMessage("§cYou must be at least group admin to set group home.");
                            }
                        }else{
                            player.sendMessage("§cYou can only set groups home in your own claim.");
                        }
                    }else{
                        player.sendMessage("§cYou can only set groups home in your own claim.");
                    }
                }else{
                    player.sendMessage("§cYour not a part of a group.");
                }
            }else{
                player.sendMessage("§cServer has group homes disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setFlag(Player player, String[] args){
        if(player.hasPermission("g.setflag")){
            if(args.length > 2){
                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    Claim claim = getClaim(player.getLocation().getChunk());
                    if(claim == null){
                        player.sendMessage("§cYou are not on a claimed chunk.");
                        return true;
                    }

                    String type = args[1];
                    try{
                        Flags flag = Flags.valueOf(args[2].toUpperCase());

                        switch(type){
                            case "add":
                                if(claim.addFlag(flag)){
                                    player.sendMessage("§7You have added the flag: §a"+flag+"§7.");
                                    modifiedClaim(claim);
                                    return true;
                                }
                                break;

                            case "remove":
                                if(claim.removeFlag(flag)){
                                    player.sendMessage("§7You have removed the flag: §c"+flag+"§7.");
                                    modifiedClaim(claim);
                                    return true;
                                }
                                break;
                        }

                        player.sendMessage("§cUnable to set flag.");
                        return true;
                    }catch(Exception e){
                        player.sendMessage("§cUnknown flag provided.");
                    }

                }else{
                    player.sendMessage("§cYou are not a part of a group.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify a warp name.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean getFlags(Player player){
        if(player.hasPermission("g.setflag")){
            Claim claim = getClaim(player.getLocation().getChunk());
            if(claim == null){
                player.sendMessage("§cYou are not on a claimed chunk.");
                return true;
            }

            if(claim.getTotalFlags() > 0){
                StringBuilder builder = new StringBuilder();
                List<Flags> flags = claim.getFlags();
                builder.append(flags.get(0).name());

                if(flags.size() > 1){
                    for(int i = 1; i < flags.size(); i++){
                        builder.append("§7, §a"+flags.get(i).name());
                    }
                }

                player.sendMessage("§7This claim has the following flags: §a"+builder.toString()+"§7.");
                return true;
            }

            player.sendMessage("§cThis claim has no flags.");
            return true;

        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean warp(Player player, String[] args){
        if(player.hasPermission("g.warp")){
            if(isGroupWarp()){
                if(args.length > 1){
                    MyGroup group = getPlayersGroup(player.getUniqueId());
                    if(group != null){
                        String warpName = args[1];
                        Location warp = group.getWarp(warpName);

                        if(warp != null){
                            teleport(player, warp, "group warp "+warpName, getColorRGB(group.getColor()));

                        }else{
                            player.sendMessage("§cYour group doesn't have a warp set with the name specified.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a group.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has group warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean setWarp(Player player, String[] args){
        if(player.hasPermission("g.setwarp")){
            if(isGroupWarp()){
                if(args.length > 1){
                    MyGroup group = getPlayersGroup(player.getUniqueId());
                    if(group != null){
                        String warpName = args[1];

                        if(warpName.length() < 13 && warpName.length() > 1){
                            if(group.canClaim(player.getUniqueId())){
                                if(inClaim(player.getLocation().getChunk())){
                                    Claim claim = getClaim(player.getLocation().getChunk());
                                    if(claim.getKey().equals(group.getKey())){
                                        if(group.getPower() >= getCreateWarpCost()){
                                            if(!group.isWarp(warpName)){
                                                group.setWarp(warpName, player.getLocation());
                                                player.sendMessage("§7You have set the group warp: §a"+warpName+"§7.");

                                            }else{
                                                player.sendMessage("§cYour group already has warp with this name.");
                                            }
                                        }else{
                                            player.sendMessage("§cYour group doesn't have enough power to set a warp.");
                                        }
                                    }else{
                                        player.sendMessage("§cYou can only set warps in your groups claims.");
                                    }
                                }else{
                                    player.sendMessage("§cYou can only set warps in your groups claims.");
                                }
                            }else{
                                player.sendMessage("§cYou must be at least a group admin to set warps.");
                            }
                        }else{
                            player.sendMessage("§cThe warp name exceeds character requirements.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a group.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has group warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean removeWarp(Player player, String[] args){
        if(player.hasPermission("g.delwarp")){
            if(isGroupWarp()){
                if(args.length > 1){
                    MyGroup group = getPlayersGroup(player.getUniqueId());
                    if(group != null){
                        String warpName = args[1];

                        if(group.canClaim(player.getUniqueId())){
                            if(group.isWarp(warpName)){
                                group.removeWarp(warpName);
                                player.sendMessage("§7You have removed the group warp: §a"+warpName+"§7.");

                            }else{
                                player.sendMessage("§cYour group doesn't have a warp set with the name specified.");
                            }
                        }else{
                            player.sendMessage("§cYou must be at least a group admin to set warps.");
                        }
                    }else{
                        player.sendMessage("§cYou are not a part of a group.");
                    }
                    return true;
                }else{
                    player.sendMessage("§cPlease specify a warp name.");
                }
            }else{
                player.sendMessage("§cServer has group warps disabled.");
                return true;
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    public boolean warps(Player player, String[] args){
        if(player.hasPermission("g.warps")){
            if(isGroupWarp()){
                int page = 0;
                if(args.length > 1){
                    page = Integer.parseInt(args[1]);
                }

                MyGroup group = getPlayersGroup(player.getUniqueId());
                if(group != null){
                    ArrayList<String> warps = group.getWarps();

                    if(warps != null && warps.size() > 0){
                        player.sendMessage("§c------- §fList of Group Warps (1/"+(((warps.size()/9)*page)+1)+") §c-------");

                        for(int i = page*9; i < (page+1)*9; i++){
                            if(i < warps.size()){
                                Location warp = group.getWarp(warps.get(i));
                                player.sendMessage("§c"+warps.get(i)+"§7: Warp is located in the world: §c"+warp.getWorld().getName()+"§7.");
                            }else{
                                break;
                            }
                        }
                    }else{
                        player.sendMessage("§cYour group has no warps.");
                    }
                }else{
                    player.sendMessage("§cYou are not in a group.");
                }
            }else{
                player.sendMessage("§cServer has group warps disabled.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }
}
