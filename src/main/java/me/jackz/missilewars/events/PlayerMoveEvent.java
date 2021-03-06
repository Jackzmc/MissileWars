package me.jackz.missilewars.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.game.ItemSystem;
import me.jackz.missilewars.game.Options;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerMoveEvent implements Listener {
    @EventHandler
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(location.getY() <= 0 && player.getGameMode() == GameMode.SURVIVAL) {
            if((boolean) Options.teleportRespawn.getValue()) {
                GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
                Location tpLocation = team == GamePlayers.MWTeam.GREEN ? GameConfig.GREEN_SPAWNPOINT : GameConfig.RED_SPAWNPOINT;
                player.teleport(tpLocation);
                player.setVelocity(new Vector(0, 0, 0));
            }else {
                player.setHealth(0);
            }
        } else if(location.distance(GameConfig.SPAWN_LOCATION) <= 2 && player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.ADVENTURE);
        } else if(player.getGameMode() == GameMode.ADVENTURE) {
            BlockVector3 blockVector3 = BukkitAdapter.adapt(location).toVector().toBlockPoint();
            ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getApplicableRegions(blockVector3);
            for (ProtectedRegion region : regions) {
                if(region.getId().equalsIgnoreCase("redteamjoin")) {
                    MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.RED);
                }else if(region.getId().equalsIgnoreCase("greenteamjoin")) {
                    MissileWars.gameManager.players().joinPlayer(player, GamePlayers.MWTeam.GREEN);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if(e.isCancelled()) return;
        if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
            e.setCancelled(true);
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
