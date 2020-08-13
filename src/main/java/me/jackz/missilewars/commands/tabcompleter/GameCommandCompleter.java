package me.jackz.missilewars.commands.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCommandCompleter implements TabCompleter {
    private final static List<String> EMPTY_LIST = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "join": {
                    if(args.length <= 2) {
                        return getList("green", "red");
                    }else{
                        if(sender.hasPermission("missilewars.join.others")) {
                            if(args.length == 3)
                                return null;
                            else return EMPTY_LIST;
                        }else{
                            return EMPTY_LIST;
                        }
                    }
                }
                case "leave": return EMPTY_LIST;
                case "stats": {
                    if(args.length <= 2) {
                        return getList("global", "session");
                    }else{
                        return EMPTY_LIST;
                    }
                }
            }
        }
        List<String> options = getList("stats");
        if(sender instanceof Player) {
            options.add("join");
            options.add("leave");
        }
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

