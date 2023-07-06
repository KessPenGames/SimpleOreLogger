package net.simple.orelogger.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LogsLookupTab implements TabCompleter {
    private final List<String> opt1 = new ArrayList<>();
    private final List<String> opt3 = new ArrayList<>();

    public LogsLookupTab() {
        init();
    }

    private void init() {
        opt1.add("statistics");
        opt1.add("logs");

        for (int i = 1; i < 51; i++) {
            opt3.add(String.valueOf(i));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return opt1;
        } else if (args.length == 2) {
            List<String> opt2 = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                opt2.add(player.getName());
            }
            return opt2;
        } else if (args.length == 3) {
            return opt3;
        }
        return null;
    }
}
