package me.jackz.missilewars;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScrambleCommand implements CommandExecutor {
    private MissileWars plugin;
    private Scoreboard sb;

    private Team redTeam;
    private Team greenTeam;

    ScrambleCommand(MissileWars plugin) {
        this.plugin = plugin;
        this.sb = plugin.getServer().getScoreboardManager().getMainScoreboard();

        redTeam = sb.getTeam("Red");
        greenTeam = sb.getTeam("Green");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }
        List<Player> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) continue;
            Team team = player.getScoreboard().getEntryTeam(player.getName());
            if(team != null && (team.getName().equalsIgnoreCase("red") || team.getName().equalsIgnoreCase("green"))) {
                team.removeEntry(player.getName());
                team = null;
            }
            //If not in a team (like spectator)
            if(team == null) {
                players.add(player);
            }
        }
        //2 / 2.0 -> 1
        int first_team_size = (int) Math.floor(players.size() / 2.0);
        boolean greenTeamOverflow = Math.random() > .5;

        Collections.shuffle(players);
        int first_team_players = 0;
        Team firstTeam = greenTeamOverflow ? greenTeam : redTeam;
        Team secondTeam = greenTeamOverflow ? redTeam : greenTeam;

        for (Player player : players) {
            if(first_team_players < first_team_size) {
                first_team_players++;
                firstTeam.addEntry(player.getName());
            }else{
                secondTeam.addEntry(player.getName());
            }
        }
        plugin.getDisplayManager().refreshSidebar();
        int second_team_players = players.size() - first_team_players;
        if(greenTeamOverflow) {
            sender.sendMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",first_team_players,second_team_players));
        }else{
            sender.sendMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",second_team_players,first_team_players));
        }
        return true;
    }
}
