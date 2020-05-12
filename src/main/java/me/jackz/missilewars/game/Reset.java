package me.jackz.missilewars.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.jackz.missilewars.lib.ClipboardLoader;
import org.bukkit.Bukkit;

class Reset {
    private final static String greenCube = "greenCube";
    private final static String redCube = "redCube";
    private final static String greenPortal = "greenPortal";
    private final static String redPortal = "redPortal";

    private final static BlockVector3 greenCubeOrigin = BlockVector3.at(-52.5,78.0,66.5);
    private final static BlockVector3 redCubeOrigin = BlockVector3.at(-52.5,78.0,-65.5);
    private final static BlockVector3 greenPortalOrigin = BlockVector3.at(-5, 73.0, 72);
    private final static BlockVector3 redPortalOrigin = BlockVector3.at(-5, 73.0, -72);

    private final static BlockVector3 NoManLandPos1 = BlockVector3.at(-73, 90, -50);
    private final static BlockVector3 NoManLandPos2 = BlockVector3.at(23, 35, 50);

    static void reset() {
        Clipboard greenCube = ClipboardLoader.getClipboard("greenCube");
        Clipboard redCube = ClipboardLoader.getClipboard("redCube");
        Bukkit.broadcastMessage("§6[Missile Wars] §eResetting the map, please wait...");

        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));

        paste(world, greenCube, greenCubeOrigin, true);
        paste(world, redCube, redCubeOrigin, true);

        paste(world, ClipboardLoader.getClipboard("portal"), greenPortalOrigin,true);
        paste(world, ClipboardLoader.getClipboard("portal"), redPortalOrigin,true);
        

        CuboidRegion region = new CuboidRegion(NoManLandPos1,NoManLandPos2);
        BlockState airBlock = BlockTypes.AIR.getDefaultState();
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try(EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            editSession.setBlocks(region, airBlock);
            editSession.setFastMode(true);
            //editSession.flushSession();
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, BlockVector3.ZERO);
            Operations.completeBlindly(forwardExtentCopy);
            //probably put a delay, cant reliabily wait
            Bukkit.broadcastMessage("§6[Missile Wars] §aMap has been reset successfully");
        } catch (MaxChangedBlocksException e) {
            Bukkit.getLogger().warning("Hit a max blocks limit resetting map, may not be cleared");
        }
    }

    private static boolean paste(World world, Clipboard clipboard, BlockVector3 location, boolean fastMode) {
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            editSession.setFastMode(fastMode);
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operation operation = holder
                    .createPaste(editSession)
                    .copyEntities(false)
                    .to(location)
                    .ignoreAirBlocks(false)
                    // configure here
                    .build();
            Operations.complete(operation);
            return true;
        } catch (WorldEditException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}