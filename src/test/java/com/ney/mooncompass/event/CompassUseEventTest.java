package com.ney.mooncompass.event;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.permission.CompassPermission;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassUseEventTest {

    private static final List<String> MESSAGE = List.of("сообщение");

    @Mock
    private ConfigManager configManager;

    @Mock
    private CompassMessageService messageService;

    @Mock
    private CompassSenderService senderService;

    @Mock
    private CompassCooldownService cooldownService;

    @Mock
    private CompassPermissionService permissionService;

    @Mock
    private Player player;

    private CompassUseEvent compassUseEvent;

    @BeforeEach
    void setUp() {

        compassUseEvent = new CompassUseEvent(configManager,
                messageService,
                senderService,
                cooldownService,
                permissionService);

    }

    @Test
    void disabledPluginSendsDisabledMessage() {

        when(configManager.isEnabled()).thenReturn(false);
        when(configManager.getDisabledMessage()).thenReturn(MESSAGE);

        compassUseEvent.onCompassUse(player, false);

        verify(senderService).sendMessage(player, MESSAGE);
        verify(permissionService, never()).hasPermission(player, CompassPermission.USE);

    }

    @Test
    void missingPermissionSendsNoPermissionMessage() {

        when(configManager.isEnabled()).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.USE)).thenReturn(false);
        when(configManager.getNoPermissionMessage()).thenReturn(MESSAGE);

        compassUseEvent.onCompassUse(player, false);

        verify(senderService).sendMessage(player, MESSAGE);
        verify(cooldownService, never()).markUsed(player);

    }

    @Test
    void cooldownBlocksOutputAndShowsRemainingSeconds() {

        when(configManager.isEnabled()).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.USE)).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.COOLDOWN_BYPASS)).thenReturn(false);
        when(cooldownService.isOnCooldown(player, false, true)).thenReturn(true);
        when(cooldownService.getRemainingSeconds(player)).thenReturn(2L);
        when(configManager.getCooldownMessage()).thenReturn(MESSAGE);
        when(messageService.applyCooldownPlaceholders(MESSAGE, player, 2L)).thenReturn(MESSAGE);

        compassUseEvent.onCompassUse(player, true);

        verify(senderService).sendFormatted(player, MESSAGE);
        verify(cooldownService, never()).markUsed(player);
        verify(senderService, never()).sendCompassOutput(player);

    }

    @Test
    void successfulUseMarksCooldownAndSendsOutput() {

        when(configManager.isEnabled()).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.USE)).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.COOLDOWN_BYPASS)).thenReturn(false);
        when(cooldownService.isOnCooldown(player, false, false)).thenReturn(false);

        compassUseEvent.onCompassUse(player, false);

        verify(cooldownService).markUsed(player);
        verify(senderService).sendCompassOutput(player);
        verify(senderService, never()).sendMessage(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList());

    }
}
