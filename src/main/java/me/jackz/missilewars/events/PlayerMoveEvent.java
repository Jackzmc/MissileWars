package me.jackz.missilewars.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
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
        }else{
            BlockVector3 blockVector3 = BukkitAdapter.adapt(location).toVector().toBlockPoint();
            ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getApplicableRegions(blockVector3);
            for (ProtectedRegion region : regions) {
                if(region.getId().equalsIgnoreCase("redteamjoin")) {
                    MissileWars.gameManager.players().add(player, GamePlayers.MWTeam.RED);
                    player.setGameMode(GameMode.ADVENTURE);
                    MissileWars.gameManager.players().setupPlayer(player);
                    player.teleport(GameConfig.RED_LOBBY_SPAWNPOINT);

                    Bukkit.broadcastMessage("§c" + player.getName() + " joined the Red team!");
                }else if(region.getId().equalsIgnoreCase("greenteamjoin")) {
                    MissileWars.gameManager.players().add(player, GamePlayers.MWTeam.GREEN);
                    player.setGameMode(GameMode.ADVENTURE);
                    MissileWars.gameManager.players().setupPlayer(player);
                    player.teleport(GameConfig.GREEN_LOBBY_SPAWNPOINT);
                    Bukkit.broadcastMessage("§a" + player.getName() + " joined the Green team!");
                }
            }
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
