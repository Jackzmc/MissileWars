package me.jackz.missilewars.lib;

import me.jackz.missilewars.game.GamePlayers;
import org.bukkit.Material;

public class Missile {
    private String id;
    private String display;
    private String hint;
    private Material material;
    private String schematic;

    public Missile(String name, Material material) {
        this.id = name;
        this.material = material;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        if(display != null) {
            return display;
        }else{
            return id.substring(0, 1).toUpperCase() + id.substring(1);
        }
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Material getMaterial() {
        return material;
    }

    public String getSchematic() {
        return schematic;
    }
    public String getFullSchematic(GamePlayers.MWTeam team) {
        return GamePlayers.getTeamName(team) + "-" + schematic;
    }

    public void setSchematic(String schematic) {
        this.schematic = schematic;
    }
}
