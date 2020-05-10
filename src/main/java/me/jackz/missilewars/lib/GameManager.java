package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameManager {
    private MissileWars plugin;
    private boolean activeState = false;
    private boolean legacy_missiles = false;
    private boolean debug_enabled = false;

    public GameManager(MissileWars plugin) {

        this.plugin = plugin;
        initalizeScoreboard();
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

    public void shutdown() {

    }

    //#region getters
    public boolean isGameActive() {
        return activeState;
    }

    public boolean isLegacyMissilesEnabled() {
        return legacy_missiles;
    }
    //#endregion
    //#region setters
    public void setLegacyMissiles(boolean legacy_missiles) {
        this.legacy_missiles = legacy_missiles;
    }

    public boolean isDebug() {
        return debug_enabled;
    }
    //#endregion
}
