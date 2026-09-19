package com.ney.mooncompass.service;

import com.ney.mooncompass.config.type.DisplayMode;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Отправляет сообщения игроку в зависимости от settings.output.mode.
 */
public class CompassSenderService {

    /**
     * Метод Player#sendActionBar(String) существует только в Paper 1.20.2+.
     * На Spigot используется ChatMessageType.ACTION_BAR из Bukkit API.
     */
    private static final boolean NATIVE_ACTION_BAR_SUPPORTED = detectNativeActionBar();

    private final CompassMessageService compassMessageService;

    public CompassSenderService(@NotNull CompassMessageService compassMessageService) {
        this.compassMessageService = compassMessageService;
    }

    /**
     * Показывает игроку результат работы компаса.
     *
     * @param player игрок
     */
    public void sendCompassOutput(@NotNull Player player) {

        DisplayMode displayMode = compassMessageService.resolveDisplayMode();

        if (displayMode == null) {
            return;
        }

        switch (displayMode) {

            case CHAT -> sendChat(player, compassMessageService.buildChatOutput(player));

            case ACTION_BAR -> sendActionBar(player,
                    compassMessageService.buildActionBarOutput(player));

            case BOTH -> {
                sendChat(player, compassMessageService.buildChatOutput(player));
                sendActionBar(player, compassMessageService.buildActionBarOutput(player));
            }

        }
    }

    /**
     * Отправляет служебное сообщение игроку (no_permission, disabled и т.д.).
     * Перед отправкой подставляет плейсхолдеры игрока, включая {prefix}.
     *
     * @param player игрок
     * @param lines  сырые строки сообщения из конфига
     */
    public void sendMessage(@NotNull Player player, @Nullable List<String> lines) {

        if (lines == null || lines.isEmpty()) {
            return;
        }

        sendChat(player, compassMessageService.applyPlaceholders(lines, player));

    }

    /**
     * Отправляет служебное сообщение отправителю.
     * Игроку подставляются все плейсхолдеры, консоли — только глобальные.
     *
     * @param sender отправитель
     * @param lines  сырые строки сообщения из конфига
     */
    public void sendMessage(@NotNull CommandSender sender, @Nullable List<String> lines) {

        if (lines == null || lines.isEmpty()) {
            return;
        }

        if (sender instanceof Player player) {

            sendMessage(player, lines);
            return;

        }

        sendChat(sender, compassMessageService.applyGlobalPlaceholders(lines));

    }

    /**
     * Отправляет уже отформатированные строки (плейсхолдеры подставлены заранее).
     *
     * @param player игрок
     * @param lines  готовые строки сообщения
     */
    public void sendFormatted(@NotNull Player player, @Nullable List<String> lines) {
        sendChat(player, lines);
    }

    private void sendChat(@NotNull CommandSender sender, @Nullable List<String> lines) {

        if (lines == null || lines.isEmpty()) {
            return;
        }

        lines.forEach(sender::sendMessage);

    }

    /**
     * Legacy-компоненты намеренно используются как запасной путь для Spigot.
     */
    @SuppressWarnings("deprecation")
    private void sendActionBar(@NotNull Player player, @Nullable String text) {

        if (text == null || text.isEmpty()) {
            return;
        }

        if (NATIVE_ACTION_BAR_SUPPORTED) {
            player.sendActionBar(text);
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(text));

    }

    private static boolean detectNativeActionBar() {

        try {

            Player.class.getMethod("sendActionBar", String.class);
            return true;

        } catch (NoSuchMethodException e) {
            return false;
        }

    }
}
