package me.jackz.missilewars.lib;

import me.jackz.missilewars.game.GameConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ConfigTextComponent {

    public final static ConfigOption itemInterval = new ConfigOption("iteminterval", ConfigOption.ConfigType.Integer, 15);
    public final static ConfigOption prioritizeDefense = new ConfigOption("preferdefense", ConfigOption.ConfigType.Boolean, false);
    public final static ConfigOption midGameJoins = new ConfigOption("midgamejoin", ConfigOption.ConfigType.Boolean, true);
    public final static ConfigOption maxItemSize = new ConfigOption("maxitemsize", ConfigOption.ConfigType.Integer, 1);
    public final static ConfigOption randomizeMode = new ConfigOption("randomizemode", ConfigOption.ConfigType.Integer, 0);


    static {

        itemInterval.setMeta("Item Spawn Interval","Sets the base interval for item spawning (scaled up per players)");
        itemInterval.setRange(1, 99, false);

        prioritizeDefense.setMeta("Prioritize Defense Items","Should defense items be spawned more often");
        midGameJoins.setMeta("Allow Midgame Joins","Should players be allowed to join in the middle of a game?");

        maxItemSize.setMeta("Max Item Size","Set the maximum amount of each type of item a player can hold");
        maxItemSize.setRange(1, 64, true);

        randomizeMode.setMeta("Randomize Mode","Sets how items are distributed","0 -> Same items given to all players","1 -> Different items given per team","2 -> Different items for every player");
        randomizeMode.setRange(0,2,false);

    }
}
