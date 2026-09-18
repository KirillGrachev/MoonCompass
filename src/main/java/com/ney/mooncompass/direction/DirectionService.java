package com.ney.mooncompass.direction;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Определяет направление взгляда игрока по его yaw.
 */
public class DirectionService {

    private final DirectionRegistry directionRegistry;

    public DirectionService(@NotNull DirectionRegistry directionRegistry) {
        this.directionRegistry = directionRegistry;
    }

    /**
     * Определяет направление по локации.
     *
     * @param location локация игрока
     * @return направление взгляда
     */
    public @NotNull CompassDirection resolveDirection(@NotNull Location location) {
        return CompassDirection.fromYaw(location.getYaw());
    }

    /**
     * Возвращает отображаемые имена направления для локации.
     *
     * @param location локация игрока
     * @return имена направления из реестра
     */
    public @NotNull DirectionName resolveDirectionName(@NotNull Location location) {
        return directionRegistry.getDirectionName(resolveDirection(location));
    }

    /**
     * Возвращает нормализованный yaw (0–360) для подстановки в плейсхолдер {yaw}.
     *
     * @param location локация игрока
     * @return yaw в градусах
     */
    public int resolveYaw(@NotNull Location location) {
        return (int) Math.round(CompassDirection.normalize(location.getYaw()));
    }
}
