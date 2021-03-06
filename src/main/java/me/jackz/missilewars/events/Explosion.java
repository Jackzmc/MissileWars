package me.jackz.missilewars.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.WinManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Explosion implements Listener {

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        if(MissileWars.gameManager.getState().isGameActive()) {
            World world = BukkitAdapter.adapt(e.getLocation().getWorld());
            for (Block block : e.blockList()) {
                if (block.getType().equals(Material.NETHER_PORTAL)) {
                    BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(block.getLocation());
                    ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(blockVector3);
                    WinManager.testWin(regions);
                    break;
                }
            }
        }
    }
}
