package me.jackz.missilewars.game;

public class GameState {


    private boolean activeState = false;
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
    //#endregion
    //#region setters
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


