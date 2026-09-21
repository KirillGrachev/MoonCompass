package com.ney.mooncompass.listener;

import com.ney.mooncompass.MoonCompass;
import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.event.CompassUseEvent;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassItemService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class CompassInteractListener implements Listener {

    private final ConfigManager configManager;
    private final CompassItemService compassItemService;
    private final CompassMessageService compassMessageService;
    private final CompassSenderService compassSenderService;
    private final CompassCooldownService compassCooldownService;
    private final CompassPermissionService compassPermissionService;

    public CompassInteractListener(@NotNull MoonCompass plugin) {

        this.configManager = plugin.getConfigManager();

        this.compassItemService = plugin.getCompassItemService();
        this.compassMessageService = plugin.getCompassMessageService();
        this.compassSenderService = plugin.getCompassSenderService();
        this.compassCooldownService = plugin.getCompassCooldownService();
        this.compassPermissionService = plugin.getCompassPermissionService();

    }

    /**
     * Компас не изменяет мир, поэтому клик читается даже если интеракт
     * отменён другим плагином (регионы, античит, vanish): ignoreCancelled
     * здесь заставил бы молча проглатывать такие клики.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCompassInteract(@NotNull PlayerInteractEvent event) {

        if (!configManager.isEnabled()) return;
        if (!configManager.isClickEnabled()) return;
        if (!isRightClick(event)) return;
        if (!isAllowedHand(event)) return;

        if (!compassItemService.isCompassItem(event.getItem())) {
            return;
        }

        Player player = event.getPlayer();

        new CompassUseEvent(configManager,
                compassMessageService,
                compassSenderService,
                compassCooldownService,
                compassPermissionService
        ).onCompassUse(player, false);

    }

    private boolean isRightClick(@NotNull PlayerInteractEvent event) {

        Action action = event.getAction();

        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;

    }

    private boolean isAllowedHand(@NotNull PlayerInteractEvent event) {

        EquipmentSlot hand = event.getHand();

        return switch (configManager.getClickHand()) {

            case MAIN_HAND -> hand == EquipmentSlot.HAND;

            case OFF_HAND -> hand == EquipmentSlot.OFF_HAND;

            case BOTH -> true;

        };

    }
}
