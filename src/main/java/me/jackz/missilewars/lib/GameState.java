package me.jackz.missilewars.lib;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameState {


    private boolean activeState = false;
    private boolean legacy_missiles = false;
    private boolean debug_enabled = false;



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


