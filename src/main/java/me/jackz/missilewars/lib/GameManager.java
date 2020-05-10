package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;

public class GameManager {
    private MissileWars plugin;
    private boolean activeState = false;
    private boolean legacy_missiles = false;
    private boolean debug_enabled = false;

    public GameManager(MissileWars plugin) {
        this.plugin = plugin;
    }

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
