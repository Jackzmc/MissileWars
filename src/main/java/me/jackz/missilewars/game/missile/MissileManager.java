package me.jackz.missilewars.game.missile;

import java.util.ArrayList;

public class MissileManager {
    private ArrayList<SpawnedMissile> missiles = new ArrayList<SpawnedMissile>();

    public void addMissile(SpawnedMissile missile) {
        this.missiles.add(missile);
    }


}
