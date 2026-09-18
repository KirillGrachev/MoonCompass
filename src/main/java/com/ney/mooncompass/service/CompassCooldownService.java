package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранит время последнего использования компаса каждым игроком.
 */
public class CompassCooldownService {

    private final ConfigManager configManager;
    private final Map<UUID, Long> lastUsage = new ConcurrentHashMap<>();

    public CompassCooldownService(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Проверяет, находится ли игрок на задержке.
     *
     * @param player     игрок
     * @param hasBypass  обходит ли игрок задержку (permission)
     * @param fromCommand вызвано ли использование командой
     * @return true, если использовать компас ещё нельзя
     */
    public boolean isOnCooldown(@NotNull Player player,
                                boolean hasBypass,
                                boolean fromCommand) {

        if (!configManager.isCooldownEnabled()) return false;
        if (hasBypass) return false;
        if (fromCommand && !configManager.isCooldownAppliedToCommand()) return false;

        return getRemainingSeconds(player) > 0;

    }

    /**
     * Возвращает остаток задержки в секундах (0 — если задержки нет).
     *
     * @param player игрок
     * @return секунды до возможности использовать компас
     */
    public long getRemainingSeconds(@NotNull Player player) {

        Long lastUsedAt = lastUsage.get(player.getUniqueId());

        if (lastUsedAt == null) {
            return 0L;
        }

        long remaining = configManager.getCooldownMillis() - (System.currentTimeMillis() - lastUsedAt);

        return remaining <= 0L ? 0L : (long) Math.ceil(remaining / 1000.0);

    }

    /**
     * Фиксирует текущее время как момент использования компаса.
     *
     * @param player игрок
     */
    public void markUsed(@NotNull Player player) {
        lastUsage.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
