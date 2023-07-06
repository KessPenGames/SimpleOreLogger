package net.simple.orelogger.listeners;

import net.simple.orelogger.config.MainConfig;
import net.simple.orelogger.task.DatabaseTaskUpdater;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class BlockBreakListener implements Listener {
    private final MainConfig config;
    private final DatabaseTaskUpdater dbTask;


    public BlockBreakListener(MainConfig config, DatabaseTaskUpdater dbTask) {
        this.config = config;
        this.dbTask = dbTask;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlock();
        String type = block.getType().toString();
        if (!config.getListOre().contains(type)) return;
        if (!block.getMetadata("PLACED").isEmpty()) return;
        dbTask.incrementLogs(uuid, type, block.getLocation());
    }
}
