package net.simple.orelogger.task;

import net.simple.orelogger.database.OreLogsDatabase;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseTaskUpdater extends TimerTask {
    private final OreLogsDatabase database;
    private final Map<UUID, Map<String, Map<Location, String>>> logs = new HashMap<>();

    public DatabaseTaskUpdater(OreLogsDatabase database) {
        this.database = database;
    }

    @Override
    public void run() {
        updateDatabase();
    }

    public void updateDatabase() {
        CompletableFuture.runAsync(() -> {
            for (var entry : logs.entrySet()) {
                Map<String, Map<Location, String>> values = entry.getValue();
                for (var entry1 : values.entrySet()) {
                    for (var entry2 : entry1.getValue().entrySet()) {
                        database.createStatistic(entry.getKey(), entry1.getKey(),
                                entry2.getKey(), entry2.getValue());
                    }
                }
            }
            logs.clear();
        });
    }

    public void incrementLogs(UUID uuid, String type, Location location) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        Map<String, Map<Location, String>> ores;
        if (logs.containsKey(uuid)) {
            ores = logs.get(uuid);
            Map<Location, String> map;
            if (ores.containsKey(type)) map = ores.get(type);
            else map = new HashMap<>();
            map.put(location, timeStamp);
            ores.put(type, map);
        } else {
            ores = new HashMap<>();
            Map<Location, String> map = new HashMap<>();
            map.put(location, timeStamp);
            ores.put(type, map);
        }
        logs.put(uuid, ores);
    }
}
