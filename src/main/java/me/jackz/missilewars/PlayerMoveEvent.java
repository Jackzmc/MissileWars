package me.jackz.missilewars;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scoreboard.Team;

public class PlayerMoveEvent implements Listener {
    @EventHandler
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(location.getY() <= 0 && player.getGameMode() == GameMode.SURVIVAL) {
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if(player.getGameMode().equals(GameMode.SURVIVAL)) {
            Team team = player.getScoreboard().getEntryTeam(player.getName());
            if(team != null && (team.getName().equalsIgnoreCase("Red") || team.getName().equalsIgnoreCase("Green"))) {
                if(e.getItemDrop().getItemStack().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                }
                //e.setCancelled(true);
            }
        }
    }

}
