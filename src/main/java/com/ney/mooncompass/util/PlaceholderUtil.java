package com.ney.mooncompass.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Утилита для подстановки плейсхолдеров вида {x}, {direction} и т.д.
 */
public class PlaceholderUtil {

    private PlaceholderUtil() {
    }

    /**
     * Заменяет все плейсхолдеры в строке на их значения.
     *
     * @param text         исходная строка
     * @param placeholders карта "плейсхолдер (без скобок) -> значение"
     * @return строка с подставленными значениями
     */
    public static @NotNull String apply(@Nullable String text,
                                        @NotNull Map<String, String> placeholders) {

        if (text == null || text.isEmpty()) {
            return "";
        }

        String result = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {

            if (entry.getValue() == null) {
                continue;
            }

            result = result.replace("{" + entry.getKey() + "}", entry.getValue());

        }

        return result;

    }

    /**
     * Создаёт пустую карту плейсхолдеров с сохранением порядка подстановки.
     *
     * @return новая изменяемая карта
     */
    public static @NotNull Map<String, String> newPlaceholders() {
        return new LinkedHashMap<>();
    }
}
