package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.StatsTracker;
import me.jackz.missilewars.lib.Missile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerStatsCommand implements TabExecutor {
    private MissileWars plugin;
    public PlayerStatsCommand(MissileWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0 && !args[0].equalsIgnoreCase("session")) {
            if(sender.hasPermission("missilewars.stats.other")) {


            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if(player.hasPlayedBefore()) {
                boolean global = true;
                if(args.length > 1 && args[1].equalsIgnoreCase("session")) {
                    global = false;
                }
                printStats(sender, player.getUniqueId(), player.getName(), global);
            }else{
                sender.sendMessage("§cThat player has no recorded statistics.");
            }
            }else {
                sender.sendMessage("§cYou do not have permission.");
            }
        }else{
            if(sender.hasPermission("missilewars.stats")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    boolean global = true;
                    if (args.length > 0 && args[0].equalsIgnoreCase("session")) {
                        global = false;
                    }
                    printStats(sender, player.getUniqueId(), player.getName(), global);
                } else {
                    sender.sendMessage("§cYou must be a player to view your own statistics. Try /stats <player>");
                }
            }else{
                sender.sendMessage("§cYou do not have permission.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        /*
        /stats <player>, /stats session, /stats <player> session
         */
        if(sender.hasPermission("missilewars.stats.others") || args.length == 0 || !args[0].equalsIgnoreCase("session")) {
            if(args.length > 1) {
                if(args.length > 2) return new ArrayList<>();
                return new ArrayList<>(Collections.singletonList("session"));
            }
            return null;
        }else {
            return new ArrayList<>();
        }
    }

    private void printStats(CommandSender sender, UUID uuid, String username, boolean global) {
        StatsTracker.StatisticType type = global ? StatsTracker.StatisticType.Saved : StatsTracker.StatisticType.Session;
        int wins = GameManager.getStats().get(type, "wins", uuid.toString());
        int loses = GameManager.getStats().get(type, "loses", uuid.toString());
        if (wins == 0 && loses == 0) {
            if(sender.getName().equals(username)) {
                sender.sendMessage("§cYou have not played any games.");
            }else{
                sender.sendMessage("§cPlayer has not played any games");
            }
        }else{
            String deaths = GameManager.getStats().getFormatted(type, "generic.deaths", uuid);
            String shield_spawns = GameManager.getStats().getFormatted(type, "spawns.barrier", uuid);
            String fireball_launches = GameManager.getStats().getFormatted(type, "spawns.fireball", uuid);
            String minutes_played = GameManager.getStats().getFormatted(type, "gametime_min", uuid);
            List<BaseComponent> components = new ArrayList<>(Arrays.asList(
                    new TextComponent("§6§nGame Statistics for " + username),
                    new TextComponent(String.format("\n§e%d Wins §9and §e%d Loses", wins, loses)),
                    new TextComponent("\n§9Minutes Played: §e" + minutes_played),
                    new TextComponent("\n§9Deaths: §e" + deaths),
                    new TextComponent("\n§9Fireball Launches: §e" + fireball_launches),
                    new TextComponent("\n§9Barriers Deployed: §e" + shield_spawns)
            ));
            for (Missile missile : GameManager.getMissileLoader().getMissiles()) {
                String spawns = GameManager.getStats().getFormatted(type, "spawns", missile.getId());
                TextComponent tc = new TextComponent("\n§9" + missile.getDisplay() + "s Deployed: §e" + spawns);
                components.add(tc);
            }
            
            BaseComponent[] componentArray = new BaseComponent[components.size()];
            components.toArray(componentArray);
            sender.spigot().sendMessage(componentArray);
        }
    }

}
