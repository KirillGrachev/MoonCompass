package com.ney.mooncompass.direction;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.util.HexColorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реестр отображаемых названий направлений.
 * Хранит имена из config.yml (settings.list) и подставляет их при выводе компаса.
 */
public class DirectionRegistry {

    private static final Logger LOGGER = Logger.getLogger("MoonCompass");

    private final ConfigManager configManager;
    private final Map<CompassDirection, DirectionName> registeredDirections = new ConcurrentHashMap<>();

    public DirectionRegistry(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
        initializeRegisteredDirections();
    }

    private void initializeRegisteredDirections() {

        Map<CompassDirection, String> fullNames = resolveOverrides(configManager.getDirectionNames());
        Map<CompassDirection, String> shortNames = resolveOverrides(configManager.getDirectionShortNames());
        Map<CompassDirection, String> localizedNames = resolveOverrides(configManager.getDirectionLocalizedNames());

        for (CompassDirection direction : CompassDirection.values()) {

            String configKey = direction.getConfigKey();

            registeredDirections.put(direction, new DirectionName(
                    HexColorUtil.color(fullNames.getOrDefault(direction, configKey)),
                    HexColorUtil.color(shortNames.getOrDefault(direction, configKey)),
                    HexColorUtil.color(localizedNames.getOrDefault(direction, configKey))
            ));

        }

    }

    /**
     * Приводит строковые ключи конфига к направлениям.
     * Неизвестные ключи отбрасываются с предупреждением в консоль.
     *
     * @param configuredNames секция settings.list.* из config.yml
     * @return карта "направление -> имя"
     */
    private @NotNull Map<CompassDirection, String> resolveOverrides(@NotNull Map<String, String> configuredNames) {

        Map<CompassDirection, String> overrides = new EnumMap<>(CompassDirection.class);

        configuredNames.forEach((key, name) -> {

            CompassDirection direction = CompassDirection.fromConfigKey(key);

            if (direction == null) {

                LOGGER.warning("Неизвестный ключ направления в config.yml: '" + key + "'");
                return;

            }

            overrides.put(direction, name);

        });

        return overrides;

    }

    /**
     * Возвращает отображаемые имена направления.
     * Если реестр ещё не заполнен — имена заменяются ключом направления (north, north_east, ...).
     *
     * @param direction направление взгляда игрока
     * @return имена направления из реестра
     */
    public @NotNull DirectionName getDirectionName(@NotNull CompassDirection direction) {

        DirectionName directionName = registeredDirections.get(direction);

        if (directionName != null) {
            return directionName;
        }

        String configKey = direction.getConfigKey();
        return new DirectionName(configKey, configKey, configKey);

    }

    public void clearRegisteredDirections() {
        registeredDirections.clear();
    }

    public void reloadRegistry() {
        clearRegisteredDirections();
        initializeRegisteredDirections();
    }
}
