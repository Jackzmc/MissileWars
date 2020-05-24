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
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.ClipboardLoader;
import me.jackz.missilewars.lib.DataLoader;
import me.jackz.missilewars.lib.MWUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class Reset {
    private static String greenCubeSchematic = "green-cube3";
    private static String redCubeSchematic = "red-cube3";
    //private final static String greenPortal = "greenPortal";
    //private final static String redPortal = "redPortal";

    private static BlockVector3 greenCubeOrigin = BlockVector3.at(-52.5,78.0,66.5);
    private static BlockVector3 redCubeOrigin = BlockVector3.at(-52.5,78.0,-65.5);
    //private final static BlockVector3 greenPortalOrigin = BlockVector3.at(-5, 73.0, 72);
    //private final static BlockVector3 redPortalOrigin = BlockVector3.at(-5, 73.0, -72);

    private static BlockVector3 NoManLandPos1 = BlockVector3.at(-73, 90, -85);
    private static BlockVector3 NoManLandPos2 = BlockVector3.at(23, 35, 85);

    private static boolean resetting = false;


    public static void reset() {
        if(isResetting()) {
            Bukkit.getLogger().warning("Already resetting, ignoring second reset request");
            return;
        }
        resetting = true;
        Clipboard greenCube = ClipboardLoader.getClipboard(Reset.greenCubeSchematic);
        Clipboard redCube = ClipboardLoader.getClipboard(Reset.redCubeSchematic);
        YamlConfiguration data = DataLoader.getData();
        Bukkit.broadcastMessage("§6[Missile Wars] §eResetting the map, please wait...");

        World world = BukkitAdapter.adapt(GameManager.getWorld());
        CuboidRegion region = new CuboidRegion(NoManLandPos1,NoManLandPos2);
        BlockState airBlock = BlockTypes.AIR.getDefaultState();
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clearNML(world, region, airBlock, clipboard);


        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            paste(world, greenCube, greenCubeOrigin, true);
        }, 20 * 15);
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            paste(world, redCube, redCubeOrigin, true);
            Bukkit.broadcastMessage("§6[Missile Wars] §aMap has been reset successfully");
            resetting = false;
        }, 20 * 25);

    }

    public static void reloadVariables() {
        YamlConfiguration data = DataLoader.getData();
        greenCubeSchematic = "green-" + data.getString("regions.green-cube.schematic","cube3");
        greenCubeOrigin = MWUtil.getVector("regions.green-cube");
        redCubeSchematic = "red-" + data.getString("regions.red-cube.schematic","cube3");
        redCubeOrigin = MWUtil.getVector("regions.red-cube");

        NoManLandPos1 = MWUtil.getVector("regions.nomanland");
        NoManLandPos2 = MWUtil.getVector("regions.pos2");

    }


    private static boolean clearNML(World world, CuboidRegion region, BlockState airBlock, BlockArrayClipboard clipboard) {
        try(EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            editSession.setBlocks(region, airBlock);
            editSession.setFastMode(true);
            //editSession.flushSession();
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, BlockVector3.ZERO);
            Operations.completeBlindly(forwardExtentCopy);
            //probably put a delay, cant reliabily wait
            return true;
        } catch (MaxChangedBlocksException e) {
            Bukkit.getLogger().warning("Hit a max blocks limit resetting map, may not be cleared");
            return false;
        }
    }

    private static boolean paste(World world, Clipboard clipboard, BlockVector3 location, boolean fastMode) {
        if(clipboard == null) return false;
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

    public static boolean isResetting() {
        return resetting;
    }
}
