package me.jackz.missilewars.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            Team team = p.getScoreboard().getEntryTeam(p.getName());
            boolean is_in_game = team != null && (team.getName().equalsIgnoreCase("green") || team.getName().equalsIgnoreCase("red"));
            boolean is_spectating = p.getGameMode() == GameMode.SPECTATOR;
            if(is_in_game) {
                p.sendMessage("§cYou are already in the game!");
            }else{
                if(is_spectating) {
                    p.setGameMode(GameMode.ADVENTURE);
                    p.teleport(p.getWorld().getSpawnLocation());
                }else{
                    p.setGameMode(GameMode.SPECTATOR);
                }
            }
        }else{
            sender.sendMessage("You must be a player to run this command.");
        }
        return true;
    }
}
