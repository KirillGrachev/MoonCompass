package com.ney.mooncompass.direction;

import org.jetbrains.annotations.NotNull;

/**
 * Отображаемые названия одного направления из settings.list.
 * Все три значения приходят из config.yml и уже содержат цветовые коды.
 */
public class DirectionName {

    private final String full;
    private final String shortName;
    private final String localizedName;

    public DirectionName(@NotNull String full,
                         @NotNull String shortName,
                         @NotNull String localizedName) {
        this.full = full;
        this.shortName = shortName;
        this.localizedName = localizedName;
    }

    /** Название для плейсхолдера {direction}. */
    public @NotNull String getFull() {
        return full;
    }

    /** Название для плейсхолдера {direction_short}. */
    public @NotNull String getShortName() {
        return shortName;
    }

    /** Название словом на любом языке — плейсхолдер {direction_localized}. */
    public @NotNull String getLocalizedName() {
        return localizedName;
    }
}
