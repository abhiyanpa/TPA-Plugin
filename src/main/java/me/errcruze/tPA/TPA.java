package me.errcruze.tPA;

import me.errcruze.tPA.commands.*;
import me.errcruze.tPA.managers.ConfigManager;
import me.errcruze.tPA.managers.TpaManager;
// Update to match your actual path
import me.errcruze.tPA.utils.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class TPA extends JavaPlugin {
    private TpaManager tpaManager;
    private ConfigManager configManager;
    private final AtomicInteger totalTeleports = new AtomicInteger(0);

    @Override
    public void onEnable() {
        // Load configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        // Initialize manager
        tpaManager = new TpaManager(this, configManager);

        // Register commands
        getCommand("tpa").setExecutor(new TpaCommand(tpaManager));
        getCommand("tpahere").setExecutor(new TpaHereCommand(tpaManager));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpaManager));
        getCommand("tpadeny").setExecutor(new TpaDenyCommand(tpaManager));
        getCommand("tpatoggle").setExecutor(new TpaToggleCommand(tpaManager));

        // Setup bStats
        setupMetrics();

        // Enable message
        getServer().getConsoleSender().sendMessage(
                configManager.getMessage("Prefix") + ChatColor.GREEN + " Plugin has been enabled!");
    }

    private void setupMetrics() {
        // Initialize Metrics with plugin ID 23995
        Metrics metrics = new Metrics(this, 23995);

        // Add custom chart for sound settings
        metrics.addCustomChart(new Metrics.SimplePie("sounds_enabled", () ->
                String.valueOf(configManager.areSoundsEnabled())));

        // Add custom chart for cooldown settings
        metrics.addCustomChart(new Metrics.SimplePie("cooldown_time", () ->
                String.valueOf(configManager.getRequestCooldown())));

        // Add custom chart for timeout settings
        metrics.addCustomChart(new Metrics.SimplePie("timeout_time", () ->
                String.valueOf(configManager.getRequestTimeout())));

        // Add custom chart for teleport count
        metrics.addCustomChart(new Metrics.SingleLineChart("teleports", () -> {
            int value = totalTeleports.get();
            totalTeleports.set(0); // Reset for next time
            return value;
        }));

        // Add custom chart for server version
        metrics.addCustomChart(new Metrics.SimplePie("server_version", () ->
                getServer().getBukkitVersion().split("-")[0]));
    }

    @Override
    public void onDisable() {
        if (tpaManager != null) {
            tpaManager.clearAllRequests();
        }
        getServer().getConsoleSender().sendMessage(
                configManager.getMessage("Prefix") + ChatColor.RED + " Plugin has been disabled!");
    }

    public void reloadPlugin() {
        configManager.loadConfig();
        tpaManager.reloadConfig(configManager);
    }

    public void incrementTeleports() {
        totalTeleports.incrementAndGet();
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}