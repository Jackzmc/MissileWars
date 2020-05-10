package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class Util {
    public static boolean IsInTeam(Player player) {
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if(team == null) return false;
        return team.getName().equalsIgnoreCase("Green") || team.getName().equalsIgnoreCase("Red");
    }
    public static void RemoveOneFromHand(Player player) {
        if(player.getGameMode() == GameMode.CREATIVE) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        int new_size = item.getAmount() - 1;
        if(new_size < 0) {
            item.setType(Material.AIR);
        }else{
            item.setAmount(new_size);
        }
    }
    public static void HighlightBlock(Location location, Material material, int ticks) {
        location.setZ(location.getZ()+.5);
        FallingBlock block = location.getWorld().spawnFallingBlock(location,material.createBlockData());
        block.setGravity(false);
        block.setGlowing(true);
        block.setHurtEntities(false);
        block.setSilent(true);
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), block::remove,ticks);
    }
    public static void HighlightBlock(Location loc) {
        HighlightBlock(loc,Material.GLOWSTONE,20 * 5);
    }
    public static void HighlightBlock(Block block) {
        HighlightBlock(block.getLocation(),Material.GLOWSTONE,20 * 5);
    }
    public static void HighlightBlock(Location loc, Material material) {
        HighlightBlock(loc,material,20 * 5);
    }
    public static void HighlightBlock(Location loc, int ticks) {
        HighlightBlock(loc,Material.GLOWSTONE,ticks);
    }
}
