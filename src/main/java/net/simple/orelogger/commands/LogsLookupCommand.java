package net.simple.orelogger.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.simple.orelogger.config.MainConfig;
import net.simple.orelogger.database.OreLogsDatabase;
import net.simple.orelogger.database.OreLogsTable;
import net.simple.orelogger.utils.ColorText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LogsLookupCommand implements CommandExecutor {
    private final MainConfig config;
    private final OreLogsDatabase database;

    public LogsLookupCommand(MainConfig config, OreLogsDatabase database) {
        this.config = config;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        List<String> args = Arrays.stream(strings).toList();
        if (args.size() != 3) {
            sender.sendMessage(ColorText.getHEXColored(config.getNotArgumentsMessage()));
            return true;
        }
        String opt1 = args.get(0);
        OfflinePlayer opt2 = Bukkit.getOfflinePlayer(args.get(1));
        if (opt2.getName() == null) {
            sender.sendMessage(ColorText.getHEXColored(config.getPlayerNotFoundMessage()));
            return true;
        }
        int opt3 = Integer.parseInt(args.get(2));
        if (opt1.equals("statistics")) {
            if (!sender.hasPermission("simple.orelogger.orelogger.statistics")) {
                sender.sendMessage(ColorText.getHEXColored(config.getNotPermissionMessage()));
                return true;
            }
            database.getAllStatistics(opt2.getUniqueId(), opt3 + 1).thenAccept(statistics -> {
                List<String> header = config.getStatisticHeader();
                List<String> ores = new ArrayList<>();
                List<String> footer = config.getStatisticFooter();
                header.replaceAll(head -> replacePlStat(head, opt2, opt3, "", "", 0));
                footer.replaceAll(head -> replacePlStat(head, opt2, opt3, "", "", 0));
                for (var entry : getOreCount(statistics).entrySet()) {
                    String color = config.getOreColor().get(entry.getKey());
                    color = color == null ? "&r" : color;
                    ores.add(replacePlStat(config.getStatisticOre(), opt2, opt3,
                            entry.getKey(), color, entry.getValue()));
                }
                header.forEach(sender::sendMessage);
                ores.forEach(sender::sendMessage);
                footer.forEach(sender::sendMessage);
            });
        } else if (opt1.equals("logs")) {
            if (!sender.hasPermission("simple.orelogger.orelogger.logs")) {
                sender.sendMessage(ColorText.getHEXColored(config.getNotPermissionMessage()));
                return true;
            }
            database.getLastStatistics(opt2.getUniqueId(), opt3).thenAccept(logs -> {
                List<String> header = config.getLogsHeader();
                List<TextComponent> ores = new ArrayList<>();
                List<String> footer = config.getLogsFooter();
                header.replaceAll(head -> replacePlLogs(head, opt2, opt3, "", ""));
                footer.replaceAll(head -> replacePlLogs(head, opt2, opt3, "", ""));
                for (OreLogsTable ore : logs) {
                    String color = config.getOreColor().get(ore.getOreType());
                    color = color == null ? "&r" : color;
                    Location loc = ore.getLocation();
                    String coordinatesString = config.getCoordinatesPattern()
                            .replace("%x%", String.valueOf(loc.getBlockX()))
                            .replace("%y%", String.valueOf(loc.getBlockY()))
                            .replace("%z%", String.valueOf(loc.getBlockZ()));
                    TextComponent coordinates = Component.text(ColorText.getHEXColored(coordinatesString))
                            .hoverEvent(Component.text(ColorText
                                    .getHEXColored(config.getCoordinatesHoverMessage())))
                            .clickEvent(ClickEvent.suggestCommand(
                                    "/tp " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()));
                    TextComponent message = (TextComponent) Component.text(replacePlLogs(
                            config.getLogsOre(), opt2, opt3, ore.getOreType(), color
                    )).replaceText(TextReplacementConfig.builder().match("%coordinates%")
                            .replacement(coordinates).build());
                    ores.add(message);
                }
                header.forEach(sender::sendMessage);
                ores.forEach(sender::sendMessage);
                footer.forEach(sender::sendMessage);
            });
        } else {
            sender.sendMessage(ColorText.getHEXColored(config.getNotArgumentsMessage()));
            return true;
        }
        return true;
    }

    private Map<String, Integer> getOreCount(List<OreLogsTable> list) {
        Map<String, Integer> map = new HashMap<>();
        for (OreLogsTable ore : list) {
            if (!map.containsKey(ore.getOreType())) {
                map.put(ore.getOreType(), 1);
                continue;
            }
            int count = map.get(ore.getOreType());
            map.put(ore.getOreType(), count+1);
        }
        return map;
    }

    private String replacePlLogs(String s, OfflinePlayer player, int count,
                                 String ore_type, String ore_color) {
        return ColorText.getHEXColored(s
                .replace("%count%", String.valueOf(count))
                .replace("%player%", player.getName())
                .replace("%ore_color%", ore_color)
                .replace("%ore_type%", ore_type)
        );
    }

    private String replacePlStat(String s, OfflinePlayer player, int day,
                                                String ore_type, String ore_color, int count) {
        return ColorText.getHEXColored(s
                .replace("%day%", String.valueOf(day))
                .replace("%player%", player.getName())
                .replace("%ore_color%", ore_color)
                .replace("%ore_type%", ore_type)
                .replace("%count%", String.valueOf(count))
        );
    }
}
