package me.rockquiet.spawn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> results = new ArrayList<>();
        final List<String> completions = new ArrayList<>();

        if (args.length == 1 && cmd.getLabel().equalsIgnoreCase("spawn")) {
            results.clear();
            results.add("set");
            results.add("reload");

        } else if (args.length >= 2) {
            results.clear();
        }

        StringUtil.copyPartialMatches(args[0], results, completions);
        Collections.sort(completions);
        results.clear();
        for (String s: completions) {
            results.add(s);
        }
        return results;
    }
}
