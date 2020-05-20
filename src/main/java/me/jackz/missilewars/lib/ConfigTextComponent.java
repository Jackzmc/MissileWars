package me.jackz.missilewars.lib;

import me.jackz.missilewars.game.GameConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ConfigTextComponent {
    public static final TextComponent tc_itemInterval = new TextComponent("§e1. Max Item Size (maxitemsize)");
    public static final TextComponent tc_prioritize_defense = new TextComponent("\n§e2. Prioritize Defense Items (preferdefense)");
    public static final TextComponent tc_midgame_joins = new TextComponent("\n§e3. Allow Joining Midgame (midgamejoin)");
    public static final  TextComponent tc_max_item = new TextComponent("\n§e4. Max Item Stack Size (maxitemsize)");
    public static final  TextComponent tc_randomize_mode = new TextComponent("\n§e4. Randomize Mode (randomizemode)");
    static {

        tc_itemInterval.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config iteminterval"));
        tc_itemInterval.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9Item Interval" +
                "\n§eConfigID: iteminterval" +
                "\n§eType: Integer" +
                "\n§6Default: " + GameConfig.DEFAULT_item_interval_sec + " (seconds)" +
                "\n§6Range: 1 - 99 seconds" +
                "\n\n§eSets the base interval for item spawning (scaled up per players)"
        )));

        tc_prioritize_defense.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config preferdefense"));
        tc_prioritize_defense.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9Prioritize Defense" +
                "\n§eConfigID: preferdefense" +
                "\n§eType: Boolean" +
                "\n§6Default: " + GameConfig.DEFAULT_prioritize_defense +
                "\n\n§eShould defense items be spawned more often"
        )));

        tc_midgame_joins.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config midgamejoins"));
        tc_midgame_joins.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9Midgame Joins" +
                "\n§eConfigID: midgamejoins" +
                "\n§eType: Boolean" +
                "\n§6Default: " + GameConfig.DEFAULT_allow_midgame_joins +
                "\n\n§eShould players be allowed to join in middle of a game?"
        )));

        tc_max_item.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config maxitems"));
        tc_max_item.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9Max Item Stack Size" +
                "\n§eConfigID: maxitems" +
                "\n§eType: Integer" +
                "\n§6Default: " + GameConfig.DEFAULT_max_items +
                "\n§6Range: 1 - 64 (-1 to disable)" +
                "\n\n§eSet the maximum amount of each type of item a player can hold"
        )));

        tc_randomize_mode.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config randomizemode"));
        tc_randomize_mode.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9Randomize Mode" +
                "\n§eConfigID: randomizemode" +
                "\n§eType: Integer" +
                "\n§6Default: " + GameConfig.DEFAULT_randomize_mode +
                "\n\n§eSets how items are distributed" +
                "\n§e0 -> Same items given to all players" +
                "\n§e1 -> Different items given per team" +
                "\n§e2 -> Different items for every player"
        )));
    }
}
