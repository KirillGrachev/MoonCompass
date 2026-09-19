package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.direction.DirectionName;
import com.ney.mooncompass.direction.DirectionService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassMessageServicePlaceholderTest {

    @Mock
    private ConfigManager configManager;

    @Mock
    private DirectionService directionService;

    @Mock
    private Player player;

    @Mock
    private Location location;

    @Mock
    private World world;

    private CompassMessageService compassMessageService;

    @BeforeEach
    void setUp() {

        compassMessageService = new CompassMessageService(configManager, directionService);

    }

    @Test
    void playerPlaceholdersAreReplaced() {

        stubPlayerPlaceholders();
        when(configManager.getPrefix()).thenReturn("&aP> ");

        List<String> formatted = compassMessageService.applyPlaceholders(
                List.of("{prefix}{player} {x}/{y}/{z} {world} {direction} {direction_short} {direction_localized} {yaw}"),
                player);

        assertEquals("§aP> Ney 7/64/100 world N N north 90", formatted.get(0));

    }

    @Test
    void globalPlaceholdersWorkWithoutPlayerData() {

        when(configManager.getPrefix()).thenReturn("&aP> ");

        List<String> formatted = compassMessageService.applyGlobalPlaceholders(
                List.of("{prefix}text {x}"));

        assertEquals("§aP> text {x}", formatted.get(0));

    }

    @Test
    void cooldownPlaceholderIsAdded() {

        stubPlayerPlaceholders();
        when(configManager.getPrefix()).thenReturn("");

        List<String> formatted = compassMessageService.applyCooldownPlaceholders(
                List.of("wait {seconds} sec"), player, 5L);

        assertEquals("wait 5 sec", formatted.get(0));

    }

    private void stubPlayerPlaceholders() {

        when(player.getName()).thenReturn("Ney");
        when(player.getLocation()).thenReturn(location);
        when(location.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");
        when(location.getBlockX()).thenReturn(7);
        when(location.getBlockY()).thenReturn(64);
        when(location.getBlockZ()).thenReturn(100);
        when(location.getPitch()).thenReturn(0F);
        when(directionService.resolveDirectionName(location))
                .thenReturn(new DirectionName("N", "N", "north"));
        when(directionService.resolveYaw(location)).thenReturn(90);

    }
}
