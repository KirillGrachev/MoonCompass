package com.ney.mooncompass.service;

import org.bukkit.command.CommandSender;
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
class CompassSenderServiceTest {

    private static final List<String> RAW = List.of("{prefix}message");
    private static final List<String> FORMATTED = List.of("message");

    @Mock
    private CompassMessageService compassMessageService;

    @Mock
    private Player player;

    @Mock
    private CommandSender console;

    private CompassSenderService compassSenderService;

    @BeforeEach
    void setUp() {
        compassSenderService = new CompassSenderService(compassMessageService);
    }

    @Test
    void playerMessagesAreFormattedWithPlayerPlaceholders() {

        when(compassMessageService.applyPlaceholders(RAW, player)).thenReturn(FORMATTED);

        compassSenderService.sendMessage(player, RAW);

        verify(player).sendMessage("message");

    }

    @Test
    void consoleMessagesAreFormattedWithGlobalPlaceholders() {

        when(compassMessageService.applyGlobalPlaceholders(RAW)).thenReturn(FORMATTED);

        compassSenderService.sendMessage(console, RAW);

        verify(console).sendMessage("message");

    }

    @Test
    void preFormattedLinesAreSentAsIs() {

        compassSenderService.sendFormatted(player, FORMATTED);

        verify(player).sendMessage("message");
        verify(compassMessageService, never()).applyPlaceholders(FORMATTED, player);

    }

    @Test
    void emptyAndNullMessagesAreSkipped() {

        compassSenderService.sendMessage(player, null);
        compassSenderService.sendMessage(player, List.of());
        compassSenderService.sendMessage(console, null);

        verify(player, never()).sendMessage(org.mockito.ArgumentMatchers.anyString());
        verify(console, never()).sendMessage(org.mockito.ArgumentMatchers.anyString());

    }
}
