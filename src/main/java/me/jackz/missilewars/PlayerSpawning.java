package me.jackz.missilewars;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class PlayerSpawning implements Listener {
    private MissileClipboardLoader clipboardLoader;
    private MissileWars plugin;
    public PlayerSpawning(MissileWars plugin) {
        this.plugin = plugin;
        clipboardLoader = new MissileClipboardLoader(plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(e.getHand() == EquipmentSlot.HAND && player.getGameMode() == GameMode.SURVIVAL) {
            if(e.getItem() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                Team team = player.getScoreboard().getEntryTeam(player.getName());
                String teamName = team != null ? team.getName() : "";
                if(teamName.equalsIgnoreCase("Green") || teamName.equalsIgnoreCase("Red")) {

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
                                spawnMissile("tomohawk", player, e.getClickedBlock());
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

                            ItemStack item = player.getInventory().getItemInMainHand();
                            int new_size = item.getAmount() - 1;
                            if(new_size < 0) {
                                item.setType(Material.AIR);
                            }else{
                                item.setAmount(new_size);
                            }
                        }
                    }
                }else{
                    player.sendMessage("§cYou must be on a team to use this.");
                    e.setCancelled(true);
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
                boolean success = paste(player,team.getName() + "-shield",projectile.getLocation(),0);
                if(success) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    int new_size = item.getAmount() - 1;
                    if (new_size < 0) {
                        item.setType(Material.AIR);
                    } else {
                        item.setAmount(new_size);
                    }
                }
                projectile.getLocation().getBlock().setType(Material.DIRT);
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
        int center_block_add = isGreenTeam ? -5 : 5;

       //if(type.equalsIgnoreCase("lightning"))
        Location spawnBlock = clickedBlock.getLocation().add(0,0,team_block_add);
        Location centerBlock = spawnBlock.add(0,0,center_block_add);
        if(hasPortal(centerBlock,5)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cCan't spawn a missile in the portal area!"));
            return;
        }
        int x = spawnBlock.getBlockX();
        int y = spawnBlock.getBlockY();
        int z = spawnBlock.getBlockZ();

        String schemName = team.getName() + "-" + type;
        boolean success = paste(player,schemName,spawnBlock,rotation_amount);
        if(success) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§0Spawned a §9" + type));
        }else {
            ItemStack item = player.getInventory().getItemInMainHand();
            int new_size = item.getAmount() - 1;
            if (new_size < 0) {
                item.setType(Material.AIR);
            } else {
                item.setAmount(new_size);
            }
        }
    }
    private boolean hasPortal(Location start, int radius){
        for(double x = start.getX() - radius; x <= start.getX() + radius; x++){
            for(double y = start.getY() - radius; y <= start.getY() + radius; y++){
                for(double z = start.getZ() - radius; z <= start.getZ() + radius; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    Material material = loc.getBlock().getType();
                    if(material == Material.NETHER_PORTAL || material == Material.OBSIDIAN) {
                        return true;
                    }
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
