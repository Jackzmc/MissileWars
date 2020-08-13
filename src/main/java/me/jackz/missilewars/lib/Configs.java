package me.jackz.missilewars.lib;

import me.jackz.missilewars.game.GameConfig;

public class Configs {

    public final static ConfigOption itemInterval = new ConfigOption("item-interval", ConfigOption.ConfigType.Integer, 15);
    public final static ConfigOption midGameJoins = new ConfigOption("allow-midgame-join", ConfigOption.ConfigType.Boolean, true);
    public final static ConfigOption maxItemSize = new ConfigOption("max-item-count", ConfigOption.ConfigType.Integer, 1);
    public final static ConfigOption randomizeMode = new ConfigOption("randomize-mode", ConfigOption.ConfigType.Integer, 0);
    public final static ConfigOption showItemTimer = new ConfigOption("show-item-timer", ConfigOption.ConfigType.Boolean, true);
    public final static ConfigOption instantFireballs = new ConfigOption("instant-fireballs", ConfigOption.ConfigType.Boolean, false);
    public final static ConfigOption teleportRespawn = new ConfigOption("teleport-respawn", ConfigOption.ConfigType.Boolean, false);

    static {

        itemInterval.setMeta("Item Spawn Interval","Sets the base interval for item spawning (scaled up per players)");
        itemInterval.setIntRange(1, 99, false);

        midGameJoins.setMeta("Allow Midgame Joins","Should players be allowed to join in the middle of a game?");

        maxItemSize.setMeta("Max Item Size","Set the maximum amount of each type of item a player can hold");
        maxItemSize.setIntRange(1, 64, true);

        randomizeMode.setMeta("Randomize Mode","Sets how items are distributed","0 -> Same items given to all players","1 -> Different items given per team","2 -> Different items for every player");
        randomizeMode.setIntRange(0,2,false);

        showItemTimer.setMeta("Show XP Item Timer", "Displays a timer in player's XP bar showing when items are given.");

        instantFireballs.setMeta("Instant Fireballs", "Should fireballs instantly explode right-clicked block in the distance?", "True -> Fireballs instantly explode targetted block", "False -> Fireballs are launched normally.");
        teleportRespawn.setMeta("Teleport Respawn", "Should instead of being killed, players be teleported back up?", "True -> Players will be teleported back to their spawnpoint when below the map", "False -> Players will be killed when below the map to respawn normally.");

        GameConfig.registerOptions(
                itemInterval,
                midGameJoins,
                maxItemSize,
                randomizeMode,
                showItemTimer,
                instantFireballs,
                teleportRespawn
        );
    }
}
