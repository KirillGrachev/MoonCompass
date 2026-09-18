package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.config.type.DisplayMode;
import com.ney.mooncompass.direction.DirectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompassMessageServiceTest {

    @Mock
    private ConfigManager configManager;

    @Mock
    private DirectionService directionService;

    private CompassMessageService compassMessageService;

    @BeforeEach
    void setUp() {
        compassMessageService = new CompassMessageService(configManager, directionService);
    }

    @Test
    void chatModeUsesChatOnly() {

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.CHAT);
        when(configManager.areChatMessagesEnabled()).thenReturn(true);

        assertEquals(DisplayMode.CHAT, compassMessageService.resolveDisplayMode());

    }

    @Test
    void bothModeUsesBothChannels() {

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.BOTH);
        when(configManager.areChatMessagesEnabled()).thenReturn(true);
        when(configManager.areActionBarMessagesEnabled()).thenReturn(true);

        assertEquals(DisplayMode.BOTH, compassMessageService.resolveDisplayMode());

    }

    @Test
    void bothModeFallsBackToActionBarWhenChatDisabled() {

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.BOTH);
        when(configManager.areChatMessagesEnabled()).thenReturn(false);
        when(configManager.areActionBarMessagesEnabled()).thenReturn(true);

        assertEquals(DisplayMode.ACTION_BAR, compassMessageService.resolveDisplayMode());

    }

    @Test
    void actionBarModeUsesActionBarOnly() {

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.ACTION_BAR);
        when(configManager.areActionBarMessagesEnabled()).thenReturn(true);

        assertEquals(DisplayMode.ACTION_BAR, compassMessageService.resolveDisplayMode());

    }

    @Test
    void disabledSelectedChannelResolvesNull() {

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.ACTION_BAR);
        when(configManager.areActionBarMessagesEnabled()).thenReturn(false);

        assertNull(compassMessageService.resolveDisplayMode());

        when(configManager.getDisplayMode()).thenReturn(DisplayMode.CHAT);
        when(configManager.areChatMessagesEnabled()).thenReturn(false);

        assertNull(compassMessageService.resolveDisplayMode());

    }
}
