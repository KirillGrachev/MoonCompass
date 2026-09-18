package com.ney.mooncompass.placeholder;

import com.ney.mooncompass.service.CompassMessageService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Экспансия PlaceholderAPI.
 * Регистрируется только если PlaceholderAPI установлен (softdepend).
 * Позволяет другим плагинам (TAB, скорборды, голограммы, чат-плагины)
 * подставлять значения компаса в собственные строки.
 *
 * Доступные плейсхолдеры:
 * %mooncompass_x%, %mooncompass_y%, %mooncompass_z%,
 * %mooncompass_world%, %mooncompass_yaw%, %mooncompass_pitch%,
 * %mooncompass_direction%, %mooncompass_direction_short%, %mooncompass_direction_localized%
 */
public class MoonCompassExpansion extends PlaceholderExpansion {

    private static final String IDENTIFIER = "mooncompass";
    private static final String AUTHOR = "Ney";
    private static final String PLUGIN_YML_RESOURCE = "/plugin.yml";
    private static final String UNKNOWN_VERSION = "unknown";

    private final String version;
    private final CompassMessageService compassMessageService;

    public MoonCompassExpansion(@NotNull CompassMessageService compassMessageService) {
        this.version = readPluginVersion();
        this.compassMessageService = compassMessageService;
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull String getAuthor() {
        return AUTHOR;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        if (!(offlinePlayer instanceof Player player) || !player.isOnline()) {
            return null;
        }

        return compassMessageService.resolvePlaceholder(player, params.toLowerCase());

    }

    /**
     * Читает версию плагина из plugin.yml внутри jar.
     * Не использует устаревший getDescription() и экспериментальный getPluginMeta().
     *
     * @return версия плагина или "unknown", если plugin.yml недоступен
     */
    private static @NotNull String readPluginVersion() {

        InputStream pluginYml = MoonCompassExpansion.class.getResourceAsStream(PLUGIN_YML_RESOURCE);

        if (pluginYml == null) {
            return UNKNOWN_VERSION;
        }

        try (Reader reader = new InputStreamReader(pluginYml, StandardCharsets.UTF_8)) {

            return YamlConfiguration.loadConfiguration(reader)
                    .getString("version", UNKNOWN_VERSION);

        } catch (IOException e) {
            return UNKNOWN_VERSION;
        }

    }
}
