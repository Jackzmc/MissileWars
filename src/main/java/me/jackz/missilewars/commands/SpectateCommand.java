package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectateCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length >= 1) {
            if(sender.isOp() || sender.hasPermission("missilewars.spectate.others")) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage("§cCould not find any players online with that name.");
                    return true;
                }
            }else{
                sender.sendMessage("§cYou do not have permission");
                return true;
            }
        }else{
            if(sender instanceof Player) {
                player = (Player) sender;
            }else{
                sender.sendMessage("Please specify a player to toggle spectating on");
                return true;
            }
        }
        boolean is_in_game = MissileWars.gameManager.players().has(player);
        if(is_in_game) {
            if(player == sender || sender.getName().equals(player.getName())) {
                sender.sendMessage("§cYou can't spectate when you are in a game.");
            }else{
                sender.sendMessage("§cThat player is currently in a game.");
            }
        }else {
            if (sender.getName().equals(player.getName())) {
                if(!player.hasPermission("missilewars.spectate")) {
                    sender.sendMessage("§cYou do not have permission");
                    return true;
                }
            }
            if(player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(GameConfig.SPAWN_LOCATION);
            }else{
                player.setGameMode(GameMode.SPECTATOR);
                if(sender.getName().equals(player.getName())) player.sendMessage("§eYou are now spectating, leave spectator mode by typing §9/spectate §eagain.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(sender.hasPermission("missilewars.spectate.others")) {
            return null;
        }else {
            return new ArrayList<>();
        }
    }
}
