package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemSystem {
    private final static List<String> ITEM_TYPES = new ArrayList<>(Arrays.asList("fireball","arrows","barrier","tomahawk","shieldbuster","juggernaut","lightning","guardian"));
    private int timerID;

    private final static ItemStack ITEM_FIREBALL = Util.getCustomItem(Material.BLAZE_SPAWN_EGG,"&9Launch Fireball","&7Spawns a Fireball.","&7Rightclick to shoot fireball in direction you are facing");
    private final static ItemStack ITEM_BOW = Util.getCustomItem(Material.BOW,"&9GunBlade","","&7A sharp Flame Bow","&7Use to ignite TNT remotely with arrows");
    private final static ItemStack ITEM_BARRIER = Util.getCustomItem(Material.SNOWBALL,"&9Deploy Barrier","&7Deploys a barrier after 1 second.");
    private final static ItemStack ITEM_ARROW = Util.getCustomItem(Material.ARROW,"&9Flame Arrow","&7Shoot to ignite TNT.");
    private final static ItemStack MISSILE_TOMAHAWK = Util.getCustomItem(Material.CREEPER_SPAWN_EGG,"&9Deploy Tomahawk","&7Spawns a tomahawk missile");
    private final static ItemStack MISSILE_SHIELDBUSTER = Util.getCustomItem(Material.WITCH_SPAWN_EGG,"&9Deploy ShieldBuster","&7Spawns a Shieldbuster missile.","","&eWill go straight through barriers");
    private final static ItemStack MISSILE_JUGGERNAUT = Util.getCustomItem(Material.GHAST_SPAWN_EGG,"&9Deploy Juggernaut","&7Spawns a Juggernaut missile.","","&eIs the most explosive missile");
    private final static ItemStack MISSILE_LIGHTNING = Util.getCustomItem(Material.OCELOT_SPAWN_EGG,"&9Deploy Lightning","&7Spawns a Lightning missile. ","","&eIs the fastest missile");
    private final static ItemStack MISSILE_GUARDIAN = Util.getCustomItem(Material.GUARDIAN_SPAWN_EGG,"&9Deploy Guardian","&7Spawns a Guardian missile.");

    static {
        ITEM_BOW.addEnchantment(Enchantment.ARROW_FIRE,1);
        ITEM_BOW.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
        ItemMeta meta = ITEM_BOW.getItemMeta();
        meta.setUnbreakable(true);
        ITEM_BOW.setItemMeta(meta);
    }

    public void start() {
        timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MissileWars.getInstance(),
                ItemSystem::chooseItem,
                20 * 5,
                20 * MissileWars.gameManager.getConfig().getItemInterval()
        );
    }
    public void stop() {
        Bukkit.getScheduler().cancelTask(timerID);
    }

    public static void chooseItem() {
        if(!MissileWars.gameManager.getConfig().prioritizeDefenseEnabled()) {
            Random rand = new Random();
            int randMode = MissileWars.gameManager.getConfig().getRandomizeMode();
            if(randMode == 0) {
                String itemName = ITEM_TYPES.get(rand.nextInt(ITEM_TYPES.size()));
                ItemStack item = getItem(itemName);
                for (Player player : MissileWars.gameManager.players().getAllPlayers()) {
                    giveItem(player, item, false);
                }
            }else if(randMode == 1) {
                String red_item_str = ITEM_TYPES.get(rand.nextInt(ITEM_TYPES.size()));
                String green_item_str = ITEM_TYPES.get(rand.nextInt(ITEM_TYPES.size()));
                ItemStack red_item = getItem(red_item_str);
                ItemStack green_item = getItem(green_item_str);

                for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED)) {
                    giveItem(player, red_item, false);
                }
                for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN)) {
                    giveItem(player, green_item, false);
                }
            }else {
                for (Player player : MissileWars.gameManager.players().getAllPlayers()) {
                    String itemName = ITEM_TYPES.get(rand.nextInt(ITEM_TYPES.size()));
                    ItemStack item = getItem(itemName);
                    giveItem(player, item, false);
                }
            }

        }
    }

    public static void giveItem(Player player, ItemStack itemstack, boolean bypassLimit) {
        //maxAmount being -1 disables limit
        int item_count = Util.getAmount(player.getInventory(), itemstack);
        if(!bypassLimit &&item_count <= MissileWars.gameManager.getConfig().getMaxItems()) {
            player.sendMessage("§cYou already have a " + itemstack.getItemMeta().getDisplayName());
        }else{
            player.getInventory().addItem(itemstack);
        }
    }
    public static void giveItem(Player player, String item, boolean bypassLimit) {
        //maxAmount being -1 disables limit
        ItemStack itemstack = getItem(item);
        if(itemstack != null) {
            giveItem(player, itemstack, bypassLimit);
        }
    }

    public static ItemStack getItem(String name) {
        switch(name.toLowerCase()) {
            case "fireball":
                return ITEM_FIREBALL;
            case "arrow":
                return ITEM_ARROW;
            case "arrows":
                ItemStack arrows = ITEM_ARROW.clone();
                arrows.setAmount(4);
                return arrows;
            case "shield":
            case "barrier":
                return ITEM_BARRIER;
            case "tomahawk":
                return MISSILE_TOMAHAWK;
            case "shieldbuster":
                return MISSILE_SHIELDBUSTER;
            case "juggernaut":
                return MISSILE_JUGGERNAUT;
            case "lightning":
                return MISSILE_LIGHTNING;
            case "guardian":
                return MISSILE_GUARDIAN;
            case "bow":
                return ITEM_BOW;
            default:
                 return null;
        }
    }
}
