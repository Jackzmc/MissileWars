package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.*;
import me.jackz.missilewars.lib.ConfigOption;
import me.jackz.missilewars.lib.ConfigTextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

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
            sender.sendMessage("§e/mwa choose <item> §7- activate item spawn manually");
            sender.sendMessage("§e/mwa items §7- get all items");
            sender.sendMessage("§e/mwa scramble §7- Scramble the teams");
            sender.sendMessage("§e/mwa game <start/reset/stop/reload>");
            sender.sendMessage("§e/mwa reload <all/config/stats");
            sender.sendMessage("§e/mwa config §7- Change game rules and configs");
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "give": {
                if(args.length >= 3) {
                    boolean bypass = (args.length >= 4) && args[3].equalsIgnoreCase("bypass");
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
                if(args[1].equalsIgnoreCase("scramble")) {
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
                    sender.sendMessage("§c[debug] Scrambling " + players.size() + " players");
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
                    //TODO: add players to gamemanager gameplayers
                    plugin.getDisplayManager().refreshSidebar();
                    int second_team_players = players.size() - first_team_players;
                    if(greenTeamOverflow) {
                        sender.sendMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",first_team_players,second_team_players));
                    }else{
                        sender.sendMessage(String.format("§eTeams have been scrambled! §a%d Green §6- §c%d Red",second_team_players,first_team_players));
                    }
                    return true;
                }else{
                    sender.sendMessage("§cUnknown option, /mwa teams for help");
                }
            }
            case "choose": {
                if(args.length >= 2) {
                    String query = args[1].toLowerCase().trim();
                    boolean bypass = (args.length >= 3) && args[2].equalsIgnoreCase("bypass");
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
                    TextComponent giveall = new TextComponent(" §d[Give All]");
                    TextComponent giveallred = new TextComponent(" §c[Give All Red]");
                    TextComponent giveallgreen = new TextComponent(" §a[Give All Green]");
                    giveall.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa choose " + s));
                    giveallred.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa give red " + s));
                    giveallgreen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mwa give green " + s));
                    tc.addExtra(giveall);
                    tc.addExtra(giveallred);
                    tc.addExtra(giveallgreen);
                    sender.spigot().sendMessage(tc);
                }
                break;
            }
            case "game": {
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /mwa game <start/reset/stop/reload>");
                }else{
                    switch(args[1].toLowerCase()) {
                        case "start":
                            MissileWars.gameManager.start();

                            break;
                        case "reset": {
                            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                                if(onlinePlayer.getGameMode() != GameMode.CREATIVE) {
                                    onlinePlayer.setGameMode(GameMode.SPECTATOR);
                                }
                            }
                            Reset.reset();
                            break;
                        }
                        case "stop":
                            MissileWars.gameManager.end();
                            break;
                        case "reload":
                            MissileWars.gameManager.reload();
                            break;
                        default:
                            sender.sendMessage("§cUnknown option, try: /mwa game <start/reset/stop/reload>");
                    }
                }
                break;
            }
            case "config": {
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /mwa config <[property name]/help/save> [new value]");
                }else {
                    switch(args[1].toLowerCase()) {
                        case "save": {
                            try {
                                MissileWars.gameManager.getConfig().save();
                                sender.sendMessage("§aSuccessfully saved configuration.");
                            }catch(Exception ex) {
                                sender.sendMessage("§cSomething happened while attempting to save configuration. Check console for details.");
                                ex.printStackTrace();
                            }
                            break;
                        }
                        case "1":
                        case "iteminterval": {
                            ConfigOption configOption = ConfigTextComponent.itemInterval;
                            if (args.length >= 3) {
                                Integer number = (Integer) configOption.parseInput(args[2]);
                                if (number != null) {
                                    MissileWars.gameManager.getConfig().setItemIntervalSec(number);
                                    sender.sendMessage("§aChanged item interval to " + number);
                                } else {
                                    sender.sendMessage("§cValue must be a valid number on the range " + configOption.getRangeText());
                                }
                            } else {
                                sendConfig(sender, configOption, MissileWars.gameManager.getConfig().getItemInterval());
                            }
                            break;
                        }
                        case "2":
                        case "preferdefense": {
                            ConfigOption configOption = ConfigTextComponent.prioritizeDefense;
                            if (args.length >= 3) {
                                boolean value = (Boolean) configOption.parseInput(args[2]);
                                MissileWars.gameManager.getConfig().setPrioritizeDefense(value);
                                if(value) {
                                    sender.sendMessage("§aNow preferring defense items");
                                }else{
                                    sender.sendMessage("§cAll items are randomized equally");
                                }
                            } else {
                                sendConfig(sender, configOption, MissileWars.gameManager.getConfig().isPrioritizeDefenseEnabled());
                            }
                        }
                        case "3":
                        case "midgamejoin": {
                            ConfigOption configOption = ConfigTextComponent.midGameJoins;
                            if (args.length >= 3) {
                                boolean value = (Boolean) configOption.parseInput(args[2]);
                                MissileWars.gameManager.getConfig().setMidgameJoins(value);
                                if(value) {
                                    sender.sendMessage("§aEnabled joining a game in session.");
                                }else{
                                    sender.sendMessage("§cDisabled joining a game in session");
                                }
                            } else {
                                sendConfig(sender, configOption, MissileWars.gameManager.getConfig().isMidGameJoinAllowed());
                            }
                            break;
                        }
                        case "4":
                        case "maxitemsize": {
                            ConfigOption configOption = ConfigTextComponent.maxItemSize;
                            if (args.length >= 3) {
                                Integer number = (Integer) configOption.parseInput(args[2]);
                                if (number != null) {
                                    MissileWars.gameManager.getConfig().setMaxItems(number);
                                    sender.sendMessage("§aChanged max item size to " + number);
                                } else {
                                    sender.sendMessage("§cValue must be a valid number on the range " + configOption.getRangeText());
                                }
                            } else {
                                sendConfig(sender, configOption, MissileWars.gameManager.getConfig().getMaxItems());
                            }
                            break;
                        }
                        case "5":
                        case "randomizemode": {
                            ConfigOption configOption = ConfigTextComponent.randomizeMode;
                            if (args.length >= 3) {
                                Integer number = (Integer) configOption.parseInput(args[2]);
                                if (number != null) {
                                    MissileWars.gameManager.getConfig().setRandomizeMode(number);
                                    sender.sendMessage("§aChanged randomize mode to " + number);
                                } else {
                                    sender.sendMessage("§cValue must be a valid number on the range " + configOption.getRangeText());
                                }
                            } else {
                                TextComponent tc = configOption.getTextComponent("§9§n", false);
                                tc.addExtra(" §a[Hover for information]");
                                sender.spigot().sendMessage(tc);
                                sender.sendMessage("§eCurrent Value: §r§9" + MissileWars.gameManager.getConfig().getRandomizeMode());
                                sender.sendMessage("§7§oSet value with §e/mwa config " + configOption.getId() + " <" + configOption.getType() + ">");
                            }
                            break;
                        }
                        case "list":
                        case "help":
                            sender.sendMessage("§6Available Settings: <Name> (<id>)");
                            sender.sendMessage("§6§o(Hover over an item to see information, click to set)");
                            sender.spigot().sendMessage(ConfigTextComponent.itemInterval.getTextComponent("§e"));
                            sender.spigot().sendMessage(ConfigTextComponent.prioritizeDefense.getTextComponent("§e"));
                            sender.spigot().sendMessage(ConfigTextComponent.midGameJoins.getTextComponent("§e"));
                            sender.spigot().sendMessage(ConfigTextComponent.maxItemSize.getTextComponent("§e"));
                            sender.spigot().sendMessage(ConfigTextComponent.randomizeMode.getTextComponent("§e"));
                            break;
                        default:
                            sender.sendMessage("§cUsage: /mwa config <[property name]/help/save> [new value]");
                    }
                }
                break;
            }
            case "reload": {
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /mwa reload <all/config/stats>");
                }else{
                    switch(args[1].toLowerCase()) {
                        case "stats":
                            GameManager.getStats().reload();
                            sender.sendMessage("§aSuccessfully reloaded statistics.");
                            break;
                        case "config":
                            MissileWars.gameManager.getConfig().reload();
                            sender.sendMessage("§aSuccessfully reloaded configuration.");
                            break;

                        case "all":
                            GameManager.getStats().reload();
                            MissileWars.gameManager.getConfig().reload();
                            sender.sendMessage("§aSuccessfully reloaded.");
                            break;
                        default:
                            sender.sendMessage("§cUnknown option, try: /mwa reload <all/config/stats>");
                    }
                }
                break;
            }
            default:
                sender.sendMessage("§cUnknown command, please try /mwa help");
        }
        return true;
    }

    private void sendConfig(CommandSender sender, ConfigOption option, Object value) {
        TextComponent tc = option.getTextComponent("§9§n", false);
        tc.addExtra("§r §a[Hover for information]");
        sender.spigot().sendMessage(tc);
        sender.sendMessage("§eCurrent Value: §r§9" + value);
        sender.sendMessage("§7§oSet value with §e/mwa config " + option.getId() + " <" + option.getType() + ">");
    }
}