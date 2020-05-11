package me.jackz.missilewars.lib;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static boolean isInTeam(Player player) {
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if(team == null) return false;
        return team.getName().equalsIgnoreCase("Green") || team.getName().equalsIgnoreCase("Red");
    }
    public static void removeOneFromHand(Player player) {
        if(player.getGameMode() == GameMode.CREATIVE) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        int new_size = item.getAmount() - 1;
        if(new_size < 0) {
            item.setType(Material.AIR);
        }else{
            item.setAmount(new_size);
        }
    }
    public static void highlightBlock(Location location, Material material, int ticks) {
        location.setZ(location.getZ()+.5);
        FallingBlock block = location.getWorld().spawnFallingBlock(location,material.createBlockData());
        block.setGravity(false);
        block.setGlowing(true);
        block.setHurtEntities(false);
        block.setSilent(true);
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), block::remove,ticks);
    }
    public static void highlightBlock(Location loc) {
        highlightBlock(loc,Material.GLOWSTONE,20 * 5);
    }
    public static void highlightBlock(Block block) {
        highlightBlock(block.getLocation(),Material.GLOWSTONE,20 * 5);
    }
    public static void highlightBlock(Location loc, Material material) {
        highlightBlock(loc,material,20 * 5);
    }
    public static void highlightBlock(Location loc, int ticks) {
        highlightBlock(loc,Material.GLOWSTONE,ticks);
    }

    public static TextComponent getButtonComponent(String text, boolean suggestCommand, String command, String... hovertext) {
        TextComponent base = new TextComponent(text);
        ClickEvent.Action action = suggestCommand ? ClickEvent.Action.SUGGEST_COMMAND : ClickEvent.Action.RUN_COMMAND;
        base.setClickEvent(new ClickEvent(action,command));

        List<TextComponent> components = new ArrayList<>();
        for (String s : hovertext) {
            components.add(new TextComponent(s));
        }
        TextComponent[] componentArray = new TextComponent[components.size()];
        components.toArray(componentArray);
        base.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,componentArray));
        return base;
    }
    public static TextComponent addButtons(TextComponent... buttons) {
        TextComponent base = new TextComponent();
        for (TextComponent button : buttons) {
            base.addExtra(button);
            base.addExtra(" ");
        }
        return base;
    }
    public static Polygonal2DRegion getWorldEditRegionFromWorldguard(ProtectedRegion wgRegion, World bukkitWorld) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(bukkitWorld);
        return new Polygonal2DRegion(world, wgRegion.getPoints(), wgRegion.getMinimumPoint().getBlockY(), wgRegion.getMaximumPoint().getBlockY());
    }
    public static Polygonal2DRegion getWorldEditRegionFromWorldGuard(ProtectedRegion wgRegion) {
        return getWorldEditRegionFromWorldguard(wgRegion,Bukkit.getWorld("world"));
    }public static ItemStack getCustomItem(Material mt, String name) {
        return getCustomItem(mt,name,new ArrayList<>());
    }
    public static ItemStack getCustomItem(Material mt, String name, List<String> lore) {
        ItemStack item = new ItemStack(mt);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        lore = lore.stream().map(v -> ChatColor.translateAlternateColorCodes('&',v)).collect(Collectors.toList());;
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getCustomItem(Material mt, String name, String... lores) {
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, lores);
        return getCustomItem(mt, name, lore);
    }

}
