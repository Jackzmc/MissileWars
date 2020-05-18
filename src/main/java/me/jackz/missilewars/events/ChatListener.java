package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
        ChatColor message_format_color = ChatColor.WHITE;
        String message_format_prefix = "";
        if(team != null) {
            if (team == GamePlayers.MWTeam.RED) {
                message_format_color = ChatColor.RED;
            } else if (team == GamePlayers.MWTeam.GREEN) {
                message_format_color = ChatColor.GREEN;
            }
        }else{
            if (player.getGameMode() == GameMode.SPECTATOR) {
                message_format_color = ChatColor.AQUA;
                message_format_prefix = "[SPECTATOR] ";
            }
        }
        if(!e.isCancelled()) {
            String format = String.format("%s%s%s: %s%s",message_format_prefix,message_format_color,player.getDisplayName(),ChatColor.RESET,e.getMessage());
            e.setFormat(format);
        }
    }
}
