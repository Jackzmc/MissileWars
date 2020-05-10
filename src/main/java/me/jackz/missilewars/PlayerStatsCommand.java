package me.jackz.missilewars;

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
            printStats(sender,args[0]);
        }else{
            if(sender instanceof Player) {
                Player p = (Player) sender;
                printStats(sender,p.getName());
            }else{
                sender.sendMessage("§cYou must be a player to view your own statistics. Try /stats <player>");
            }
        }
        return true;
    }
    private void printStats(CommandSender sender, String player) {
        for (String entry : sb.getEntries()) {
            if(entry.equalsIgnoreCase(player)) {
                Score wins = sb.getObjective("wins").getScore(entry);
                Score loses = sb.getObjective("loses").getScore(entry);
                if(!wins.isScoreSet() && !loses.isScoreSet()) {
                    sender.sendMessage("§cPlayer has not played any games");
                    return;
                }
                int win_count = wins.isScoreSet() ? wins.getScore() : 0;
                int lose_count = loses.isScoreSet() ? loses.getScore() : 0;
                sender.sendMessage("§6Game Stats for " + entry);

                String msg = String.format("Has Won %d Games\nHas Lost %d Games",win_count,lose_count);
                sender.sendMessage(msg);
                return;
            }
        }
        sender.sendMessage("§cCould not find any players.");



        /*if(!wins.isScoreSet() && !loses.isScoreSet()) {
            sender.sendMessage("§cThere are no statistics for that player, or player does not exist.");
        }else{

        }*/
    }
}
