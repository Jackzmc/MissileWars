package me.jackz.missilewars.events;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.GamePlayers;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
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
        if(!e.isCancelled()) {
            Player player = e.getPlayer();
            GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
            TextComponent base = new TextComponent();
            TextComponent playerName = new TextComponent(player.getDisplayName() + ":");
            if(team != null && team != GamePlayers.MWTeam.NONE) {
                playerName.setColor(GamePlayers.getTeamColor(team));
            }else{
                if(player.getGameMode() == GameMode.SPECTATOR) {
                    base = new TextComponent("[SPECTATOR] ");
                    playerName.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                }
            }
            Chat chat = MissileWars.getInstance().getVaultChat();
            if(chat != null && chat.isEnabled()) {
                base.addExtra(ChatColor.translateAlternateColorCodes('&',chat.getPlayerPrefix(player)) + ChatColor.RESET);
            }

            int player_wins = GameManager.getStats().getSavedStat("wins." + player.getUniqueId());
            int player_loses = GameManager.getStats().getSavedStat("loses." + player.getUniqueId());
            int player_time = GameManager.getStats().getSavedStat("gametime_min." + player.getUniqueId());
            BaseComponent[] stats = new BaseComponent[]{
                    new TextComponent("§6" + player.getName() + "'s Statistics"),
                    new TextComponent("\n§9Wins: §e" + player_wins),
                    new TextComponent("\n§9Loses: §e" + player_loses),
                    new TextComponent("\n§9Total Minutes Played: §e" + player_time)
            };
            playerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, stats));
            playerName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/msg " + player.getName() + " "));
            base.addExtra(playerName);
            base.addExtra(new TextComponent(" " + ChatColor.RESET + e.getMessage()));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.spigot().sendMessage(base);
            }
            e.setCancelled(true);
        }
    }
}
