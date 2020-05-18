package me.jackz.missilewars.game;

public class GameState {


    private boolean activeState = true;
    private boolean legacy_missiles = false;
    private boolean debug_enabled = false;

    private boolean redTeamReady = false;
    private boolean greenTeamReady = false;



    //#region getters
    public boolean isGameActive() {
        return activeState;
    }
    public boolean isDebug() {
        return debug_enabled;
    }
    public boolean isLegacyMissilesEnabled() {
        return legacy_missiles;
    }
    //#endregion
    //#region setters
    public void setLegacyMissiles(boolean legacy_missiles) {
        this.legacy_missiles = legacy_missiles;
    }
    public void setActive(boolean bool) {
        activeState = bool;
    }

    public boolean isGameReady() {
        return redTeamReady && greenTeamReady;
    }
    public void setTeamReady(GamePlayers.MWTeam team, boolean value) {
        if(team == GamePlayers.MWTeam.RED) {
            redTeamReady = value;
        }else {
            greenTeamReady = value;
        }
    }
    //#endregion


}


