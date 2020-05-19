package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerStatsCommand implements CommandExecutor  {
    private MissileWars plugin;
    public PlayerStatsCommand(MissileWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0 && !args[0].equalsIgnoreCase("session")) {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null) {
                boolean global = true;
                if(args.length > 1 && args[1].equalsIgnoreCase("session")) {
                    global = false;
                }
                printStats(sender,player, global);
            }else{
                sender.sendMessage("§cCould not find any players with that name.");
            }

        }else{
            if(sender instanceof Player) {
                Player player = (Player) sender;
                boolean global = true;
                if(args.length > 0 && args[0].equalsIgnoreCase("session")) {
                    global = false;
                }
                printStats(sender, player, global);
            }else{
                sender.sendMessage("§cYou must be a player to view your own statistics. Try /stats <player>");
            }
        }
        return true;
    }
    private void printStats(CommandSender sender, Player player, boolean global) {
        int wins = GameManager.getStats().getStat("wins." + player.getUniqueId(), global);
        int loses = GameManager.getStats().getStat("loses." + player.getUniqueId(), global);
        if (wins == 0 && loses == 0) {
            if(sender == player || sender.getName().equals(player.getName())) {
                sender.sendMessage("§cYou have not played any games.");
            }else{
                sender.sendMessage("§cPlayer has not played any games");
            }
        }else{
            int deaths = GameManager.getStats().getStat("generic.deaths." + player.getUniqueId(), global);
            int shield_spawns = GameManager.getStats().getStat("spawns.barrier." + player.getUniqueId(), global);
            int fireball_launches = GameManager.getStats().getStat("spawns.fireball." + player.getUniqueId(), global);
            int tomahawk_spawns = GameManager.getStats().getStat("spawns.tomahawk." + player.getUniqueId(), global);
            int juggernaut_spawns = GameManager.getStats().getStat("spawns.juggernaut." + player.getUniqueId(), global);
            int guardian_spawns = GameManager.getStats().getStat("spawns.guardian." + player.getUniqueId(), global);
            int lightning_spawns = GameManager.getStats().getStat("spawns.lightning." + player.getUniqueId(), global);
            int shieldbuster_spawns = GameManager.getStats().getStat("spawns.shieldbuster." + player.getUniqueId(), global);
            int minutes_played = GameManager.getStats().getStat("gametime_min." + player.getUniqueId(), global);
            List<BaseComponent> components = new ArrayList<>(Arrays.asList(
                    new TextComponent("§6§nGame Statistics for " + player.getDisplayName()),
                    new TextComponent(String.format("\n§e%d Wins §9and §e%d Loses", wins, loses)),
                    new TextComponent("\n§9Minutes Played: §e" + minutes_played),
                    new TextComponent("\n§9Deaths: §e" + deaths),
                    new TextComponent("\n§9Fireball Launches: §e" + fireball_launches),
                    new TextComponent("\n§9Barriers Deployed: §e" + shield_spawns),
                    new TextComponent("\n§9Tomahawks Deployed: §e" + tomahawk_spawns),
                    new TextComponent("\n§9Juggernauts Deployed: §e" + juggernaut_spawns),
                    new TextComponent("\n§9Guardians Deployed: §e" + guardian_spawns),
                    new TextComponent("\n§9Lightnings Deployed: §e" + lightning_spawns),
                    new TextComponent("\n§9Shieldbusters Deployed: §e" + shieldbuster_spawns)
            ));
            BaseComponent[] componentArray = new BaseComponent[components.size()];
            components.toArray(componentArray);
            sender.spigot().sendMessage(componentArray);
        }

    }
}
