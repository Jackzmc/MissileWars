package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.lib.ClipboardLoader;
import me.jackz.missilewars.lib.MWUtil;
import me.jackz.missilewars.lib.Missile;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MissileLoader {
    //Will manage the files, and the data.yml file and managing spawning
    private static File file;
    private List<Missile> missileList = new ArrayList<>();

    public MissileLoader() {
        file = new File(MissileWars.getInstance().getDataFolder(),"data.yml");
        if(file.exists()) {
            reload();
        }else{
            MissileWars.getInstance().saveResource("data.yml",false);
            Bukkit.getLogger().warning("Could not find data.yml, creating default.");
        }
    }

    public List<Missile> getMissiles() {
        return missileList;
    }
    public List<String> getIds() {
        return missileList.stream().map(Missile::getId).collect(Collectors.toList());
    }

    public Missile findMissile(String id) {
        for (Missile missile : missileList) {
            if(missile.getId().equalsIgnoreCase(id)) {
                return missile;
            }
        }
        return null;
    }

    public void reload() {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection missiles = data.getConfigurationSection("missiles");
        if(missiles != null) {
            for (String key : missiles.getKeys(false)) {
                ConfigurationSection section = missiles.getConfigurationSection(key);
                String materialName = section.getString("item");
                if(materialName != null) {
                    Material material = Material.getMaterial(materialName);
                    if(material != null) {
                        Missile missile = new Missile(key, material);
                        missile.setHint(section.getString("hint"));
                        missile.setDisplay(section.getString("display"));
                        missile.setSchematic(section.getString("schematic"));
                        missileList.add(missile);
                    }else{
                        Bukkit.getLogger().warning("Missile id " + key + " has invalid material '" + materialName + "'");
                    }
                }
            }
            ClipboardLoader.loadList(missileList);
        }else{
            Bukkit.getLogger().severe("data.yml is missing 'missiles' section: Missile spawning disabled");
        }
    }

    public void processRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(e.getItem() == null || e.getClickedBlock() == null) return;
        for (Missile missile : missileList) {
            if(missile.getMaterial().equals(e.getItem().getType())) {
                e.setCancelled(true);
                spawnMissile(missile, player, e.getClickedBlock().getLocation());
            }
        }
    }
    private void spawnMissile(Missile missile, Player player, Location startPos) {
        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
        boolean isLightning = missile.getId().equalsIgnoreCase("lightning");
        boolean isGreenTeam = team == GamePlayers.MWTeam.GREEN;
        //lightning schematics are inverted...
        int distance_from_block = isLightning ? 5 : 4;
        int rotation_amount = isLightning  ? 180 : 0;

        int team_block_add = isGreenTeam ? -distance_from_block : distance_from_block;

        Location spawnBlock = startPos.add(0,-3,team_block_add);
        if(MissileWars.gameManager.getState().isDebug()) Util.highlightBlock(spawnBlock, Material.SEA_LANTERN,20 * 5);
        if(MWUtil.isPortalInLocation(spawnBlock,isGreenTeam)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cCan't spawn a missile in the portal area!"));
            return;
        }

        String schemName = missile.getFullSchematic(team);
        boolean success = MWUtil.pasteSchematic(player,schemName,spawnBlock,rotation_amount);
        if(success) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§0Spawned a §9" + missile.getId()));
            Util.removeOneFromHand(player);
            MWUtil.updateSpawnStat(missile.getId(),player);
        }else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cMissile spawn failed"));
        }
    }
}
