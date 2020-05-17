package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.GamePlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameCommand implements CommandExecutor  {
    private Scoreboard sb;
    private MissileWars plugin;
    public GameCommand(MissileWars plugin) {
        this.plugin = plugin;
        sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
    }
    /*
    todo:
    check if team players are online
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 2 && args[0].equalsIgnoreCase("join")) {
            Player player;
            if(args.length >= 3) {
                player = Bukkit.getPlayer(args[3]);
                if(player == null) {
                    sender.sendMessage("§cCould not find that player");
                    return true;
                }
            }else{
                if(sender instanceof Player) {
                    sender.sendMessage("§cCan not use this command in console. You can specify a player instead");
                    return true;
                }else{
                    player = (Player) sender;
                }
            }
            if (args[1].equalsIgnoreCase("green")) {
                MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.GREEN);
            } else if (args[1].equalsIgnoreCase("red")) {
                MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.RED);
            }
            if(sender instanceof Player) {

            }
            return true;
        }

        //todo: fetch stat
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

    private boolean isPlayerOnline(String username) {
        return plugin.getServer().getPlayer(username.toLowerCase()) != null;
    }
}
