package me.jackz.missilewars.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameManager {
    private MissileWars plugin;
    private GameState state;
    private GamePlayers players;
    private static GameConfig config;
    private static ItemSystem itemSystem;

    private final String red_portal_region = "redportal";
    private final String green_portal_region = "greenportal";

    public GameManager(MissileWars plugin) {
        this.state = new GameState();
        this.plugin = plugin;
        players = new GamePlayers();
        config = new GameConfig();
        initalizeScoreboard();
        itemSystem = new ItemSystem();
    }

    //#region privatemethods
    private void initalizeScoreboard() {
        Scoreboard main = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        if(main.getTeam("Red") == null) {
            Team red = main.registerNewTeam("Red");

            red.setColor(ChatColor.RED);
            red.setAllowFriendlyFire(false);
            red.setCanSeeFriendlyInvisibles(true);
            red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }
        if(main.getTeam("Green") == null) {
            Team green = main.registerNewTeam("Green");

            green.setColor(ChatColor.GREEN);
            green.setAllowFriendlyFire(false);
            green.setCanSeeFriendlyInvisibles(true);
            green.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }

        if(main.getObjective("wins") == null ) main.registerNewObjective("wins", "dummy", "Wins");
        if(main.getObjective("loses") == null ) main.registerNewObjective("loses", "dummy", "Loses");
    }
    //#endregion
    public void testWin(ApplicableRegionSet regions) {
        for (ProtectedRegion region : regions) {
            if(region.getId().equalsIgnoreCase(green_portal_region)) {
                //green wins
            }else if(region.getId().equalsIgnoreCase(red_portal_region)){
                //red wins
            }
        }
    }

    public void shutdown() {

    }

    public GameState getState() {
        return state;
    }

    public GamePlayers players() {
        return players;
    }
    public GameConfig getConfig() {
        return config;
    }
}
