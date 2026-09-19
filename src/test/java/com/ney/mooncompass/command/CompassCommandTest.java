package com.ney.mooncompass.command;

import com.ney.mooncompass.MoonCompass;
import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.permission.CompassPermission;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassCommandTest {

    private static final String PERMISSION_RELOAD = "mooncompass.reload";
    private static final List<String> MESSAGE = List.of("строка");

    @Mock
    private MoonCompass plugin;

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
    private CommandSender sender;

    @Mock
    private Command command;

    @Mock
    private Player player;

    private CompassCommand compassCommand;

    @BeforeEach
    void setUp() {

        compassCommand = new CompassCommand(plugin,
                configManager,
                messageService,
                senderService,
                cooldownService,
                permissionService);

    }

    @Test
    void filterKeepsOnlyMatchingPrefixes() {

        assertEquals(List.of("reload"), CompassCommand.filter(List.of("reload"), "re"));
        assertEquals(List.of("reload"), CompassCommand.filter(List.of("reload"), "RELOAD"));
        assertTrue(CompassCommand.filter(List.of("reload"), "xyz").isEmpty());

    }

    @Test
    void consoleReceivesOnlyPlayerMessage() {

        when(configManager.getOnlyPlayerMessage()).thenReturn(MESSAGE);
        when(messageService.applyGlobalPlaceholders(MESSAGE)).thenReturn(MESSAGE);

        compassCommand.onCommand(sender, command, "compass", new String[0]);

        verify(sender).sendMessage("строка");

    }

    @Test
    void unknownSubCommandSendsUsage() {

        when(configManager.getUsageMessage()).thenReturn(MESSAGE);
        when(messageService.applyGlobalPlaceholders(MESSAGE)).thenReturn(MESSAGE);

        compassCommand.onCommand(sender, command, "compass", new String[]{"help"});

        verify(sender).sendMessage("строка");

    }

    @Test
    void reloadWithoutPermissionSendsNoPermissionMessage() {

        when(permissionService.hasPermission(sender, CompassPermission.RELOAD)).thenReturn(false);
        when(configManager.getNoPermissionMessage()).thenReturn(MESSAGE);
        when(messageService.applyGlobalPlaceholders(MESSAGE)).thenReturn(MESSAGE);

        compassCommand.onCommand(sender, command, "compass", new String[]{"reload"});

        verify(sender).sendMessage("строка");
        verify(plugin, org.mockito.Mockito.never()).reloadPlugin();

    }

    @Test
    void reloadWithPermissionReloadsPlugin() {

        when(permissionService.hasPermission(sender, CompassPermission.RELOAD)).thenReturn(true);
        when(configManager.getReloadedMessage()).thenReturn(MESSAGE);
        when(messageService.applyGlobalPlaceholders(MESSAGE)).thenReturn(MESSAGE);

        compassCommand.onCommand(sender, command, "compass", new String[]{"reload"});

        verify(plugin).reloadPlugin();
        verify(sender).sendMessage("строка");

    }

    @Test
    void tabCompleteSuggestsReloadOnlyWithPermission() {

        when(configManager.getPermissionReload()).thenReturn(PERMISSION_RELOAD);

        when(sender.hasPermission(PERMISSION_RELOAD)).thenReturn(false);
        assertTrue(compassCommand.onTabComplete(sender, command, "compass", new String[]{""}).isEmpty());

        when(sender.hasPermission(PERMISSION_RELOAD)).thenReturn(true);
        assertEquals(List.of("reload"),
                compassCommand.onTabComplete(sender, command, "compass", new String[]{"rel"}));

    }

    @Test
    void playerWithoutArgsUsesCompass() {

        when(configManager.isEnabled()).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.USE)).thenReturn(true);
        when(permissionService.hasPermission(player, CompassPermission.COOLDOWN_BYPASS)).thenReturn(false);
        when(cooldownService.isOnCooldown(player, false, true)).thenReturn(false);

        compassCommand.onCommand(player, command, "compass", new String[0]);

        verify(cooldownService).markUsed(player);
        verify(senderService).sendCompassOutput(player);

    }
}
