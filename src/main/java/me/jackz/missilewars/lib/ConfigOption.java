package me.jackz.missilewars.lib;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigOption {
    /*todo: possibly have GameConfig not even have hardcoded values? grab it from id.
       mid-game-interval -> midgameinterval for input purposes
       and then can have a way to get value manually and reduce duplicate code
    */

    public enum ConfigType {
        String,
        Integer,
        Boolean
    }

    private String id;
    private ConfigType type;
    private String display;
    private Object defaultValue;
    private List<String> description = new ArrayList<>();

    private Integer min_number = null;
    private Integer max_number = null;
    private boolean allow_disable = false;

    public ConfigOption(String id, ConfigType type, Object defaultValue) {
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getDisplay() {
        return display;
    }

    public ConfigType getType() {
        return type;
    }

    public String getId() {
        return id;
    }
    public String[] getDescription() {
        String[] array = new String[description.size()];
        description.toArray(array);
        return array;
    }

    public TextComponent getTextComponent(String prefix, boolean includeID) {
        String baseText = prefix + display;
        if(includeID) {
            baseText += " (" + id + ")";
        }
        String rangeText = getRangeText();

        TextComponent textComponent = new TextComponent(baseText);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mwa config " + id));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                "§9" + display +
                "\n§eConfigID: §7" + id +
                "\n§eType: §7" + type +
                ((rangeText != null) ? "\n§eRange: §7" + rangeText : "") +
                "\n§6Default Value: §7" + defaultValue +
                "\n"
                + description.stream().map(v -> "\n§e" + v).collect(Collectors.joining())
        )));
        return textComponent;
    }
    public TextComponent getTextComponent(String prefix) {
        return getTextComponent(prefix, true);
    }

    public String getRangeText() {
        String text;
        if(min_number == null && max_number != null) {
            text = "<=" + max_number;
        }else if(max_number == null && min_number != null) {
            text = ">=" + min_number;
        }else if(max_number != null){
            text = min_number + " - " + max_number;
        }else{
            text = null;
        }
        if(allow_disable && text != null) {
            text += " (or -1 to disable)";
        }
        return text;
    }

    public Object parseInput(String text) {
        if(type == ConfigType.Boolean) {
            return text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes");
        }else if(type == ConfigType.Integer) {
            if(text.matches(".*\\d.*")) {
                int number = Integer.parseInt(text);
                if(allow_disable && number == -1) {
                    return -1;
                }
                if(min_number != null && number >= min_number) {
                    if(max_number != null && number <= max_number) {
                        return number;
                    }
                }
            }
            return null;
        }else{
            return text;
        }
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setDescription(String... args) {
        description.addAll(Arrays.asList(args));
    }
    public void setMeta(String display, String... args) {
        this.display = display;
        description.addAll(Arrays.asList(args));
    }

    public void setRange(int min, int max, boolean allowDisable) {
        min_number = min;
        max_number = max;
        allow_disable = allowDisable;
    }

}
