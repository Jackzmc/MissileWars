package me.jackz.missilewars.game;

import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameManager {
    private MissileWars plugin;
    private GameState state;
    private GamePlayers players;
    private static GameConfig config;
    private ItemSystem itemSystem;



    public GameManager(MissileWars plugin) {
        this.state = new GameState();
        this.plugin = plugin;
        itemSystem = new ItemSystem();
        players = new GamePlayers();
        config = new GameConfig();
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
    //#endregion
    public void start() {
        state.setActive(true);
        ItemStack bow = ItemSystem.getItem("bow");
        for (Map.Entry<Player, GamePlayers.MWTeam> entry : players.getAll()) {
            Player player = entry.getKey();
            GamePlayers.MWTeam team = entry.getValue();
            if(team == GamePlayers.MWTeam.GREEN) {
                player.teleport(GameConfig.GREEN_SPAWNPOINT);
            }else if(team == GamePlayers.MWTeam.RED) {
                player.teleport(GameConfig.RED_SPAWNPOINT);
            }

            players.setupPlayer(player);
            //todo: tp to spawnpoint
            player.setHealth(20);
            ItemSystem.giveItem(player, bow, 1);
        }
        itemSystem.start();
    }

    public void reset() {
        Set<Player> allPlayers = players.getAllPlayers();
        for (Player player : allPlayers) {
            players.setupPlayer(player);
        }

        state.setActive(false);
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> Reset.reset(), 20 * 20);

        itemSystem.stop();
        Bukkit.getScheduler().runTaskLater(MissileWars.getInstance(), () -> {
            for(Player player : allPlayers) {
                player.getInventory().clear();
                player.teleport(GameConfig.SPAWN_LOCATION);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }, 20 * 30);
        //todo: run reset, copy regions, and reset gamestate, and players list
    }


    public void shutdown() {
        state = null;
        if(itemSystem != null) {
            itemSystem.stop();
            itemSystem = null;
        }
        players = null;
        config = null;
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
}
