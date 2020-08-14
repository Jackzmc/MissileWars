package me.jackz.missilewars.commands.tabcompleter;

import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameConfig;
import me.jackz.missilewars.game.ItemSystem;
import me.jackz.missilewars.lib.ConfigOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommandCompleter implements TabCompleter {
    private final static List<String> EMPTY_LIST = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "give": {
                    if(args.length > 2) {
                        if(args.length < 4) {
                            return ItemSystem.getTypes();
                        }else{
                            if(sender.hasPermission("missilewars.admin.bypass")) {
                                if(args.length == 4)
                                    return getList("bypass");
                                else return EMPTY_LIST;
                            }else {
                                return EMPTY_LIST;
                            }
                        }
                    }else{
                        List<String> options = getList("red", "green");
                        options.addAll(MissileWars.gameManager.players().getAllPlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
                        return options;
                    }
                }
                case "items":
                case "scramble":
                    return EMPTY_LIST;
                case "choose":
                    if(args.length <= 2)
                        return ItemSystem.getTypes();
                    else
                        if(sender.hasPermission("missilewars.admin.bypass")) {
                            if(args.length == 3)
                                return getList("bypass");
                            else return EMPTY_LIST;
                        }else {
                            return EMPTY_LIST;
                        }
                case "game":
                    if(args.length <= 2) {
                        List<String> options = new ArrayList<>();
                        if(sender.hasPermission("missilewars.admin.game.start")) options.add("start");
                        if(sender.hasPermission("missilewars.admin.game.stop")) {
                            options.add("stop");
                            options.add("reset");
                        }
                        options.add("reload");
                        return options;
                    } else
                        return EMPTY_LIST;
                case "reload":
                    if(args.length <= 2)
                        return getList(args[0], "all", "config", "data", "stats");
                    else
                        return EMPTY_LIST;
                case "config": {
                    if(args.length <= 2) {
                        List<String> options = getList("help", "save");
                        for (ConfigOption option : GameConfig.getOptions()) {
                            if(sender.hasPermission("missilewars.admin.config" + option.getSafeId())) {
                                options.add(option.getId());
                            }
                        }
                        return options;
                    }else {
                        return EMPTY_LIST;
                    }
                }
            }
        }
        List<String> options = new ArrayList<>();
        if(sender.hasPermission("missilewars.admin.give")) options.add("give");
        if(sender.hasPermission("missilewars.admin.choose")) options.add("choose");
        options.add("items");
        if(sender.hasPermission("missilewars.admin.scramble")) options.add("scramble");
        if(sender.hasPermission("missilewars.admin.game")) options.add("game");
        if(sender.hasPermission("missilewars.admin.reload")) options.add("config");
        if(sender.hasPermission("missilewars.admin.config")) options.add("config");
        return options;
    }

    private List<String> getList(String... args) {
        final List<String> completions = new ArrayList<>();
        Collections.addAll(completions, args);
        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
        Collections.sort(completions);
        return completions;
    }
}
