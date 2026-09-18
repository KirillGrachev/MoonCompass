package com.ney.mooncompass.direction;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.util.HexColorUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectionRegistryTest {

    @Mock
    private ConfigManager configManager;

    @Test
    void configuredNamesAreColoredAndResolved() {

        stubConfig();

        DirectionRegistry registry = new DirectionRegistry(configManager);
        DirectionName north = registry.getDirectionName(CompassDirection.NORTH);

        assertEquals(HexColorUtil.color("&aСевер"), north.getFull());
        assertEquals("N", north.getShortName());
        assertEquals("север", north.getLocalizedName());

    }

    @Test
    void missingKeysFallBackToConfigKey() {

        stubConfig();

        DirectionRegistry registry = new DirectionRegistry(configManager);
        DirectionName northEast = registry.getDirectionName(CompassDirection.NORTH_EAST);

        assertEquals("north_east", northEast.getFull());
        assertEquals("north_east", northEast.getShortName());
        assertEquals("north_east", northEast.getLocalizedName());

    }

    @Test
    void unknownConfigKeysAreIgnored() {

        stubConfig();

        DirectionRegistry registry = new DirectionRegistry(configManager);

        assertEquals("north_east", registry.getDirectionName(CompassDirection.NORTH_EAST).getFull());

    }

    @Test
    void reloadRegistryRepopulatesNames() {

        stubConfig();

        DirectionRegistry registry = new DirectionRegistry(configManager);
        registry.clearRegisteredDirections();

        assertEquals("north", registry.getDirectionName(CompassDirection.NORTH).getFull());

        registry.reloadRegistry();

        assertEquals(HexColorUtil.color("&aСевер"), registry.getDirectionName(CompassDirection.NORTH).getFull());

    }

    private void stubConfig() {

        when(configManager.getDirectionNames()).thenReturn(Map.of(
                "north", "&aСевер",
                "unknown_key", "x"
        ));
        when(configManager.getDirectionShortNames()).thenReturn(Map.of("north", "N"));
        when(configManager.getDirectionLocalizedNames()).thenReturn(Map.of("north", "север"));

    }
}
