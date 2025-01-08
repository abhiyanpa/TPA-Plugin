package me.errcruze.tPA.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VersionManager {
    private final int majorVersion;
    private final boolean legacy;

    public VersionManager() {
        String version = Bukkit.getServer().getBukkitVersion();
        String[] parts = version.split("\\.");
        majorVersion = Integer.parseInt(parts[1].split("-")[0]);
        legacy = majorVersion <= 12;
    }

    public void playSound(Player player, SoundType soundType) {
        try {
            if (legacy) {
                playLegacySound(player, soundType);
            } else {
                playModernSound(player, soundType);
            }
        } catch (Exception e) {
            // Silently fail if sound doesn't exist in this version
        }
    }

    private void playLegacySound(Player player, SoundType soundType) {
        switch (soundType) {
            case TELEPORT:
                if (majorVersion <= 8) {
                    player.playSound(player.getLocation(), "ENDERMAN_TELEPORT", 1, 1);
                } else {
                    player.playSound(player.getLocation(), "ENTITY_ENDERMAN_TELEPORT", 1, 1);
                }
                break;
            case REQUEST:
                if (majorVersion <= 8) {
                    player.playSound(player.getLocation(), "ORB_PICKUP", 1, 1);
                } else {
                    player.playSound(player.getLocation(), "ENTITY_EXPERIENCE_ORB_PICKUP", 1, 1);
                }
                break;
            case DENY:
                if (majorVersion <= 8) {
                    player.playSound(player.getLocation(), "ANVIL_BREAK", 1, 1);
                } else {
                    player.playSound(player.getLocation(), "BLOCK_ANVIL_BREAK", 1, 1);
                }
                break;
        }
    }

    private void playModernSound(Player player, SoundType soundType) {
        switch (soundType) {
            case TELEPORT:
                player.playSound(player.getLocation(), "ENTITY_ENDERMAN_TELEPORT", 1, 1);
                break;
            case REQUEST:
                player.playSound(player.getLocation(), "ENTITY_EXPERIENCE_ORB_PICKUP", 1, 1);
                break;
            case DENY:
                player.playSound(player.getLocation(), "BLOCK_ANVIL_BREAK", 1, 1);
                break;
        }
    }

    public enum SoundType {
        TELEPORT,
        REQUEST,
        DENY
    }
}