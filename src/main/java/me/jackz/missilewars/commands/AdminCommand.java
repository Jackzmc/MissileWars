package me.jackz.missilewars.commands;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.game.ItemSystem;
import net.md_5.bungee.api.chat.ClickEvent;
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
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor {
    private MissileWars plugin;
    private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    private Team redTeam;
    private Team greenTeam;

    public AdminCommand(MissileWars plugin) {
        this.plugin = plugin;
        Scoreboard sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
        redTeam = sb.getTeam("Red");
        greenTeam = sb.getTeam("Green");
    }

    private final Map<String, String> ITEMS = new HashMap<String, String>() {{
        put("Fireball", "fill -121 45 -21 -119 45 -21 redstone_block");
        put("Arrows", "fill -121 45 -16 -119 45 -16 redstone_block");
        put("Barrier","fill -121 45 -11 -119 45 -11 redstone_block");

        put("Tomahawk","fill -121 45 -6 -119 45 -6 redstone_block");
        put("ShieldBuster","fill -121 45 -1 -119 45 -1 redstone_block");
        put("Juggernaut","fill -121 45 4 -119 45 4 redstone_block");
        put("Lightning","fill -121 45 9 -119 45 9 redstone_block");
        put("Guardian","fill -121 45 14 -119 45 14 redstone_block");
    }};
    private final Map<String, String> GIVE_ITEMS = new HashMap<String,String>() {{
        put("fireball","give %PLAYER% blaze_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Fireball\\\"}\",Lore:[\"Spawns a Fireball.\",\"Punch it to aim/launch at target.\"]}} 1");
        put("arrows","give %PLAYER% arrow{display:{Name:\"{\\\"text\\\":\\\"Flame Arrow\\\"}\",Lore:[\"Shoot to ignite TNT.\"]}} 3");
        put("barrier","give %PLAYER% snowball{display:{Name:\"{\\\"text\\\":\\\"Deploy Barrier\\\"}\",Lore:[\"Deploys a barrier after 1 second.\"]}} 1");

        put("shieldbuster","give %PLAYER% witch_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Red ShieldBuster\\\"}\",Lore:[\"Spawns a Shield Buster missile.\"]}} 1");
        put("tomahawk","give %PLAYER% creeper_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Red Tomahawk\\\"}\",Lore:[\"Spawns a Tomahawk missile.\"]}} 1");
        put("juggernaut","give %PLAYER% ghast_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Red Juggernaut\\\"}\",Lore:[\"Spawns a Juggernaut missile.\"]}} 1");
        put("lightning","give %PLAYER% ocelot_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Red Lightning\\\"}\",Lore:[\"Spawns a Lightning missile.\"]}} 1");
        put("guardian","give %PLAYER% guardian_spawn_egg{display:{Name:\"{\\\"text\\\":\\\"Deploy Red Guardian\\\"}\",Lore:[\"Spawns a Guardian missile.\"]}} 1");
    }};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help") ) {
            sender.sendMessage("§6Missile Wars - Admin Menu");
            sender.sendMessage("§e/mwa give <player/red/green> <item> §7- gives player or team an item");
            sender.sendMessage("§e/mwa choose <item> §7- activate item spawn manually");
            sender.sendMessage("§e/mwa items §7- get all items");
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "give": {
                if(args.length >= 3) {
                    if(args[1].equalsIgnoreCase("red")) {
                        List<Player> players = MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED);
                        ItemStack item = ItemSystem.getItem(args[2].toLowerCase().trim());
                        if (item != null) {
                            for (Player player : players) {
                                ItemSystem.giveItem(player,item,1);
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
                                ItemSystem.giveItem(player,item,1);
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
                                    ItemSystem.giveItem(player,item,1);
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
            case "teams": {
                if(args.length >= 2) {
                    if(args[1].equalsIgnoreCase("scramble")) {
                        List<Player> players = new ArrayList<>();
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) continue;
                            if(MissileWars.gameManager.players().has(player)) {
                                MissileWars.gameManager.players().remove(player);
                                players.add(player);
                            }
                            Team team = player.getScoreboard().getEntryTeam(player.getName());
                            if(team == null) {
                                players.add(player);
                            }
                        }
                        //2 / 2.0 -> 1
                        int first_team_size = (int) Math.floor(players.size() / 2.0);
                        boolean greenTeamOverflow = Math.random() > .5;

                        Collections.shuffle(players);
                        int first_team_players = 0;
                        Team firstTeam = greenTeamOverflow ? greenTeam : redTeam;
                        Team secondTeam = greenTeamOverflow ? redTeam : greenTeam;

                        for (Player player : players) {
                            if(first_team_players < first_team_size) {
                                first_team_players++;
                                firstTeam.addEntry(player.getName());
                            }else{
                                secondTeam.addEntry(player.getName());
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
                }else{
                    sender.sendMessage("§cTeams options: /mwa teams scramble");
                }
                //todo: AdminTeamCommand
            }
            case "choose": {
                if(args.length >= 2) {
                    String query = args[1].toLowerCase().trim();
                    int max_of_item = MissileWars.gameManager.getConfig().getMaxItems();
                    ItemStack item = ItemSystem.getItem(query);
                    if(item == null) {
                        sender.sendMessage("§cUnknown item type specified");
                    }else {
                        for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.GREEN)) {
                            ItemSystem.giveItem(player, item, max_of_item);
                        }
                        for (Player player : MissileWars.gameManager.players().get(GamePlayers.MWTeam.RED)) {
                            ItemSystem.giveItem(player, item, max_of_item);
                        }
                    }
                }else{
                    ItemSystem.chooseItem();
                }
                break;
            }
            case "items": {
                sender.sendMessage("§6Missile Wars - Items");
                Set<String> itemSet = ITEMS.keySet();
                int i=0;
                for (String s : itemSet) {
                    TextComponent tc = new TextComponent("§6" + ++i + ". §e" + s);
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
            case "start": {
                MissileWars.gameManager.start();
                break;
            }
            case "reset": {
                MissileWars.gameManager.reset();
                break;
            }
            case "stop": {
                MissileWars.gameManager.end();
                break;
            }
            default:
                sender.sendMessage("§cUnknown command, please try /mwa help");
        }
        return true;
    }
}