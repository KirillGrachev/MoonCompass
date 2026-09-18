package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassCooldownServiceTest {

    @Mock
    private ConfigManager configManager;

    @Mock
    private Player player;

    private CompassCooldownService cooldownService;

    @BeforeEach
    void setUp() {

        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        cooldownService = new CompassCooldownService(configManager);

    }

    @Test
    void freshPlayerIsNotOnCooldown() {

        when(configManager.isCooldownEnabled()).thenReturn(true);

        assertFalse(cooldownService.isOnCooldown(player, false, false));

    }

    @Test
    void playerIsOnCooldownRightAfterUsage() {

        when(configManager.isCooldownEnabled()).thenReturn(true);
        when(configManager.getCooldownMillis()).thenReturn(3000L);

        cooldownService.markUsed(player);

        assertTrue(cooldownService.isOnCooldown(player, false, false));
        assertTrue(cooldownService.getRemainingSeconds(player) > 0);

    }

    @Test
    void bypassPermissionIgnoresCooldown() {

        when(configManager.isCooldownEnabled()).thenReturn(true);

        cooldownService.markUsed(player);

        assertFalse(cooldownService.isOnCooldown(player, true, false));

    }

    @Test
    void commandIsNotLimitedUnlessConfigured() {

        when(configManager.isCooldownEnabled()).thenReturn(true);
        when(configManager.isCooldownAppliedToCommand()).thenReturn(false);

        cooldownService.markUsed(player);

        assertFalse(cooldownService.isOnCooldown(player, false, true));

    }

    @Test
    void disabledCooldownNeverBlocks() {

        when(configManager.isCooldownEnabled()).thenReturn(false);

        cooldownService.markUsed(player);

        assertFalse(cooldownService.isOnCooldown(player, false, false));

    }
}
