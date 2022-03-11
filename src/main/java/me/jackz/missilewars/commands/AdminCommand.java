package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.*;
import me.jackz.missilewars.lib.ConfigOption;
import me.jackz.missilewars.lib.DataLoader;
import me.jackz.missilewars.game.missile.SpawnableMissile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor {
    private MissileWars plugin;

    public AdminCommand(MissileWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help") ) {
            sender.sendMessage("§6Missile Wars - Admin Menu");
            sender.sendMessage("§e/mwa give <player/red/green> <item> [bypass]§7- gives player or team an item");
            sender.sendMessage("§e/mwa choose [item] §7- activate item spawn manually");
            sender.sendMessage("§e/mwa items §7- get all items");
            sender.sendMessage("§e/mwa scramble §7- scramble the teams");
            sender.sendMessage("§e/mwa game <start/reset/stop/reload> §7- start, stop, and reload game");
            sender.sendMessage("§e/mwa reload <all/config/data/stats> §7- reload any or all configuration files");
            sender.sendMessage("§e/mwa config §7- change game rules and configs");
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "give": {
                if(!sender.hasPermission("missilewars.admin.give")) {
                    sender.sendMessage("§cYou do not have permission");
                    return true;
                }

                if(args.length >= 3) {
                    boolean bypass = (args.length >= 4) && args[3].equalsIgnoreCase("bypass");
                    if(bypass && !sender.hasPermission("missilewars.admin.bypass")) {
                        sender.sendMessage("§cYou do not have permission to bypass");
                        return true;
                    }
                    if(!MissileWars.gameManager.getState().isGameActive()) {
                        sender.sendMessage("§cGame has not started yet.");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("red")) {
                        List<Player> players = MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED);
                        ItemStack item = ItemSystem.getItem(args[2].toLowerCase().trim());
                        if (item != null) {
                            for (Player player : players) {
                                ItemSystem.giveItem(player, item, bypass);
                            }
                            sender.sendMessage("§aGave the §cred team §aa " + args[2].toLowerCase().trim());

                        }else {
                            sender.sendMessage("§cUnknown item. Try /mwa items");
                        }
                    }else if(args[1].equalsIgnoreCase("green")) {
                        List<Player> players = MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN);
                        ItemStack item = ItemSystem.getItem(args[2].toLowerCase().trim());
                        if (item != null) {
                            for (Player player : players) {
                                ItemSystem.giveItem(player, item, bypass);
                            }
                            sender.sendMessage("§aGave the green team §aa " + args[2].toLowerCase().trim());
                        }else {
                            sender.sendMessage("§cUnknown item. Try /mwa items");
                        }
                    }else {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            if (MissileWars.gameManager.players().has(player)) {
                                ItemStack item = ItemSystem.getItem(args[2].toLowerCase().trim());
                                if(item != null) {
                                    ItemSystem.giveItem(player, item, bypass);
                                }else{
                                    player.sendMessage("§cUnknown item. Try /mwa items");
                                }
                            }else{
                                sender.sendMessage("§cPlayer is not in game.");
                            }
                        } else {
                            sender.sendMessage("§cThat player could not be found. ");
                        }
                    }
                }else{
                    sender.sendMessage("§cInvalid usage. /mwa give <red/green/[player]> <item>");
                }
                break;
            }
            case "scramble": {
                if(!sender.hasPermission("missilewars.admin.scramble")) {
                    sender.sendMessage("§cYou do not have permission");
                    return true;
                }
                List<Player> players = new ArrayList<>();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) continue;
                    if(MissileWars.gameManager.players().has(player)) {
                        MissileWars.gameManager.players().remove(player);
                        players.add(player);
                    }else {
                        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(player);
                        if (team == GamePlayers.MWTeam.NONE) {
                            players.add(player);
                        }
                    }
                }
                //2 / 2.0 -> 1
                int first_team_size = (int) Math.floor(players.size() / 2.0);
                boolean greenTeamOverflow = Math.random() > .5;

                Collections.shuffle(players);
                int first_team_players = 0;
                GamePlayers.MWTeam firstTeam = greenTeamOverflow ? GamePlayers.MWTeam.GREEN : GamePlayers.MWTeam.RED;
                GamePlayers.MWTeam secondTeam = greenTeamOverflow ? GamePlayers.MWTeam.RED: GamePlayers.MWTeam.GREEN;

                for (Player player : players) {
                    if(first_team_players < first_team_size) {
                        first_team_players++;
                        MissileWars.gameManager.players().joinPlayer(player, firstTeam);
                    }else{
                        MissileWars.gameManager.players().joinPlayer(player, secondTeam);
                    }
                }
                plugin.getDisplayManager().refreshSidebar();
                int second_team_players = players.size() - first_team_players;
                if(greenTeamOverflow) {
                    Bukkit.getServer().broadcastMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",first_team_players,second_team_players));
                }else{
                    Bukkit.getServer().broadcastMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",second_team_players,first_team_players));
                }
                return true;
            }
            case "choose": {
                if(!sender.hasPermission("missilewars.admin.choose")) {
                    sender.sendMessage("§cYou do not have permission");
                    return true;
                }
                if(!MissileWars.gameManager.getState().isGameActive()) {
                    sender.sendMessage("§cGame has not started yet.");
                    return true;
                }
                if(args.length >= 2) {
                    String query = args[1].toLowerCase().trim();
                    boolean bypass = (args.length >= 3) && args[2].equalsIgnoreCase("bypass");
                    if(bypass && !sender.hasPermission("missilewars.admin.bypass")) {
                            sender.sendMessage("§cYou do not have permission to bypass");
                            return true;
                    }
                    ItemStack item = ItemSystem.getItem(query);
                    if(item == null) {
                        sender.sendMessage("§cUnknown item type specified");
                    }else {
                        for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN)) {
                            ItemSystem.giveItem(player, item, bypass);
                        }
                        for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED)) {
                            ItemSystem.giveItem(player, item, bypass);
                        }
                    }
                }else{
                    ItemSystem.chooseItem();
                }
                break;
            }
            case "items": {
                sender.sendMessage("§6Missile Wars - Items");
                List<String> itemSet = ItemSystem.getTypes();
                int i=0;
                for (String s : itemSet) {
                    String name = s.substring(0, 1).toUpperCase() + s.substring(1);
                    TextComponent tc = new TextComponent("§6" + ++i + ". §e" + name);
                    if(sender.hasPermission("missilewars.admin.choose")) {
                        TextComponent giveall = new TextComponent(" §d[Give All]");
                        giveall.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa choose " + s));
                        tc.addExtra(giveall);
                    }
                    if(sender.hasPermission("missilewars.admin.give")) {
                        TextComponent giveallred = new TextComponent(" §c[Give All Red]");
                        TextComponent giveallgreen = new TextComponent(" §a[Give All Green]");
                        giveallred.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa give red " + s));
                        giveallgreen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa give green " + s));
                        tc.addExtra(giveallred);
                        tc.addExtra(giveallgreen);
                    }

                    sender.spigot().sendMessage(tc);
                }
                break;
            }
            case "game": {
                if(!sender.hasPermission("missilewars.admin.game")) {
                    sender.sendMessage("§cYou do not have permission.");
                    return true;
                }
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /mwa game <start/reset/stop/reload>");
                }else{
                    switch(args[1].toLowerCase()) {
                        case "start":
                            if(sender.hasPermission("missilewars.admin.game.start")) {
                                MissileWars.gameManager.start();
                            }else{
                                sender.sendMessage("§cYou do not have permission to start the game.");
                            }
                            break;
                        case "reset": {
                            if(sender.hasPermission("missilewars.admin.game.stop")) {
                                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                                    if(onlinePlayer.getGameMode() != GameMode.CREATIVE) {
                                        onlinePlayer.setGameMode(GameMode.SPECTATOR);
                                    }
                                }
                                Reset.reset();
                            }else{
                                sender.sendMessage("§cYou do not have permission to start the game.");
                            }
                            break;
                        }
                        case "stop":
                            if(sender.hasPermission("missilewars.admin.game.stop")) {
                                MissileWars.gameManager.end();
                            }else{
                                sender.sendMessage("§cYou do not have permission to stop the game.");
                            }
                            break;
                        case "reload":
                            MissileWars.gameManager.reload();
                            break;
                        case "debug":
                            sender.sendMessage("active: " + MissileWars.gameManager.getState().isGameActive());
                            String missileList = GameManager.getMissileLoader().getMissiles().stream().map(SpawnableMissile::getId).collect(Collectors.joining(","));
                            sender.sendMessage("Missiles: " + missileList);
                            sender.sendMessage("SPAWN_POINT: " + GameConfig.SPAWN_LOCATION);
                            sender.sendMessage("options:");
                            for (ConfigOption option : GameConfig.getOptions()) {
                                sender.sendMessage(option.getId() + " value: " + option.getValue());
                            }
                            break;
                        default:
                            sender.sendMessage("§cUnknown option, try: /mwa game <start/reset/stop/reload>");
                    }
                }
                break;
            }
            case "config": {
                if(args.length < 2) {
                    if(!sender.hasPermission("missilewars.admin.config")) {
                        sender.sendMessage("§cYou do not have permission.");
                        return true;
                    }
                    sender.sendMessage("§cUsage: /mwa config <[property name]/help/save> [new value]");
                }else {

                    switch(args[1].toLowerCase()) {
                        case "save": {
                            if(sender.hasPermission("missilewars.admin.config")) {
                                try {
                                    GameConfig.save();
                                    sender.sendMessage("§aSuccessfully saved configuration.");
                                } catch (Exception ex) {
                                    sender.sendMessage("§cSomething happened while attempting to save configuration. Check console for details.");
                                    ex.printStackTrace();
                                }
                            }else {
                                sender.sendMessage("§cYou do not have permission to save the settings.");
                            }
                            break;
                        }
                        case "list":
                        case "help":
                            sender.sendMessage("§6Available Settings: <Name> (<id>)");
                            sender.sendMessage("§6§o(Hover over an item to see information, click to set)");
                            for (ConfigOption option : GameConfig.getOptions()) {
                                sender.spigot().sendMessage(option.getTextComponent("§e"));
                            }
                            break;
                        default:
                            ConfigOption option = GameConfig.getOption(args[1]);
                            if(option != null) {
                                if (args.length >= 3) {
                                    if(sender.hasPermission("missilewars.admin.config." + option.getSafeId())) {
                                        Object result = option.parseInput(args[2]);
                                        switch(option.getType()) {
                                            case Boolean: {
                                                option.setValue(result);
                                                break;
                                            }
                                            case Integer: {
                                                if(result != null) {
                                                    option.setValue(result);
                                                }else{
                                                    sender.sendMessage(option.getIntRangeText());
                                                    return true;
                                                }
                                            }
                                            default:
                                                option.setValue(result);
                                        }
                                        sender.sendMessage("§aChanged option §9" + option.getId() + " §ato §9" + result + "§a successfully");
                                    }else{
                                        sender.sendMessage("§cYou do not have permission to change this setting");
                                    }
                                } else {
                                    sendConfig(sender, option);
                                }
                                break;
                                //todo: print option
                            }else{
                                sender.sendMessage("Could not find that option. §cUsage: /mwa config <[property name]/help/save> [new value]");
                            }
                    }
                }
                break;
            }
            case "reload": {
                if(!sender.hasPermission("missilewars.admin.reload")) {
                    sender.hasPermission("§cYou do not have permission.");
                    return true;
                }
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /mwa reload <all/config/stats/data>");
                }else{
                    switch(args[1].toLowerCase()) {
                        case "stats":
                            GameManager.getStats().reload();
                            sender.sendMessage("§aSuccessfully reloaded statistics.");
                            break;
                        case "config":
                            GameConfig.reload();
                            sender.sendMessage("§aSuccessfully reloaded configuration.");
                            break;
                        case "data":
                        case "setup":
                            DataLoader.reload();
                            sender.sendMessage("§aSuccessfully reloaded configuration.");
                        case "all":
                            GameManager.getStats().reload();
                            GameConfig.reload();
                            DataLoader.reload();
                            sender.sendMessage("§aSuccessfully reloaded.");
                            break;
                        default:
                            sender.sendMessage("§cUnknown option, try: /mwa reload <all/config/stats/data>");
                    }
                }
                break;
            }
            default:
                sender.sendMessage("§cUnknown command, please try /mwa help");
        }
        return true;
    }

    private void sendConfig(CommandSender sender, ConfigOption option) {
        TextComponent tc = option.getTextComponent("§9§n", false);
        tc.addExtra("§r §a[Hover for information]");
        sender.sendMessage("");
        sender.spigot().sendMessage(tc);
        sender.sendMessage("§eCurrent Value: §r§9" + option.getValue());
        sender.sendMessage("§7§oSet value with §e/mwa config " + option.getId() + " <" + option.getType() + ">");
    }

}