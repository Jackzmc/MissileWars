package me.jackz.missilewars.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.lib.Util;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class MiscPlayerEvents implements Listener {
    private final static PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION,99999,2,true,false,false);
    private final static PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION,99999,2,true,false,false);
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

        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
        if(team != GamePlayers.MWTeam.NONE && !MissileWars.gameManager.getState().isGameActive()) {
            MissileWars.gameManager.players().remove(player,team);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            int new_health = (int) (player.getHealth() - e.getFinalDamage());
            if(new_health <= 0) {
                LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                if (WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgPlayer.getWorld()).hasRegion("spawnlobby")) {
                    e.setDamage(0);
                }
            }

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            player.addPotionEffect(nightVision);
            player.addPotionEffect(saturation);
        },5);
    }
}
