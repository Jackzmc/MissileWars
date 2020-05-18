package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerStatsCommand implements CommandExecutor  {
    private MissileWars plugin;
    private Scoreboard sb;
    public PlayerStatsCommand(MissileWars plugin) {
        this.plugin = plugin;
        this.sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null) {
                printStats(sender,player);
            }else{
                sender.sendMessage("§cCould not find any players with that name.");
            }

        }else{
            if(sender instanceof Player) {
                Player player = (Player) sender;
                printStats(sender,player);
            }else{
                sender.sendMessage("§cYou must be a player to view your own statistics. Try /stats <player>");
            }
        }
        return true;
    }
    private void printStats(CommandSender sender, Player player) {
        int wins = GameManager.getStats().getSavedStat("wins." + player.getUniqueId());
        int loses = GameManager.getStats().getSavedStat("loses." + player.getUniqueId());
        if (wins == 0 && loses == 0) {
            if(sender == player || sender.getName().equals(player.getName())) {
                sender.sendMessage("§cYou have not played any games.");
            }else{
                sender.sendMessage("§cPlayer has not played any games");
            }
        }else{
            sender.sendMessage("§6§nGame Stats for " + player.getDisplayName());

            String msg = String.format("Has Won %d Games\nHas Lost %d Games", wins, loses);
            sender.sendMessage(msg);
        }

    }
}
