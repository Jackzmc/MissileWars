package me.jackz.missilewars.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.StatsTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WinManager {
    private final static String red_portal_region = "redportal";
    private final static String green_portal_region = "greenportal";

    public static void testWin(ApplicableRegionSet regions) {
        for (ProtectedRegion region : regions) {
            if(region.getId().equalsIgnoreCase(green_portal_region)) {
                announceWin(GamePlayers.MWTeam.RED);
                GameManager.getStats().incSavedStat("wins.team_red");
                //green wins
            }else if(region.getId().equalsIgnoreCase(red_portal_region)){
                //red wins
                announceWin(GamePlayers.MWTeam.GREEN);
                GameManager.getStats().incSavedStat("wins.team_green");
            }
        }
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
