package me.errcruze.tPA.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getMessage(String path) {
        String message = config.getString("Messages." + path, "Message not found: " + path);
        String prefix = config.getString("Messages.Prefix", "&8[&bTPA&8]");
        return ChatColor.translateAlternateColorCodes('&',
                message.replace("%prefix%", prefix));
    }

    public String formatMessage(String path, String... placeholders) {
        String message = getMessage(path);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return message;
    }

    public int getRequestTimeout() {
        return config.getInt("Settings.request-timeout", 60);
    }

    public int getRequestCooldown() {
        return config.getInt("Settings.request-cooldown", 30);
    }

    public boolean areSoundsEnabled() {
        return config.getBoolean("Settings.enable-sounds", true);
    }

    public String getSound(String type, boolean legacy) {
        String path = "Sounds." + type + (legacy ? ".legacy" : ".modern");
        return config.getString(path);
    }

    public float getSoundVolume(String type) {
        return (float) config.getDouble("Sounds." + type + ".volume", 1.0);
    }

    public float getSoundPitch(String type) {
        return (float) config.getDouble("Sounds." + type + ".pitch", 1.0);
    }

    public boolean isSoundEnabled(String type) {
        return config.getBoolean("Sounds." + type + ".enabled", true);
    }
}