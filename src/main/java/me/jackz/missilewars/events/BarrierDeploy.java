package me.jackz.missilewars.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.game.ItemSystem;
import me.jackz.missilewars.lib.MWUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class BarrierDeploy implements Listener {
    private static final String nomanland_region = "nomanland";

    @EventHandler
    private void onSnowballThrow(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if(projectile.getType() == EntityType.SNOWBALL) {
            if(Math.random() < .05) {
                Entity firework = projectile.getWorld().spawnEntity(projectile.getLocation(),EntityType.FIREWORK);
                projectile.addPassenger(firework);
            }

            Player player = (Player) projectile.getShooter();
            GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
            projectile.setBounce(false);

            Bukkit.getServer().getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
                if(projectile.isDead() || e.isCancelled()) return;
                //First check if simply Y is out of bounds, cheap cost compared to checking regions
                if(projectile.getLocation().getY() >= 85 || projectile.getLocation().getY() < 40) {
                    //Fail barrier spawn
                    if(player.getGameMode() == GameMode.SURVIVAL) {
                        ItemSystem.giveItem(player, ItemSystem.getItem("barrier"), true);
                    }
                    projectile.remove();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cCan't deploy barrier out of bounds."));
                }else {
                    //Check if region of snowball has nomanland, if so deploy
                    BlockVector3 blockVector3 = BukkitAdapter.adapt(projectile.getLocation()).toVector().toBlockPoint();
                    ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getApplicableRegions(blockVector3);
                    for (ProtectedRegion region : regions) {
                        if(region.getId().equalsIgnoreCase(nomanland_region)) {
                            boolean success = MWUtil.pasteSchematic(player, GamePlayers.getTeamName(team) + "-shield", projectile.getLocation(), 0);
                            if (!success) {
                                if(player.getGameMode() == GameMode.SURVIVAL) {
                                    ItemSystem.giveItem(player, ItemSystem.getItem("barrier"), true);
                                }
                            }
                            MWUtil.updateSpawnStat("barrier",player);
                            return;
                        }
                    }
                    if(player.getGameMode() == GameMode.SURVIVAL) {
                        ItemSystem.giveItem(player, ItemSystem.getItem("barrier"), true);
                    }
                    projectile.remove();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText("§cCan't deploy barrier in a block"));
                }
            },20);
        }
    }
}
