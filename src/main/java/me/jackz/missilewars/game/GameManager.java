package me.jackz.missilewars.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.missilewars.MissileWars;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class GameManager {
    private MissileWars plugin;
    private GameState state;
    private GamePlayers players;
    private static GameConfig config;
    private ItemSystem itemSystem;

    private final static PotionEffect nightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 9999, 2, true, false, false);
    private final static PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, 9999, 2, true, false, false);

    public GameManager(MissileWars plugin) {
        this.state = new GameState();
        this.plugin = plugin;
        itemSystem = new ItemSystem();
        players = new GamePlayers();
        config = new GameConfig();
        initalizeScoreboard();
    }

    //#region privatemethods
    private void initalizeScoreboard() {
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
        for (Player player : players.getAll()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.addPotionEffect(nightVision);
            player.addPotionEffect(saturation);
            //todo: tp to spawnpoint
            player.setHealth(20);
            ItemSystem.giveItem(player, bow, 1);
        }
        itemSystem.start();
    }
    public void reset() {
        for (Player player : players.getAll()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.addPotionEffect(nightVision);
            player.addPotionEffect(saturation);
            player.setHealth(20);
        }
        state.setActive(false);
        Reset.reset();
        itemSystem.stop();
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
