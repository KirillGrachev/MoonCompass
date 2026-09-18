package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.config.type.DisplayMode;
import com.ney.mooncompass.direction.DirectionName;
import com.ney.mooncompass.direction.DirectionService;
import com.ney.mooncompass.util.HexColorUtil;
import com.ney.mooncompass.util.PlaceholderUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Собирает текст компаса и служебные сообщения из config.yml.
 */
public class CompassMessageService {

    private final ConfigManager configManager;
    private final DirectionService directionService;

    public CompassMessageService(@NotNull ConfigManager configManager,
                                 @NotNull DirectionService directionService) {
        this.configManager = configManager;
        this.directionService = directionService;
    }

    /**
     * Собирает строки компаса для чата с подстановкой плейсхолдеров.
     *
     * @param player игрок
     * @return готовые к отправке строки (пустой список, если вывод в чат выключен)
     */
    public @NotNull List<String> buildChatOutput(@NotNull Player player) {

        if (!configManager.areChatMessagesEnabled()) {
            return Collections.emptyList();
        }

        return applyPlaceholders(configManager.getChatLines(), player);

    }

    /**
     * Собирает строку для action bar с подстановкой плейсхолдеров.
     *
     * @param player игрок
     * @return строка action bar или null, если вывод туда выключен
     */
    public String buildActionBarOutput(@NotNull Player player) {

        if (!configManager.areActionBarMessagesEnabled()) {
            return null;
        }

        List<String> lines = applyPlaceholders(configManager.getActionBarLines(), player);

        if (lines.isEmpty()) {
            return null;
        }

        return String.join("\n", lines);

    }

    /**
     * Подставляет плейсхолдеры игрока в каждую строку.
     *
     * @param lines  строки из конфига
     * @param player игрок
     * @return строки с подставленными значениями
     */
    private @NotNull List<String> applyPlaceholders(@NotNull List<String> lines,
                                                    @NotNull Player player) {

        Map<String, String> placeholders = buildPlaceholders(player);

        return lines.stream()
                .map(line -> HexColorUtil.color(PlaceholderUtil.apply(line, placeholders)))
                .collect(Collectors.toList());

    }

    /**
     * Подставляет плейсхолдеры в строки сообщения о задержке.
     * Дополнительно доступен плейсхолдер {seconds}.
     *
     * @param lines           строки из конфига
     * @param player          игрок
     * @param remainingSeconds остаток задержки в секундах
     * @return готовые к отправке строки
     */
    public @NotNull List<String> applyCooldownPlaceholders(@NotNull List<String> lines,
                                                           @NotNull Player player,
                                                           long remainingSeconds) {

        Map<String, String> placeholders = buildPlaceholders(player);
        placeholders.put("seconds", String.valueOf(remainingSeconds));

        return lines.stream()
                .map(line -> HexColorUtil.color(PlaceholderUtil.apply(line, placeholders)))
                .collect(Collectors.toList());

    }

    /**
     * Возвращает значение плейсхолдера по его имени (без фигурных скобок).
     *
     * @param player        игрок
     * @param placeholder   имя плейсхолдера, например "direction"
     * @return значение или пустая строка, если плейсхолдер неизвестен
     */
    public @NotNull String resolvePlaceholder(@NotNull Player player,
                                              @NotNull String placeholder) {
        return buildPlaceholders(player).getOrDefault(placeholder, "");
    }

    private @NotNull Map<String, String> buildPlaceholders(@NotNull Player player) {

        Location location = player.getLocation();
        DirectionName directionName = directionService.resolveDirectionName(location);

        Map<String, String> placeholders = PlaceholderUtil.newPlaceholders();

        placeholders.put("prefix", configManager.getPrefix());
        placeholders.put("player", player.getName());
        placeholders.put("world", location.getWorld() == null ? "unknown" : location.getWorld().getName());
        placeholders.put("x", String.valueOf(location.getBlockX()));
        placeholders.put("y", String.valueOf(location.getBlockY()));
        placeholders.put("z", String.valueOf(location.getBlockZ()));
        placeholders.put("yaw", String.valueOf(directionService.resolveYaw(location)));
        placeholders.put("pitch", String.valueOf(Math.round(location.getPitch())));
        placeholders.put("direction", directionName.getFull());
        placeholders.put("direction_short", directionName.getShortName());
        placeholders.put("direction_localized", directionName.getLocalizedName());

        return placeholders;

    }

    /**
     * Определяет, куда именно нужно отправить результат компаса.
     * Учитывает settings.output.mode и включённость конкретных блоков сообщений.
     *
     * @return CHAT, ACTION_BAR, BOTH или null, если выводить некуда
     */
    public DisplayMode resolveDisplayMode() {

        DisplayMode displayMode = configManager.getDisplayMode();

        boolean chatAvailable = displayMode != DisplayMode.ACTION_BAR
                && configManager.areChatMessagesEnabled();
        boolean actionBarAvailable = displayMode != DisplayMode.CHAT
                && configManager.areActionBarMessagesEnabled();

        if (chatAvailable && actionBarAvailable) {
            return DisplayMode.BOTH;
        }

        if (chatAvailable) {
            return DisplayMode.CHAT;
        }

        return actionBarAvailable ? DisplayMode.ACTION_BAR : null;

    }
}
