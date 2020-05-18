package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.lib.MWUtil;
import me.jackz.missilewars.lib.StatsTracker;
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
            }
            return;
        }
        if(MissileWars.gameManager.getState().isGameActive() && e.getHand() == EquipmentSlot.HAND && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.CREATIVE)) {
            if(e.getItem() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if(MissileWars.gameManager.players().has(player)) {
                    StatsTracker stats = GameManager.getStats();
                    if (e.getClickedBlock() != null) {
                        switch (e.getItem().getType()) {
                            case GUARDIAN_SPAWN_EGG:
                                e.setCancelled(true);
                                spawnMissile("guardian", player, e.getClickedBlock());
                                e.setCancelled(true);
                                break;
                            case GHAST_SPAWN_EGG:
                                e.setCancelled(true);
                                spawnMissile("juggernaut", player, e.getClickedBlock());
                                break;
                            case OCELOT_SPAWN_EGG:
                                e.setCancelled(true);
                                spawnMissile("lightning", player, e.getClickedBlock());
                                break;
                            case WITCH_SPAWN_EGG:
                                e.setCancelled(true);
                                spawnMissile("shieldbuster", player, e.getClickedBlock());
                                break;
                            case CREEPER_SPAWN_EGG:
                                e.setCancelled(true);
                                spawnMissile("tomahawk", player, e.getClickedBlock());
                            case BLAZE_SPAWN_EGG:
                                e.setCancelled(true);
                            case SNOWBALL:
                                e.setCancelled(true);
                                //spawnShield(player);
                                break;
                        }

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


    private void spawnMissile(String type, Player player, Block clickedBlock) {
        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
        boolean isLightning = type.equalsIgnoreCase("lightning");
        boolean isGreenTeam = team == GamePlayers.MWTeam.GREEN;
        //lightning schematics are inverted...
        int distance_from_block = isLightning ? 5 : 4;
        int rotation_amount = isLightning  ? 180 : 0;

        int team_block_add = isGreenTeam ? -distance_from_block : distance_from_block;

        Location spawnBlock = clickedBlock.getLocation().add(0,-3,team_block_add);
        if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(spawnBlock,Material.SEA_LANTERN,20 * 5);
        if(MWUtil.isPortalInLocation(spawnBlock,isGreenTeam)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cCan't spawn a missile in the portal area!"));
            return;
        }

        String schemName = GamePlayers.getTeamName(team) + "-" + type;
        boolean success = MWUtil.pasteSchematic(player,schemName,spawnBlock,rotation_amount);
        if(success) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§0Spawned a §9" + type));
            Util.removeOneFromHand(player);
            MWUtil.updateSpawnStat(type,player);
        }else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cMissile spawn failed"));
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
