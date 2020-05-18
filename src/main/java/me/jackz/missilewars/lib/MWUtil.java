package me.jackz.missilewars.lib;

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
import me.jackz.missilewars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MWUtil {
    private static ClipboardLoader clipboardLoader;
    static {
        clipboardLoader = new ClipboardLoader(MissileWars.getInstance());
    }
    public static void updateSpawnStat(String statname, Player player) {
        GameManager.getStats().incStat("spawns." + statname + ".total");
        GameManager.getStats().incStat("spawns." + statname + "." + player.getUniqueId());
    }
    public static void updateGenericStat(String prefix, String statname, Player player) {
        GameManager.getStats().incStat(prefix + "." + statname + ".total");
        GameManager.getStats().incStat(prefix + "." + statname + "." + player.getUniqueId());
    }
    public static boolean isPortalInLocation(Location start, boolean negZ) {
        final int z_radius = 15;
        final int y_radius = 4;
        //0 -> -15 or 0 -> 15
        //negZ: -15 to 0 | posZ: 0 -> 15
        double startBlock = negZ ? start.getZ() - z_radius : start.getZ(); //-15 or 0
        for(double z = startBlock; z <= startBlock + z_radius; z++) {
            //check below
            for(double y = start.getY() - y_radius; y < start.getY(); y++) {
                Location loc = new Location(start.getWorld(), start.getX(), y, z);
                if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(loc, Material.RED_WOOL);
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

    public static boolean pasteSchematic(Player player,String schemName,Location origin, int rotation_amount) {
        BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
        int x = origin.getBlockX();
        int y = origin.getBlockY();
        int z = origin.getBlockZ();

        Clipboard clipboard = clipboardLoader.getClipboard(schemName);
        if(clipboard == null) {
            Bukkit.getLogger().warning("Could not find schematic '" + schemName + "'");
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
