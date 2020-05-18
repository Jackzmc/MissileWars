package me.jackz.missilewars.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.StatsTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class WinManager {
    private final static String red_portal_region = "redportal";
    private final static String green_portal_region = "greenportal";

    public static void testWin(ApplicableRegionSet regions) {
        GamePlayers.MWTeam team = GamePlayers.MWTeam.NONE;
        for (ProtectedRegion region : regions) {
            if(region.getId().equalsIgnoreCase(green_portal_region)) {
                //green wins
                team = GamePlayers.MWTeam.RED;
                break;
            }else if(region.getId().equalsIgnoreCase(red_portal_region)){
                //red wins
                team = GamePlayers.MWTeam.GREEN;
                break;
            }
        }
        if(team != GamePlayers.MWTeam.NONE) {
            announceWin(team);
            updateStats(team);
        }
    }

    private static void updateStats(GamePlayers.MWTeam winning) {
        StatsTracker stats = GameManager.getStats();
        if(winning == GamePlayers.MWTeam.GREEN) {
            stats.incSavedStat("wins.team_green");
            stats.incSavedStat("loses.team_red");
        }else{
            stats.incSavedStat("wins.team_red");
            stats.incSavedStat("loses.team_green");
        }
        for (Map.Entry<Player, GamePlayers.MWTeam> playerMWTeamEntry : MissileWars.gameManager.players().getAll()) {
            GamePlayers.MWTeam team = playerMWTeamEntry.getValue();
            Player player = playerMWTeamEntry.getKey();
            //If team was winning team
            if (team == winning) {
                stats.incSavedStat("wins." + player.getUniqueId());
            } else {
                stats.incSavedStat("loses." + player.getUniqueId());
            }
        }
        stats.save();
    }

    private static void announceWin(GamePlayers.MWTeam team) {
        ChatColor color = team == GamePlayers.MWTeam.GREEN ? ChatColor.GREEN : ChatColor.RED;
        String name = team == GamePlayers.MWTeam.GREEN ? "Green" : "Red";
        String loserTeamName = (team == GamePlayers.MWTeam.GREEN) ? "red" : "green";

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(color.toString() + name + " Team Wins!","The " + loserTeamName + " team's portal has been destroyed",0,160,0);
        }
        Bukkit.broadcastMessage(color.toString() + name + " team §9has won the game!");
        MissileWars.gameManager.end();
    }
}
