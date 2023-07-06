package net.simple.orelogger;

import net.simple.orelogger.commands.LogsLookupCommand;
import net.simple.orelogger.commands.LogsLookupTab;
import net.simple.orelogger.config.MainConfig;
import net.simple.orelogger.database.DataSource;
import net.simple.orelogger.database.OreLogsDatabase;
import net.simple.orelogger.listeners.BlockBreakListener;
import net.simple.orelogger.listeners.BlockPlaceListener;
import net.simple.orelogger.task.DatabaseTaskUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import pl.mikigal.config.ConfigAPI;
import pl.mikigal.config.style.CommentStyle;
import pl.mikigal.config.style.NameStyle;

import java.util.Timer;

public final class SimpleOreLoggerPlugin extends JavaPlugin {
    private MainConfig config;
    private OreLogsDatabase database;
    private DatabaseTaskUpdater dbTask;
    private Timer timer;

    @Override
    public void onEnable() {
        config = ConfigAPI.init(
                MainConfig.class, NameStyle.UNDERSCORE, CommentStyle.INLINE, true, this
        );
        if (!config.getEnablePlugin()) {
            getLogger().info("Plugin not enabled in config, please turn plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        registerDatabase();
        registerTimer();
        registerListeners();
        registerCommand();
        getLogger().info("Plugin started up!");
    }

    @Override
    public void onDisable() {
        timer.cancel();
        dbTask.updateDatabase();
        getLogger().info("Plugin disabled up!");
    }

    private void registerDatabase() {
        DataSource ds = new DataSource(
                config.getMysqlIp(), config.getMysqlPort(), config.getDbName(), config.getDbUser(), config.getDbPassword()
        );
        database = new OreLogsDatabase(ds, config.getDbTable());
        getLogger().info("Connected to MySql!");
    }

    private void registerTimer() {
        timer = new Timer();
        dbTask = new DatabaseTaskUpdater(database);
        timer.scheduleAtFixedRate(dbTask, 0, config.getDatabaseUpdateMilliseconds());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(config, dbTask), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this, config), this);
    }
    
    private void registerCommand() {
        @Nullable PluginCommand command = getCommand("orelogger");
        if (command == null) {
            getLogger().info("Command not found.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        command.setExecutor(new LogsLookupCommand(config, database));
        command.setTabCompleter(new LogsLookupTab());
    }
}
