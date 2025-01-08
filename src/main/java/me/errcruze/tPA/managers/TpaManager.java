package me.errcruze.tPA.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TpaManager {
    private final Plugin plugin;
    private final VersionManager versionManager;
    private ConfigManager config;

    private final Map<UUID, UUID> tpaRequests = new HashMap<>();
    private final Map<UUID, UUID> tpaHereRequests = new HashMap<>();
    private final Map<UUID, BukkitTask> requestTasks = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Set<UUID> toggledPlayers = new HashSet<>();

    public TpaManager(Plugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.versionManager = new VersionManager();
    }

    public boolean canSendRequest(Player player) {
        if (!player.hasPermission("tpasystem.bypass")) {
            Long cooldownTime = cooldowns.get(player.getUniqueId());
            if (cooldownTime != null && cooldownTime > System.currentTimeMillis()) {
                long remainingSeconds = (cooldownTime - System.currentTimeMillis()) / 1000;
                player.sendMessage(config.formatMessage("tpa.cooldown", "%time%", String.valueOf(remainingSeconds)));
                return false;
            }
        }
        return true;
    }

    public void sendTpaRequest(Player sender, Player target) {
        if (!canSendRequest(sender)) {
            return;
        }

        UUID senderId = sender.getUniqueId();
        UUID targetId = target.getUniqueId();

        if (toggledPlayers.contains(targetId)) {
            sender.sendMessage(config.getMessage("tpa.target-disabled"));
            return;
        }

        if (tpaRequests.containsKey(senderId) || tpaHereRequests.containsKey(senderId)) {
            sender.sendMessage(config.getMessage("tpa.already-pending"));
            return;
        }

        // Cancel any existing request
        cancelRequest(senderId);

        // Send new request
        tpaRequests.put(senderId, targetId);

        // Play sounds
        if (config.areSoundsEnabled()) {
            versionManager.playSound(sender, VersionManager.SoundType.REQUEST);
            versionManager.playSound(target, VersionManager.SoundType.REQUEST);
        }

        // Schedule timeout
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (tpaRequests.remove(senderId) != null) {
                sender.sendMessage(config.getMessage("timeout.sender"));
                target.sendMessage(config.formatMessage("timeout.target", "%player%", sender.getName()));
            }
            requestTasks.remove(senderId);
        }, config.getRequestTimeout() * 20L);

        requestTasks.put(senderId, task);

        // Set cooldown
        if (!sender.hasPermission("tpasystem.bypass")) {
            cooldowns.put(senderId, System.currentTimeMillis() + (config.getRequestCooldown() * 1000L));
        }

        // Send messages
        sender.sendMessage(config.formatMessage("tpa.sent", "%target%", target.getName()));
        target.sendMessage(config.formatMessage("tpa.received", "%player%", sender.getName()));
        target.sendMessage(config.getMessage("tpa.instructions"));
    }

    public void sendTpaHereRequest(Player sender, Player target) {
        if (!canSendRequest(sender)) {
            return;
        }

        UUID senderId = sender.getUniqueId();
        UUID targetId = target.getUniqueId();

        if (toggledPlayers.contains(targetId)) {
            sender.sendMessage(config.getMessage("tpa.target-disabled"));
            return;
        }

        if (tpaRequests.containsKey(senderId) || tpaHereRequests.containsKey(senderId)) {
            sender.sendMessage(config.getMessage("tpa.already-pending"));
            return;
        }

        // Cancel any existing request
        cancelRequest(senderId);

        // Send new request
        tpaHereRequests.put(senderId, targetId);

        // Play sounds
        if (config.areSoundsEnabled()) {
            versionManager.playSound(sender, VersionManager.SoundType.REQUEST);
            versionManager.playSound(target, VersionManager.SoundType.REQUEST);
        }

        // Schedule timeout
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (tpaHereRequests.remove(senderId) != null) {
                sender.sendMessage(config.getMessage("timeout.sender"));
                target.sendMessage(config.formatMessage("timeout.target", "%player%", sender.getName()));
            }
            requestTasks.remove(senderId);
        }, config.getRequestTimeout() * 20L);

        requestTasks.put(senderId, task);

        // Set cooldown
        if (!sender.hasPermission("tpasystem.bypass")) {
            cooldowns.put(senderId, System.currentTimeMillis() + (config.getRequestCooldown() * 1000L));
        }

        // Send messages
        sender.sendMessage(config.formatMessage("tpahere.sent", "%target%", target.getName()));
        target.sendMessage(config.formatMessage("tpahere.received", "%player%", sender.getName()));
        target.sendMessage(config.getMessage("tpa.instructions"));
    }

    public void acceptRequest(Player player) {
        UUID playerId = player.getUniqueId();
        Player requester = null;
        boolean isTpaHere = false;

        // Check for normal TPA requests
        for (Map.Entry<UUID, UUID> entry : tpaRequests.entrySet()) {
            if (entry.getValue().equals(playerId)) {
                requester = Bukkit.getPlayer(entry.getKey());
                break;
            }
        }

        // Check for TPA Here requests
        if (requester == null) {
            for (Map.Entry<UUID, UUID> entry : tpaHereRequests.entrySet()) {
                if (entry.getValue().equals(playerId)) {
                    requester = Bukkit.getPlayer(entry.getKey());
                    isTpaHere = true;
                    break;
                }
            }
        }

        if (requester == null || !requester.isOnline()) {
            player.sendMessage(config.getMessage("accept.no-request"));
            return;
        }

        // Play sounds
        if (config.areSoundsEnabled()) {
            versionManager.playSound(player, VersionManager.SoundType.TELEPORT);
            versionManager.playSound(requester, VersionManager.SoundType.TELEPORT);
        }

        // Perform teleport
        if (isTpaHere) {
            player.teleport(requester.getLocation());
            player.sendMessage(config.formatMessage("accept.success-sender", "%target%", requester.getName()));
            requester.sendMessage(config.formatMessage("accept.success-target", "%player%", player.getName()));
            tpaHereRequests.remove(requester.getUniqueId());
        } else {
            requester.teleport(player.getLocation());
            requester.sendMessage(config.formatMessage("accept.success-sender", "%target%", player.getName()));
            player.sendMessage(config.formatMessage("accept.success-target", "%player%", requester.getName()));
            tpaRequests.remove(requester.getUniqueId());
        }

        // Cancel the timeout task
        BukkitTask task = requestTasks.remove(requester.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void denyRequest(Player player) {
        UUID playerId = player.getUniqueId();
        Player requester = null;

        // Check for normal TPA requests
        for (Map.Entry<UUID, UUID> entry : tpaRequests.entrySet()) {
            if (entry.getValue().equals(playerId)) {
                requester = Bukkit.getPlayer(entry.getKey());
                tpaRequests.remove(entry.getKey());
                break;
            }
        }

        // Check for TPA Here requests
        if (requester == null) {
            for (Map.Entry<UUID, UUID> entry : tpaHereRequests.entrySet()) {
                if (entry.getValue().equals(playerId)) {
                    requester = Bukkit.getPlayer(entry.getKey());
                    tpaHereRequests.remove(entry.getKey());
                    break;
                }
            }
        }

        if (requester == null) {
            player.sendMessage(config.getMessage("deny.no-request"));
            return;
        }

        // Play sounds
        if (config.areSoundsEnabled()) {
            versionManager.playSound(player, VersionManager.SoundType.DENY);
            versionManager.playSound(requester, VersionManager.SoundType.DENY);
        }

        // Send messages
        player.sendMessage(config.getMessage("deny.success-sender"));
        requester.sendMessage(config.formatMessage("deny.success-target", "%player%", player.getName()));

        // Cancel the timeout task
        BukkitTask task = requestTasks.remove(requester.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void toggleRequests(Player player) {
        UUID playerId = player.getUniqueId();
        if (toggledPlayers.contains(playerId)) {
            toggledPlayers.remove(playerId);
            player.sendMessage(config.getMessage("toggle.enabled"));
            if (config.areSoundsEnabled()) {
                versionManager.playSound(player, VersionManager.SoundType.REQUEST);
            }
        } else {
            toggledPlayers.add(playerId);
            player.sendMessage(config.getMessage("toggle.disabled"));
            if (config.areSoundsEnabled()) {
                versionManager.playSound(player, VersionManager.SoundType.DENY);
            }
        }
    }

    public void cancelRequest(UUID playerId) {
        tpaRequests.remove(playerId);
        tpaHereRequests.remove(playerId);
        BukkitTask task = requestTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }

    public void clearAllRequests() {
        tpaRequests.clear();
        tpaHereRequests.clear();
        requestTasks.values().forEach(BukkitTask::cancel);
        requestTasks.clear();
        cooldowns.clear();
    }

    public void reloadConfig(ConfigManager newConfig) {
        this.config = newConfig;
    }
}