package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemSystem {
    private final static List<String> ITEM_TYPES = new ArrayList<>(Arrays.asList("fireball","arrows","barrier","tomahawk","shieldbuster","juggernaut","lightning","guardian"));

    ItemSystem() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MissileWars.getInstance(),
                ItemSystem::chooseItem,
                0,
                20 * MissileWars.gameManager.getConfig().getItemInterval()
        );
    }
    public static void chooseItem() {
        if(!MissileWars.gameManager.getConfig().prioritizeDefenseEnabled()) {
            Random rand = new Random();
            String itemName = ITEM_TYPES.get(rand.nextInt(ITEM_TYPES.size()));
            ItemStack item = getItem(itemName);

            int max_of_item = MissileWars.gameManager.getConfig().getMaxItems();

            for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN)) {
                giveItem(player, item, max_of_item);
            }
            for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED)) {
                giveItem(player, item, max_of_item);
            }
        }
    }

    public static void giveItem(Player player, ItemStack itemstack, int maxAmount) {
        //maxAmount being -1 disables limit
        if(maxAmount >= 0 && player.getInventory().contains(itemstack, maxAmount)) {
            player.sendMessage("§cYou already have a " + itemstack.getItemMeta().getDisplayName());
        }else{
            player.getInventory().addItem(itemstack);
        }
    }

    public static ItemStack getItem(String name) {
        switch(name.toLowerCase()) {
            case "fireball":
                return Util.getCustomItem(Material.BLAZE_SPAWN_EGG,"&9Launch Fireball","&7Spawns a Fireball.","&7Rightclick to shoot fireball in direction you are facing");
            case "arrows":
                ItemStack arrows = Util.getCustomItem(Material.ARROW,"&9Flame Arrow","&7Shoot to ignite TNT.");
                arrows.setAmount(3);
                return arrows;
            case "barrier":
                return Util.getCustomItem(Material.SNOWBALL,"&9Deploy Barrier","&7Deploys a barrier after 1 second.");
            case "tomahawk":
                return Util.getCustomItem(Material.CREEPER_SPAWN_EGG,"&9Deploy Tomahawk","&7Spawns a tomahawk missile");
            case "shieldbuster":
                return Util.getCustomItem(Material.WITCH_SPAWN_EGG,"&9Deploy ShieldBuster","&7Spawns a Shieldbuster missile.","","&eWill go straight through barriers");
            case "juggernaut":
                return Util.getCustomItem(Material.GHAST_SPAWN_EGG,"&9Deploy Juggernaut","&7Spawns a Juggernaut missile.","","&eIs the most explosive missile");
            case "lightning":
                return Util.getCustomItem(Material.OCELOT_SPAWN_EGG,"&9Deploy Lightning","&7Spawns a Lightning missile. ","","&eIs the fastest missile");
            case "guardian":
                return Util.getCustomItem(Material.GUARDIAN_SPAWN_EGG,"&9Deploy Guardian","&7Spawns a Guardian missile.");
            default:
                 return null;
        }
    }
}
