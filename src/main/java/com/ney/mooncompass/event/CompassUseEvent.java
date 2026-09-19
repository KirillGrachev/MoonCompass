package com.ney.mooncompass.event;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.permission.CompassPermission;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Логика использования компаса, общая для клика и для команды.
 */
public class CompassUseEvent {

    private final ConfigManager configManager;
    private final CompassMessageService messageService;
    private final CompassSenderService senderService;
    private final CompassCooldownService cooldownService;
    private final CompassPermissionService permissionService;

    public CompassUseEvent(@NotNull ConfigManager configManager,
                           @NotNull CompassMessageService messageService,
                           @NotNull CompassSenderService senderService,
                           @NotNull CompassCooldownService cooldownService,
                           @NotNull CompassPermissionService permissionService) {
        this.configManager = configManager;
        this.messageService = messageService;
        this.senderService = senderService;
        this.cooldownService = cooldownService;
        this.permissionService = permissionService;
    }

    /**
     * Обрабатывает попытку игрока использовать компас.
     *
     * @param player      игрок
     * @param fromCommand true, если компас вызван командой /compass
     */
    public void onCompassUse(@NotNull Player player, boolean fromCommand) {

        if (!configManager.isEnabled()) {
            senderService.sendMessage(player, configManager.getDisabledMessage());
            return;
        }

        if (!permissionService.hasPermission(player, CompassPermission.USE)) {
            senderService.sendMessage(player, configManager.getNoPermissionMessage());
            return;
        }

        boolean hasBypass = permissionService.hasPermission(player, CompassPermission.COOLDOWN_BYPASS);

        if (cooldownService.isOnCooldown(player, hasBypass, fromCommand)) {
            senderService.sendFormatted(player, messageService.applyCooldownPlaceholders(
                    configManager.getCooldownMessage(), player,
                    cooldownService.getRemainingSeconds(player)));
            return;
        }

        cooldownService.markUsed(player);
        senderService.sendCompassOutput(player);

    }
}
