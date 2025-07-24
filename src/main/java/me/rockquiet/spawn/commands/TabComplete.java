package me.rockquiet.spawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!cmd.getLabel().equalsIgnoreCase("spawn")) {
            return Collections.emptyList();
        }

        final List<String> results = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands and player names
            if (sender.hasPermission("spawn.others")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    results.add(p.getName());
                }
                results.remove(sender.getName());
            }
            if (sender.hasPermission("spawn.set")) {
                results.add("set");
            }
            if (sender.hasPermission("spawn.reload")) {
                results.add("reload");
            }
            
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], results, completions);
            Collections.sort(completions);
            return completions;
            
        } else if (args.length == 2) {
            // Second argument: world names for both "/spawn set [world]" and "/spawn [player] [world]"
            
            if (args[0].equalsIgnoreCase("set") && sender.hasPermission("spawn.set")) {
                // "/spawn set [world]" - show all worlds
                for (World world : Bukkit.getWorlds()) {
                    results.add(world.getName());
                }
            } else if (sender.hasPermission("spawn.others")) {
                // Check if first argument is a valid player name, then show worlds
                Player targetPlayer = Bukkit.getPlayerExact(args[0]);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    // "/spawn [player] [world]" - show all worlds
                    for (World world : Bukkit.getWorlds()) {
                        results.add(world.getName());
                    }
                }
            }
            
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], results, completions);
            Collections.sort(completions);
            return completions;
        }

        return Collections.emptyList();
    }
}
