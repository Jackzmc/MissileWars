package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.game.ItemSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

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
        if(player.getGameMode().equals(GameMode.SURVIVAL) && !e.isCancelled()) {
            GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
            if(team != GamePlayers.MWTeam.NONE) {
                //if(e.getItemDrop().getItemStack().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                //}
                //e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupArrowEvent e) {
        if(!e.isCancelled()) {
            ItemStack arrow = ItemSystem.getItem("arrow");
            e.getItem().setItemStack(arrow);
        }
    }

}
