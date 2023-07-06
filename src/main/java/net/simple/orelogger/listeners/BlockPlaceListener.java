package net.simple.orelogger.listeners;

import net.simple.orelogger.config.MainConfig;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class BlockPlaceListener implements Listener {
    private final Plugin plugin;
    private final MainConfig config;

    public BlockPlaceListener(Plugin plugin, MainConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        if (config.getListOre().contains(b.getType().toString())) b
                .setMetadata("PLACED", new FixedMetadataValue(plugin, "something"));
    }
}
