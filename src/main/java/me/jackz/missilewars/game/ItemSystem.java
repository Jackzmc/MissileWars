package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.Missile;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemSystem {
    private final static List<String> ITEM_TYPES = new ArrayList<>(Arrays.asList("fireball","arrows","barrier"));
    private BukkitTask timerTask;
    private int currentCount = 0;

    private final static ItemStack ITEM_FIREBALL = Util.getCustomItem(Material.BLAZE_SPAWN_EGG,"&9Launch Fireball","&7Spawns a Fireball.","&7Rightclick to shoot fireball in direction you are facing");
    private final static ItemStack ITEM_BOW = Util.getCustomItem(Material.BOW,"&9GunBlade","","&7A sharp Flame Bow","&7Use to ignite TNT remotely with arrows");
    private final static ItemStack ITEM_BARRIER = Util.getCustomItem(Material.SNOWBALL,"&9Deploy Barrier","&7Deploys a barrier after 1 second.");
    private final static ItemStack ITEM_ARROW = Util.getCustomItem(Material.ARROW,"&9Flame Arrow","&7Shoot to ignite TNT.");

    static {
        ITEM_BOW.addEnchantment(Enchantment.ARROW_FIRE,1);
        ITEM_BOW.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
        ItemMeta meta = ITEM_BOW.getItemMeta();
        meta.setUnbreakable(true);
        ITEM_BOW.setItemMeta(meta);
    }

    public void start() {
        currentCount = MissileWars.gameManager.getConfig().getItemInterval();
        timerTask = Bukkit.getScheduler().runTaskTimer(MissileWars.getInstance(),
                this::processTimer,
                0,
                20
        );
    }
    public void stop() {
        if(timerTask != null) timerTask.cancel();
        currentCount = 0;
    }

    public static List<String> getTypes() {
        List<String> list = ITEM_TYPES;
        ITEM_TYPES.addAll(GameManager.getMissileLoader().getIds());
        return list;
    }

    public void processTimer() {
        currentCount++;
        final int INTERVAL = MissileWars.gameManager.getConfig().getItemInterval();
        for (Player player : MissileWars.gameManager.players().getAllPlayers()) {
            player.setExp(currentCount == INTERVAL ? 1.0f : 0.0f);
            player.setLevel(INTERVAL - currentCount);
        }
        if(currentCount >= INTERVAL) {
            ItemSystem.chooseItem();
            currentCount = 0;
        }
    }

    public static void chooseItem() {
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

    public static void giveItem(Player player, ItemStack itemstack, boolean bypassLimit) {
        //maxAmount being -1 disables limit
        int item_count = Util.getAmount(player.getInventory(), itemstack);
        if(MissileWars.gameManager.getConfig().getMaxItems() == -1) bypassLimit = true;
        if(!bypassLimit && item_count >= MissileWars.gameManager.getConfig().getMaxItems()) {
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
            case "bow":
                return ITEM_BOW;
            default:
                Missile missile = GameManager.getMissileLoader().findMissile(name.toLowerCase());
                if(missile != null) {
                    List<String> lore = new ArrayList<>();
                    String hint = missile.getHint();

                    lore.add("&7Spawns a " + missile.getId() + " missile");
                    if(hint != null) {
                        lore.add("");
                        lore.add("&e" + hint);
                    }
                    return Util.getCustomItem(missile.getMaterial(),"&9Deploy " + missile.getDisplay(),lore);
                }
                return null;
        }
    }
}
