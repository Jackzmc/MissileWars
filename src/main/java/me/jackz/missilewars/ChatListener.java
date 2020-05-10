package me.jackz.missilewars;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;
import java.util.Set;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Team team = p.getScoreboard().getEntryTeam(p.getName());
        ChatColor message_format_color = ChatColor.WHITE;
        String message_format_prefix = "";
        if(team != null) {
            if (team.getName().equalsIgnoreCase("red")) {
                message_format_color = ChatColor.RED;
            } else if (team.getName().equalsIgnoreCase("green")) {
                message_format_color = ChatColor.GREEN;
            }
        }else{
            if (p.getGameMode() == GameMode.SPECTATOR) {
                message_format_color = ChatColor.AQUA;
                message_format_prefix = "[SPECTATOR] ";
            }
        }
        if(!e.isCancelled()) {
            String format = String.format("%s%s%s: %s%s",message_format_prefix,message_format_color,p.getDisplayName(),ChatColor.RESET,e.getMessage());
            e.setFormat(format);
        }
    }
}
