package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.*;
import me.jackz.missilewars.lib.MWUtil;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerSpawning implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(MissileWars.gameManager.getState().isLegacyMissilesEnabled()) return;
        if(e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            String[] lines = sign.getLines();
            if(lines[1].contains("Return to Lobby")) {
                player.teleport(GameConfig.SPAWN_LOCATION);
                MissileWars.gameManager.players().remove(player);
                Bukkit.broadcastMessage(player.getName() + " returned to the lobby.");
            }else if(lines[2].contains("ready")) {
                GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
                if(team == GamePlayers.MWTeam.GREEN) {
                    MissileWars.gameManager.ready(GamePlayers.MWTeam.GREEN);
                }else if(team == GamePlayers.MWTeam.RED) {
                    MissileWars.gameManager.ready(GamePlayers.MWTeam.RED);
                }else{
                    player.sendMessage("§cYou must be in a team to ready up!");
                }
                //todo: implement ready logic
            }else if(lines[1].contains("Spectate")) {
                player.setGameMode(GameMode.SPECTATOR);

            }
            return;
        }
        if(MissileWars.gameManager.getState().isGameActive() && e.getHand() == EquipmentSlot.HAND && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.CREATIVE)) {
            if(e.getItem() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if(MissileWars.gameManager.players().has(player)) {
                    if (e.getClickedBlock() != null) {
                        GameManager.getMissileLoader().processRightClick(e);
                    } else {
                        if(e.getItem().getType().equals(Material.BLAZE_SPAWN_EGG)) {
                            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                e.setCancelled(true);
                            }
                            launchFireball(player);
                            e.setCancelled(true);
                        }
                    }
                }else{
                    if(player.getGameMode() == GameMode.SURVIVAL) {
                        player.sendMessage("§cYou must be on a team to use this.");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    private void launchFireball(Player player) {
        Location eye = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.2));
        Fireball fireball = (Fireball) eye.getWorld().spawnEntity(eye, EntityType.FIREBALL);
        fireball.setVelocity(eye.getDirection().normalize().multiply(.8));
        fireball.setShooter(player);
        fireball.setVelocity(fireball.getVelocity().multiply(1));
        fireball.setGravity(true);
        if(Math.random() < .01) {
            Entity Pig = player.getWorld().spawnEntity(player.getLocation(),EntityType.PIG);
            fireball.addPassenger(Pig);
        }
        Util.removeOneFromHand(player);
        MWUtil.updateSpawnStat("fireball",player);
    }


}
