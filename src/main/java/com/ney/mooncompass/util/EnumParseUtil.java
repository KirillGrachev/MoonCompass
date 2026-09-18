package com.ney.mooncompass.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Утилита для безопасного разбора значений из config.yml.
 */
public class EnumParseUtil {

    private EnumParseUtil() {
    }

    /**
     * Разбирает строку в значение enum, игнорируя регистр и пробелы.
     *
     * @param value        строка из конфига
     * @param type         класс перечисления
     * @param defaultValue значение по умолчанию при ошибке разбора
     * @param <T>          тип перечисления
     * @return значение перечисления
     */
    public static <T extends Enum<T>> @NotNull T parse(@Nullable String value,
                                                       @NotNull Class<T> type,
                                                       @NotNull T defaultValue) {

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }

    }

    /**
     * Разбирает строку в Material, игнорируя регистр и пробелы.
     *
     * @param value        строка из конфига
     * @param defaultValue материал по умолчанию при ошибке разбора
     * @return материал
     */
    public static @NotNull Material parseMaterial(@Nullable String value,
                                                  @NotNull Material defaultValue) {

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        Material material = Material.matchMaterial(value.trim());

        return material == null ? defaultValue : material;

    }
}
