package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.Set;

public class GameManager {
    private GameState state;
    private GamePlayers players;
    private static GameConfig config;
    private static MissileLoader missileLoader;
    private ItemSystem itemSystem;
    private static StatsTracker stats;



    public GameManager(MissileWars plugin) {
        this.state = new GameState();
        itemSystem = new ItemSystem();
        players = new GamePlayers();
        config = new GameConfig();
        stats = new StatsTracker();
        missileLoader = new MissileLoader();
        initializeScoreboard();
    }

    //#region privatemethods
    private void initializeScoreboard() {
        Scoreboard main = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        if(main.getTeam("Red") == null) {
            Team red = main.registerNewTeam("Red");

            red.setColor(ChatColor.RED);
            red.setAllowFriendlyFire(false);
            red.setCanSeeFriendlyInvisibles(true);
            red.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }
        if(main.getTeam("Green") == null) {
            Team green = main.registerNewTeam("Green");

            green.setColor(ChatColor.GREEN);
            green.setAllowFriendlyFire(false);
            green.setCanSeeFriendlyInvisibles(true);
            green.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        }

        if(main.getObjective("wins") == null ) main.registerNewObjective("wins", "dummy", "Wins");
        if(main.getObjective("loses") == null ) main.registerNewObjective("loses", "dummy", "Loses");
    }
    public void ready(GamePlayers.MWTeam team) {
        state.setTeamReady(team,true);
        String name = (team == GamePlayers.MWTeam.RED) ? "§cRed" : "§aGreen";
        Bukkit.broadcastMessage(name + " team §eis ready to go!");
        if(state.isGameReady()) {
            start();
        }
    }
    //#endregion
    //todo: prevent start if is resetting
    public void start() {
        state.setActive(true);
        ItemStack bow = ItemSystem.getItem("bow");
        for (Map.Entry<Player, GamePlayers.MWTeam> entry : players.getAll()) {
            Player player = entry.getKey();
            GamePlayers.MWTeam team = entry.getValue();
            if(team == GamePlayers.MWTeam.GREEN) {
                player.teleport(GameConfig.GREEN_SPAWNPOINT);
                player.setBedSpawnLocation(GameConfig.GREEN_SPAWNPOINT);
            }else if(team == GamePlayers.MWTeam.RED) {
                player.teleport(GameConfig.RED_SPAWNPOINT);
                player.setBedSpawnLocation(GameConfig.RED_SPAWNPOINT);
            }else{
                continue;
            }
            player.sendMessage("§eMissile Wars game has started!");
            player.sendMessage("§9Tip: §7Use §e/teammsg §7to chat with your team");

            players.setupPlayer(player);
            ItemSystem.giveItem(player, bow, false);
            player.setGameMode(GameMode.SURVIVAL);
        }
        itemSystem.start();
        stats.resetGameTime();
        stats.clearSessionStats();
    }

    public void end() {
        Set<Player> allPlayers = players.getAllPlayers();
        int duration_minutes = (int) stats.getGameTimeMS() / 1000 / 60;

        stats.incSavedStat("gametime_min.total",duration_minutes);
        int longest_game = stats.getSavedStat("gametime_min.longest");
        if(longest_game < duration_minutes) {
            stats.setSavedStat("gametime_min.longest", duration_minutes);
            Bukkit.broadcastMessage("§eGame was a record breaking total of §9" + duration_minutes + " minutes §elong! ");
        }else{
            Bukkit.broadcastMessage("§eGame was a total of §9" + duration_minutes + " minutes §elong!");
        }

        for (Player player : allPlayers) {
            players.setupPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
            stats.incSavedStat("gametime_min." + player.getUniqueId(),duration_minutes);
        }
        state.setActive(false);

        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), this::reset , 20 * 15);
    }

    public void reset() {
        Reset.reset();
        itemSystem.stop();
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            Set<Player> allPlayers = players.getAllPlayers();
            for(Player player : allPlayers) {
                player.getInventory().clear();
                player.teleport(GameConfig.SPAWN_LOCATION);
                player.setGameMode(GameMode.ADVENTURE);
                players.remove(player);
            }
        }, 20 * 20);
        //todo: run reset, copy regions, and reset gamestate, and players list
    }

    public void reload() {
        itemSystem.stop();
        itemSystem.start();
    }


    public void shutdown() {
        state = null;
        if(itemSystem != null) {
            itemSystem.stop();
            itemSystem = null;
        }
        players = null;
        config = null;
        if(stats != null) {
            stats.save();
            stats = null;
        }
        missileLoader = null;
    }

    public GameState getState() {
        return state;
    }

    public GamePlayers players() {
        return players;
    }
    public GameConfig getConfig() {
        return config;
    }
    public static StatsTracker getStats() {
        return stats;
    }
    public static MissileLoader getMissileLoader() {
        return missileLoader;
    }

}
