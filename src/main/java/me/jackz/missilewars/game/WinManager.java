package me.jackz.missilewars.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WinManager {
    private final static String red_portal_region = "redportal";
    private final static String green_portal_region = "greenportal";

    public static void testWin(ApplicableRegionSet regions) {
        for (ProtectedRegion region : regions) {
            if(region.getId().equalsIgnoreCase(green_portal_region)) {
                announceWin(GamePlayers.MWTeam.GREEN);
                //green wins
            }else if(region.getId().equalsIgnoreCase(red_portal_region)){
                //red wins
                announceWin(GamePlayers.MWTeam.RED);
            }
        }
    }
    static void announceWin(GamePlayers.MWTeam team) {
        ChatColor color = team == GamePlayers.MWTeam.GREEN ? ChatColor.GREEN : ChatColor.RED;
        String name = team == GamePlayers.MWTeam.GREEN ? "Green" : "Red";

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(color.toString() + name + " Wins!","",0,100,0);
        }
        Bukkit.broadcastMessage(color.toString() + name + " §9team has won the game!");
        //wait 30s
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> MissileWars.gameManager.reset(),20 * 30);
    }
}
