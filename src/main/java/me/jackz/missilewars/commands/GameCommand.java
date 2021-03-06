package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.*;
import me.jackz.missilewars.lib.ConfigOption;
import me.jackz.missilewars.lib.Missile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameCommand implements CommandExecutor  {
    private MissileWars plugin;
    private ConfigOption isMidGameJoinAllowed;

    public GameCommand(MissileWars plugin) {
        this.plugin = plugin;
        isMidGameJoinAllowed = Options.midGameJoins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("join")) {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /game join <red/green>");
                    return true;
                } else if (Reset.isResetting()) {
                    sender.sendMessage("§cGame is currently resetting, please wait.");
                    return true;
                }
                boolean self = false;
                Player player;
                if (args.length >= 3) {
                    if (!sender.hasPermission("missilewars.join.others") && !sender.isOp()) {
                        sender.sendMessage("§cYou do not have permission.");
                        return true;
                    }
                    player = Bukkit.getPlayer(args[3]);
                    if (player == null) {
                        sender.sendMessage("§cCould not find that player");
                        return true;
                    }
                } else {
                    if (sender instanceof Player) {
                        if (sender.hasPermission("missilewars.join")) {
                            player = (Player) sender;
                            self = true;
                        } else {
                            sender.sendMessage("§cYou do not have permission.");
                            return true;
                        }
                    } else {
                        sender.sendMessage("§cCan not use this command in console. You can specify a player instead");
                        return true;
                    }
                }
                if (self && (boolean) isMidGameJoinAllowed.getValue() && MissileWars.gameManager.getState().isGameActive()) {
                    if (MissileWars.gameManager.players().has(player)) {
                        sender.sendMessage("§cCan't switch teams during a game.");
                    } else {
                        sender.sendMessage("§cCan't join a game that is in session");
                    }
                } else {
                    if (args[1].equalsIgnoreCase("green")) {
                        MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.GREEN);
                    } else if (args[1].equalsIgnoreCase("red")) {
                        MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.RED);
                    }
                }
                return true;
            }else if(args[0].equalsIgnoreCase("leave")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if(MissileWars.gameManager.players().has(player)) {
                        MissileWars.gameManager.players().remove(player);
                        MissileWars.gameManager.players().setupPlayer(player);
                        player.setGameMode(GameMode.ADVENTURE);
                        player.teleport(GameConfig.SPAWN_LOCATION);
                    }else{
                        sender.sendMessage("§cYou are not currently in a game.");
                    }
                } else {
                    sender.sendMessage("§cCan not use this command in console. ");
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("stats")) {
                if(sender.hasPermission("missilewars.stats")) {
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("global")) {
                            if(sender.hasPermission("missilewars.stats.global")) {
                                sender.spigot().sendMessage(getTotalStatComponents(true));
                                return true;
                            }else{
                                sender.sendMessage("§cYou do not have permission.");
                            }
                        } else if (args[1].equalsIgnoreCase("session")) {
                            sender.spigot().sendMessage(getTotalStatComponents(false));
                            return true;
                        }
                    }
                }else{
                    sender.sendMessage("§cYou do not have permission.");
                }
                sender.sendMessage("§cUsage: /game stats <global/session>");
                return true;
            }
        }

        int total_red_wins = GameManager.getStats().getSavedStat("wins.team_red");
        int total_green_wins = GameManager.getStats().getSavedStat("wins.team_green");

        Set<String> green_players = MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN).stream().filter(OfflinePlayer::isOnline).map(HumanEntity::getName).collect(Collectors.toSet());
        Set<String> red_players   = MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED).stream().filter(OfflinePlayer::isOnline).map(HumanEntity::getName).collect(Collectors.toSet());
        // if (game active) { */
        sender.sendMessage(String.format("§cRed Team (%d wins)", total_red_wins));
        if (red_players.size() > 0) {
            sender.sendMessage(ChatColor.GRAY + String.join(", ", red_players));
        } else {
            sender.sendMessage("§7There are no players on the red team");
        }
        sender.sendMessage("");
        sender.sendMessage(String.format("§aGreen Team (%d wins)", total_green_wins));
        if (green_players.size() > 0) {
            sender.sendMessage(ChatColor.GRAY + String.join(", ", green_players));
        } else {
            sender.sendMessage("§7There are no players on the green team");
        }

        // }
        return true;
    }

    private BaseComponent[] getTotalStatComponents(boolean global) {
        StatsTracker stats = GameManager.getStats();
        int green_wins = stats.getStat("wins.team_green", global);
        int red_wins = stats.getStat("wins.team_red", global);

        int green_loses = stats.getStat("loses.team_green", global);
        int red_loses = stats.getStat("loses.team_red", global);

        int deaths = stats.getStat("generic.deaths.total", global);
        int shield_spawns = stats.getStat("spawns.barrier.total", global);
        int fireball_launches = stats.getStat("spawns.fireball.total", global);
        int minutes_played = stats.getStat("gametime_min.total", global);
        int minutes_played_highest = stats.getStat("gametime_min.longest", global);
        List<BaseComponent> components = new ArrayList<>(Arrays.asList(
                new TextComponent("§6§n" + (global? "Global" : "Session") + " Statistics"),
                new TextComponent(String.format("\n§9Minutes Played Total: §e%d §9(Highest per game: §e%d§9)",minutes_played, minutes_played_highest)),
                new TextComponent("\n§9Deaths: §e" + deaths),
                new TextComponent("\n§9Fireball Launches: §e" + fireball_launches),
                new TextComponent("\n§9Barriers Deployed: §e" + shield_spawns)
        ));
        if(global) {
            components.add(1, new TextComponent(String.format("\n§aGreen -> Wins: §e%d §a| Loses: §e%d", green_wins, green_loses)));
            components.add(2, new TextComponent(String.format("\n§cRed    -> Wins: §e%d §c| Loses: §e%d", red_wins, red_loses)));
        }
        for (Missile missile : GameManager.getMissileLoader().getMissiles()) {
            int spawns = GameManager.getStats().getStat("spawns." + missile.getId() + ".total",global);
            TextComponent tc = new TextComponent("\n§9" + missile.getDisplay() + "s Deployed: §e" + spawns);
            components.add(tc);
        }
        BaseComponent[] componentArray = new BaseComponent[components.size()];
        components.toArray(componentArray);
        return componentArray;
    }
}
