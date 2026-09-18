package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.util.HexColorUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassItemServiceTest {

    private static final String ITEM_NAME = "&b&lMoon Compass";

    @Mock
    private ConfigManager configManager;

    @Mock
    private ItemStack itemStack;

    @Mock
    private ItemMeta itemMeta;

    private CompassItemService compassItemService;

    @BeforeEach
    void setUp() {
        compassItemService = new CompassItemService(configManager);
    }

    @Test
    void disabledClickRejectsEverything() {

        when(configManager.isClickEnabled()).thenReturn(false);

        assertFalse(compassItemService.isCompassItem(itemStack));

    }

    @Test
    void nullAndAirAreRejected() {

        when(configManager.isClickEnabled()).thenReturn(true);
        assertFalse(compassItemService.isCompassItem(null));

        when(itemStack.getType()).thenReturn(Material.AIR);
        assertFalse(compassItemService.isCompassItem(itemStack));

    }

    @Test
    void wrongMaterialIsRejected() {

        when(configManager.isClickEnabled()).thenReturn(true);
        when(itemStack.getType()).thenReturn(Material.CLOCK);
        when(configManager.getClickItem()).thenReturn(Material.COMPASS);

        assertFalse(compassItemService.isCompassItem(itemStack));

    }

    @Test
    void configuredMaterialPassesWithoutNameCheck() {

        when(configManager.isClickEnabled()).thenReturn(true);
        when(itemStack.getType()).thenReturn(Material.COMPASS);
        when(configManager.getClickItem()).thenReturn(Material.COMPASS);
        when(configManager.isNameRequired()).thenReturn(false);

        assertTrue(compassItemService.isCompassItem(itemStack));

    }

    @Test
    void nameCheckRequiresMatchingDisplayName() {

        when(configManager.isClickEnabled()).thenReturn(true);
        when(itemStack.getType()).thenReturn(Material.COMPASS);
        when(configManager.getClickItem()).thenReturn(Material.COMPASS);
        when(configManager.isNameRequired()).thenReturn(true);
        when(configManager.getRequiredItemName()).thenReturn(HexColorUtil.color(ITEM_NAME));

        when(itemStack.getItemMeta()).thenReturn(null);
        assertFalse(compassItemService.isCompassItem(itemStack));

        when(itemStack.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(false);
        assertFalse(compassItemService.isCompassItem(itemStack));

        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ITEM_NAME);
        assertTrue(compassItemService.isCompassItem(itemStack));

    }
}
