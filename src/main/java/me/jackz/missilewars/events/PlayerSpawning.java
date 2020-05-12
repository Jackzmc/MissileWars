package me.jackz.missilewars.events;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.ItemSystem;
import me.jackz.missilewars.lib.ClipboardLoader;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Team;

public class PlayerSpawning implements Listener {
    private ClipboardLoader clipboardLoader;
    private MissileWars plugin;

    public PlayerSpawning(MissileWars plugin) {
        this.plugin = plugin;
        clipboardLoader = new ClipboardLoader(plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(MissileWars.gameManager.getState().isLegacyMissilesEnabled()) return;
        if(e.getHand() == EquipmentSlot.HAND && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.CREATIVE)) {
            if(e.getItem() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if(Util.isInTeam(player)) {
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
                            case BLAZE_ROD:
                                if(MissileWars.gameManager.getState().isDebug()) {
                                    boolean red = isPortalInLocation(e.getClickedBlock().getLocation(),true);
                                    boolean green = isPortalInLocation(e.getClickedBlock().getLocation(),false);
                                    String formatted = String.format("§c%b | §a%b",red,green);
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.valueOf(formatted)));
                                    break;
                                }
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

                            Entity fireball = player.launchProjectile(Fireball.class);
                            fireball.setVelocity(fireball.getVelocity().multiply(1));
                            fireball.setGravity(true);
                            if(Math.random() < .1) {
                                Entity Pig = player.getWorld().spawnEntity(player.getLocation(),EntityType.PIG);
                                fireball.addPassenger(Pig);
                            }

                            e.setCancelled(true);
                            Util.removeOneFromHand(player);
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
    @EventHandler
    private void onSnowballThrow(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if(projectile.getType() == EntityType.SNOWBALL) {
            Player player = (Player) projectile.getShooter();
            Team team = player.getScoreboard().getEntryTeam(player.getName());
            projectile.setBounce(false);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if(projectile.getLocation().getY() >= 90 || projectile.getLocation().getY() <= 35) {
                    //Fail barrier spawn
                    if(player.getGameMode() == GameMode.SURVIVAL) {
                        ItemSystem.giveItem(player, ItemSystem.getItem("barrier"), 1);
                    }
                    projectile.remove();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText("§cCan't deploy barrier out of bounds."));
                }else {
                    boolean success = paste(player, team.getName() + "-shield", projectile.getLocation(), 0);
                    if (success) {
                        Util.removeOneFromHand(player);
                    }
                }
            },20);
        }
    }

    private void spawnMissile(String type, Player player, Block clickedBlock) {
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        boolean isLightning = type.equalsIgnoreCase("lightning");
        boolean isGreenTeam = team.getName().equalsIgnoreCase("Green");
        //lightning schematics are inverted...
        int distance_from_block = isLightning ? 5 : 4;
        int rotation_amount = isLightning  ? 180 : 0;

        int team_block_add = isGreenTeam ? -distance_from_block : distance_from_block;

        Location spawnBlock = clickedBlock.getLocation().add(0,-2,team_block_add);
        if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(spawnBlock,Material.SEA_LANTERN,20 * 5);
        if(isPortalInLocation(spawnBlock,isGreenTeam)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cCan't spawn a missile in the portal area!"));
            return;
        }

        String schemName = team.getName() + "-" + type;
        boolean success = paste(player,schemName,spawnBlock,rotation_amount);
        if(success) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§0Spawned a §9" + type));
            Util.removeOneFromHand(player);
        }else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cMissile spawn failed"));
        }
    }
    private boolean isPortalInLocation(Location start, boolean negZ) {
        final int z_radius = 15;
        final int y_radius = 4;
        //0 -> -15 or 0 -> 15
        //negZ: -15 to 0 | posZ: 0 -> 15
        double startBlock = negZ ? start.getZ() - z_radius : start.getZ(); //-15 or 0
        for(double z = startBlock; z <= startBlock + z_radius; z++) {
            //check below
            for(double y = start.getY() - y_radius; y < start.getY(); y++) {
                Location loc = new Location(start.getWorld(), start.getX(), y, z);
                if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(loc,Material.RED_WOOL);
                Material material = loc.getBlock().getType();
                if(material == Material.NETHER_PORTAL || material == Material.OBSIDIAN) {
                    Bukkit.getLogger().info("locaiton:" + loc.getX() + "," + loc.getY() + "," + loc.getZ());
                    if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(loc.getBlock());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean paste(Player player,String schemName,Location origin, int rotation_amount) {
        BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
        int x = origin.getBlockX();
        int y = origin.getBlockY();
        int z = origin.getBlockZ();

        Clipboard clipboard = clipboardLoader.getClipboard(schemName);
        if(clipboard == null) {
            plugin.getLogger().warning("Could not find schematic '" + schemName + "'");
            player.sendMessage("§cSorry, there is no schematic for this item to place.");
            return false;
        }else{
            AffineTransform transform = new AffineTransform();
            transform = transform.rotateY(rotation_amount);
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(wePlayer.getWorld(), 200)) {
                //editSession.setFastMode(true);
                ClipboardHolder holder = new ClipboardHolder(clipboard);
                holder.setTransform(holder.getTransform().combine(transform));
                Operation operation = holder
                        .createPaste(editSession)
                        .copyEntities(false)
                        .to(BlockVector3.at(x,y,z))
                        .ignoreAirBlocks(true)
                        // configure here
                        .build();
                Operations.complete(operation);
                return true;
            } catch (WorldEditException ex) {
                ex.printStackTrace();
                player.sendMessage("§cException while placing: " + ex.getMessage());
                return false;
            }
        }
    }
}
