package com.ney.mooncompass.direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Восемь направлений взгляда игрока.
 * Порядок значений образует сектора по 45°, начиная с севера.
 * Отображаемые названия направлений хранятся в config.yml (settings.list).
 */
public enum CompassDirection {

    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    /** Размер одного сектора в градусах. */
    private static final double SECTOR_SIZE = 360.0 / values().length;

    /** Половина сектора — сдвиг, при котором направление меняется. */
    private static final double SECTOR_OFFSET = SECTOR_SIZE / 2.0;

    /**
     * Определяет направление по углу поворота игрока (yaw).
     *
     * @param yaw угол поворота из Location#getYaw()
     * @return направление, в котором смотрит игрок
     */
    public static @NotNull CompassDirection fromYaw(float yaw) {

        CompassDirection[] directions = values();
        double rotation = normalize(yaw);
        int index = (int) Math.floor((rotation + SECTOR_OFFSET) / SECTOR_SIZE);

        return directions[index % directions.length];

    }

    /**
     * Приводит произвольный угол к диапазону [0; 360).
     *
     * @param degrees угол в градусах
     * @return нормализованный угол
     */
    public static double normalize(double degrees) {

        double rotation = degrees % 360.0;

        if (rotation < 0) {
            rotation += 360.0;
        }

        return rotation;

    }

    /**
     * Ключ направления в config.yml (settings.list).
     *
     * @return ключ в нижнем регистре, например north_east
     */
    public @NotNull String getConfigKey() {
        return name().toLowerCase();
    }

    /**
     * Находит направление по ключу из config.yml (settings.list).
     *
     * @param key ключ направления, например north_east
     * @return направление или null, если ключ неизвестен
     */
    public static @Nullable CompassDirection fromConfigKey(@NotNull String key) {

        return switch (key.toLowerCase(Locale.ROOT)) {

            case "north" -> NORTH;
            case "north_east" -> NORTH_EAST;
            case "east" -> EAST;
            case "south_east" -> SOUTH_EAST;
            case "south" -> SOUTH;
            case "south_west" -> SOUTH_WEST;
            case "west" -> WEST;
            case "north_west" -> NORTH_WEST;

            default -> null;

        };

    }
}
