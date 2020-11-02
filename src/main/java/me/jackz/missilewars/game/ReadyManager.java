package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class ReadyManager {
    private static List<Player> readyUpPlayers = new ArrayList<>();

    public static boolean isReady(GamePlayers.MWTeam team) {
        boolean allReady = true;
        for (Player player : MissileWars.gameManager.players().get(team)) {
            if(!readyUpPlayers.contains(player)) {
                allReady = false;
                break;
            }
        }
        return allReady;
    }

    public static void readyPlayer(Player player) {
        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
        if(MissileWars.gameManager.players().has(player)) {
            if(!readyUpPlayers.contains(player)) {
                readyUpPlayers.add(player);
                player.sendMessage("§9You have readied up.");
                if(isReady(team)) {
                    MissileWars.gameManager.ready(team);
                }
            }
        }
    }

    public static void reset() {
        readyUpPlayers.clear();
    }
}
