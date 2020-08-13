package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.lib.MWUtil;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class MiscPlayerEvents implements Listener {
    private final static PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION,999999,2,true,false,false);
    private final static PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION,999999,2,true,false,false);
    private final static PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION,999999,0,true,false,false);

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.addPotionEffect(nightVision);
        player.addPotionEffect(saturation);
        player.getInventory().clear();

        player.sendMessage("");
        player.sendMessage("§6Welcome to Missile Wars, §e" + player.getName());
        TextComponent base = new TextComponent();

        TextComponent btn_spectate = Util.getButtonComponent("§9[Spectate] ",false,"/spectate","§7Click to join as spectate. Type /spectate to leave or go to center.");
        TextComponent btn_green = Util.getButtonComponent("§a[Join Green Team] ",false,"/game join green","§7Click to join the green team");
        TextComponent btn_red = Util.getButtonComponent("§c[Join Red Team] ",false,"/game join red","§7Click to join the red team");

        base.addExtra(Util.addButtons(btn_spectate,btn_green,btn_red));
        player.spigot().sendMessage(base);
        player.sendMessage("");

        if(!MissileWars.gameManager.getState().isGameActive()) {
            MissileWars.gameManager.players().remove(player);
            player.setExp(0);
            player.setLevel(0);
            player.setBedSpawnLocation(GameConfig.SPAWN_LOCATION, true);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(GameConfig.SPAWN_LOCATION);
        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            player.addPotionEffect(nightVision);
            player.addPotionEffect(saturation);
            player.addPotionEffect(regen);

            if (MissileWars.gameManager.getState().isGameActive() && MissileWars.gameManager.players().has(player)) {
                GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
                if (team == GamePlayers.MWTeam.RED) {
                    player.teleport(GameConfig.RED_SPAWNPOINT);
                } else if (team == GamePlayers.MWTeam.GREEN) {
                    player.teleport(GameConfig.GREEN_SPAWNPOINT);
                }
                MWUtil.updateGenericStat("generic","deaths", player);
            }else{
                player.teleport(GameConfig.SPAWN_LOCATION);
            }
        },2);

    }
}
