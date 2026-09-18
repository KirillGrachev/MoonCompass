package com.ney.mooncompass.permission;

import com.ney.mooncompass.config.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Единая точка проверки прав плагина.
 * Заменяет прежнюю проверку групп LuckPerms — теперь используются обычные permissions.
 */
public class CompassPermissionService {

    private final ConfigManager configManager;

    public CompassPermissionService(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Проверяет наличие права у игрока.
     *
     * @param player     игрок
     * @param permission проверяемое право
     * @return true, если право выдано (или система прав выключена)
     */
    public boolean hasPermission(@NotNull Player player,
                                 @NotNull CompassPermission permission) {
        return hasPermission((CommandSender) player, permission);
    }

    /**
     * Проверяет наличие права у отправителя (игрок или консоль).
     *
     * @param sender     отправитель
     * @param permission проверяемое право
     * @return true, если право выдано (или система прав выключена)
     */
    public boolean hasPermission(@NotNull CommandSender sender,
                                 @NotNull CompassPermission permission) {

        if (!configManager.arePermissionsEnabled()) {
            return true;
        }

        if (configManager.isOpBypass() && (sender.isOp() || !(sender instanceof Player))) {
            return true;
        }

        return sender.hasPermission(resolveNode(permission));

    }

    /**
     * Возвращает строковый узел права из config.yml.
     *
     * @param permission проверяемое право
     * @return узел вида mooncompass.use
     */
    private @NotNull String resolveNode(@NotNull CompassPermission permission) {

        return switch (permission) {

            case USE -> configManager.getPermissionUse();
            case COOLDOWN_BYPASS -> configManager.getPermissionCooldownBypass();
            case RELOAD -> configManager.getPermissionReload();

        };

    }
}
