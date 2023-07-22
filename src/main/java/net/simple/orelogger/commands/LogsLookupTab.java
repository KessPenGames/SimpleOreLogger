package net.simple.orelogger.commands;

import net.simple.orelogger.config.MainConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LogsLookupTab implements TabCompleter {
    private final List<String> opt1 = new ArrayList<>();
    private final List<String> opt3 = new ArrayList<>();
    private final List<String> opt4 = new ArrayList<>();
    private final MainConfig config;

    public LogsLookupTab(MainConfig config) {
        this.config = config;
        init();
    }

    private void init() {
        opt1.add("statistics");
        opt1.add("logs");
        opt1.add("top");

        for (int i = 1; i < 10; i++) {
            opt3.add(String.valueOf(i));
        }
        for (int i = 10; i < 51; i = i+10) {
            opt4.add(String.valueOf(i));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return opt1;
        }
        if (!args[0].equals("top")) {
            if (args.length == 3) {
                return opt3;
            } else if (args.length == 4) {
                return config.getListOre();
            }
        } else {
            if (args.length == 2) {
                return config.getListOre();
            } else if (args.length == 3) {
                return opt4;
            } else if (args.length == 4) {
                return opt3;
            }
        }
        return null;
    }
}
