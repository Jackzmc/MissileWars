package me.jackz.missilewars.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MiscPlayerEvents implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,999,1,true,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,999,2,true,false,false));
        player.getInventory().clear();
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
}
