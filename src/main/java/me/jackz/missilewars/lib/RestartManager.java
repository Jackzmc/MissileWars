package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;

public class RestartManager {
    private boolean PENDING_RESTART = false;
    private final Long RESTART_DELAY_SECONDS = 172800L;


    public RestartManager() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MissileWars.getInstance(), () -> {
            if(Bukkit.getServer().getOnlinePlayers().size() > 0) {
                PENDING_RESTART = true;
                Bukkit.getServer().broadcastMessage("§2[Missilewars] §eNotice: Server will being restarting when all players leave.");
            }else{
                Bukkit.getServer().broadcastMessage("§2[MissileWars] §cServer is now auto restarting.");
                Bukkit.getServer().shutdown();
            }
        },20 * RESTART_DELAY_SECONDS);
    }

    public boolean isPendingRestart() {
        return PENDING_RESTART;
    }
}
