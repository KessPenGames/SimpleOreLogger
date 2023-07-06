package net.simple.orelogger.database;

import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.UUID;

public class OreLogsTable {
    private final UUID uuid;
    private final String ore_type;
    private final Location location;
    private final Timestamp date;

    public OreLogsTable(UUID uuid, String ore_type, Location location, Timestamp date) {
        this.uuid = uuid;
        this.ore_type = ore_type;
        this.location = location;
        this.date = date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getOreType() {
        return ore_type;
    }

    public Location getLocation() {
        return location;
    }

    public Timestamp getDate() {
        return date;
    }
}
