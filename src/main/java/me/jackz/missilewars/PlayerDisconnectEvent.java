package me.jackz.missilewars;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectEvent implements Listener {
    private MissileWars plugin;

    public PlayerDisconnectEvent(MissileWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if(plugin.isPendingRestart() && plugin.getServer().getOnlinePlayers().isEmpty()) {
            plugin.getServer().broadcastMessage("§2[Missilewars] §7Last player has left, will restart if no one joins in 30 seconds.");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if(plugin.getServer().getOnlinePlayers().isEmpty()) {
                    plugin.getServer().broadcastMessage("§2[MissileWars] §cNo players have rejoined, restarting server.");
                    plugin.getServer().shutdown();
                }
            },20 * 30L);
        }
    }
}
