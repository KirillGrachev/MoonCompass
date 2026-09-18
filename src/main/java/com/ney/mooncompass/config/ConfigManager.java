package com.ney.mooncompass.config;

import com.ney.mooncompass.MoonCompass;
import com.ney.mooncompass.config.type.CompassHand;
import com.ney.mooncompass.config.type.DisplayMode;
import com.ney.mooncompass.util.EnumParseUtil;
import com.ney.mooncompass.util.HexColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigManager implements MoonCompassConfig {

    private final MoonCompass plugin;
    private FileConfiguration config;

    private static final String PATH_ENABLED = "settings.enabled";
    private static final String PATH_OP_BYPASS = "settings.op_bypass";
    private static final String PATH_CLICK_ENABLED = "settings.click.enabled";
    private static final String PATH_CLICK_ITEM = "settings.click.item";
    private static final String PATH_CLICK_HAND = "settings.click.hand";
    private static final String PATH_CLICK_REQUIRE_NAME = "settings.click.require_name";
    private static final String PATH_CLICK_NAME = "settings.click.name";
    private static final String PATH_OUTPUT_MODE = "settings.output.mode";
    private static final String PATH_COOLDOWN_ENABLED = "settings.cooldown.enabled";
    private static final String PATH_COOLDOWN_SECONDS = "settings.cooldown.seconds";
    private static final String PATH_COOLDOWN_APPLY_TO_COMMAND = "settings.cooldown.apply_to_command";
    private static final String PATH_PERMISSIONS_ENABLED = "settings.permissions.enabled";
    private static final String PATH_PERMISSION_USE = "settings.permissions.use";
    private static final String PATH_PERMISSION_COOLDOWN_BYPASS = "settings.permissions.cooldown_bypass";
    private static final String PATH_PERMISSION_RELOAD = "settings.permissions.reload";
    private static final String PATH_DIRECTION_NAMES = "settings.list.names";
    private static final String PATH_DIRECTION_SHORT_NAMES = "settings.list.short_names";
    private static final String PATH_DIRECTION_LOCALIZED_NAMES = "settings.list.localized_names";
    private static final String PATH_PREFIX = "messages.prefix";
    private static final String PATH_CHAT_ENABLED = "messages.chat.enabled";
    private static final String PATH_CHAT_TEXT = "messages.chat.text";
    private static final String PATH_ACTION_BAR_ENABLED = "messages.action_bar.enabled";
    private static final String PATH_ACTION_BAR_TEXT = "messages.action_bar.text";
    private static final String PATH_ONLY_PLAYER_ENABLED = "messages.only_player.enabled";
    private static final String PATH_ONLY_PLAYER_TEXT = "messages.only_player.text";
    private static final String PATH_NO_PERMISSION_ENABLED = "messages.no_permission.enabled";
    private static final String PATH_NO_PERMISSION_TEXT = "messages.no_permission.text";
    private static final String PATH_COOLDOWN_MESSAGE_ENABLED = "messages.on_cooldown.enabled";
    private static final String PATH_COOLDOWN_MESSAGE_TEXT = "messages.on_cooldown.text";
    private static final String PATH_RELOADED_ENABLED = "messages.reloaded.enabled";
    private static final String PATH_RELOADED_TEXT = "messages.reloaded.text";
    private static final String PATH_DISABLED_ENABLED = "messages.disabled.enabled";
    private static final String PATH_DISABLED_TEXT = "messages.disabled.text";
    private static final String PATH_USAGE_ENABLED = "messages.usage.enabled";
    private static final String PATH_USAGE_TEXT = "messages.usage.text";

    private boolean enabled;
    private boolean opBypass;
    private boolean clickEnabled;
    private boolean nameRequired;
    private boolean cooldownEnabled;
    private boolean cooldownAppliedToCommand;
    private boolean permissionsEnabled;
    private boolean chatMessagesEnabled;
    private boolean actionBarMessagesEnabled;
    private boolean onlyPlayerEnabled;
    private boolean noPermissionEnabled;
    private boolean cooldownMessageEnabled;
    private boolean reloadedEnabled;
    private boolean disabledEnabled;
    private boolean usageEnabled;

    private Material clickItem;
    private CompassHand clickHand;
    private DisplayMode displayMode;
    private long cooldownMillis;

    private String requiredItemName;
    private String permissionUse;
    private String permissionCooldownBypass;
    private String permissionReload;
    private String prefix;

    private Map<String, String> directionNames;
    private Map<String, String> directionShortNames;
    private Map<String, String> directionLocalizedNames;
    private List<String> chatLines;
    private List<String> actionBarLines;
    private List<String> onlyPlayerMessage;
    private List<String> noPermissionMessage;
    private List<String> cooldownMessage;
    private List<String> reloadedMessage;
    private List<String> disabledMessage;
    private List<String> usageMessage;

    public ConfigManager(@NotNull MoonCompass plugin) {

        this.plugin = plugin;
        saveDefaultConfig();

        loadConfig();
        cacheConfigValues();

    }

    private void saveDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    private void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void cacheConfigValues() {

        enabled = config.getBoolean(PATH_ENABLED, true);
        opBypass = config.getBoolean(PATH_OP_BYPASS, false);

        clickEnabled = config.getBoolean(PATH_CLICK_ENABLED, true);
        nameRequired = config.getBoolean(PATH_CLICK_REQUIRE_NAME, false);

        cooldownEnabled = config.getBoolean(PATH_COOLDOWN_ENABLED, true);
        cooldownAppliedToCommand = config.getBoolean(PATH_COOLDOWN_APPLY_TO_COMMAND, false);

        permissionsEnabled = config.getBoolean(PATH_PERMISSIONS_ENABLED, true);

        chatMessagesEnabled = config.getBoolean(PATH_CHAT_ENABLED, true);
        actionBarMessagesEnabled = config.getBoolean(PATH_ACTION_BAR_ENABLED, true);
        onlyPlayerEnabled = config.getBoolean(PATH_ONLY_PLAYER_ENABLED, true);
        noPermissionEnabled = config.getBoolean(PATH_NO_PERMISSION_ENABLED, true);
        cooldownMessageEnabled = config.getBoolean(PATH_COOLDOWN_MESSAGE_ENABLED, true);
        reloadedEnabled = config.getBoolean(PATH_RELOADED_ENABLED, true);
        disabledEnabled = config.getBoolean(PATH_DISABLED_ENABLED, true);
        usageEnabled = config.getBoolean(PATH_USAGE_ENABLED, true);

        clickItem = EnumParseUtil.parseMaterial(
                config.getString(PATH_CLICK_ITEM), Material.COMPASS);
        clickHand = EnumParseUtil.parse(
                config.getString(PATH_CLICK_HAND), CompassHand.class, CompassHand.BOTH);
        displayMode = EnumParseUtil.parse(
                config.getString(PATH_OUTPUT_MODE), DisplayMode.class, DisplayMode.CHAT);

        cooldownMillis = Math.max(0L, config.getLong(PATH_COOLDOWN_SECONDS, 3L)) * 1000L;

        requiredItemName = config.getString(PATH_CLICK_NAME);
        permissionUse = config.getString(PATH_PERMISSION_USE, "mooncompass.use");
        permissionCooldownBypass = config.getString(PATH_PERMISSION_COOLDOWN_BYPASS, "mooncompass.cooldown.bypass");
        permissionReload = config.getString(PATH_PERMISSION_RELOAD, "mooncompass.reload");
        prefix = HexColorUtil.color(config.getString(PATH_PREFIX, ""));

        directionNames = cacheStringMap(PATH_DIRECTION_NAMES);
        directionShortNames = cacheStringMap(PATH_DIRECTION_SHORT_NAMES);
        directionLocalizedNames = cacheStringMap(PATH_DIRECTION_LOCALIZED_NAMES);
        chatLines = cacheColoredList(PATH_CHAT_TEXT);
        actionBarLines = cacheColoredList(PATH_ACTION_BAR_TEXT);
        onlyPlayerMessage = cacheColoredList(PATH_ONLY_PLAYER_TEXT);
        noPermissionMessage = cacheColoredList(PATH_NO_PERMISSION_TEXT);
        cooldownMessage = cacheColoredList(PATH_COOLDOWN_MESSAGE_TEXT);
        reloadedMessage = cacheColoredList(PATH_RELOADED_TEXT);
        disabledMessage = cacheColoredList(PATH_DISABLED_TEXT);
        usageMessage = cacheColoredList(PATH_USAGE_TEXT);

    }

    private @NotNull Map<String, String> cacheStringMap(@NotNull String path) {

        ConfigurationSection section = config.getConfigurationSection(path);
        Map<String, String> values = new LinkedHashMap<>();

        if (section == null) {
            return values;
        }

        for (String key : section.getKeys(false)) {

            String value = section.getString(key);

            if (value != null && !value.isEmpty()) {
                values.put(key, value);
            }

        }

        return values;

    }

    private @NotNull List<String> cacheColoredList(@NotNull String path) {
        return config.getStringList(path).stream()
                .map(HexColorUtil::color)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isOpBypass() {
        return opBypass;
    }

    @Override
    public boolean isClickEnabled() {
        return clickEnabled;
    }

    @Override
    public @NotNull Material getClickItem() {
        return clickItem;
    }

    @Override
    public @NotNull CompassHand getClickHand() {
        return clickHand;
    }

    @Override
    public boolean isNameRequired() {
        return nameRequired;
    }

    @Override
    public @Nullable String getRequiredItemName() {

        if (requiredItemName == null || requiredItemName.isEmpty()) {
            return null;
        }

        return HexColorUtil.color(requiredItemName);

    }

    @Override
    public @NotNull DisplayMode getDisplayMode() {
        return displayMode;
    }

    @Override
    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    @Override
    public long getCooldownMillis() {
        return cooldownMillis;
    }

    @Override
    public boolean isCooldownAppliedToCommand() {
        return cooldownAppliedToCommand;
    }

    @Override
    public boolean arePermissionsEnabled() {
        return permissionsEnabled;
    }

    @Override
    public @NotNull String getPermissionUse() {
        return permissionUse;
    }

    @Override
    public @NotNull String getPermissionCooldownBypass() {
        return permissionCooldownBypass;
    }

    @Override
    public @NotNull String getPermissionReload() {
        return permissionReload;
    }

    @Override
    public @NotNull Map<String, String> getDirectionNames() {
        return directionNames;
    }

    @Override
    public @NotNull Map<String, String> getDirectionShortNames() {
        return directionShortNames;
    }

    @Override
    public @NotNull Map<String, String> getDirectionLocalizedNames() {
        return directionLocalizedNames;
    }

    @Override
    public @NotNull String getPrefix() {
        return prefix;
    }

    @Override
    public boolean areChatMessagesEnabled() {
        return chatMessagesEnabled;
    }

    @Override
    public @NotNull List<String> getChatLines() {
        return chatLines;
    }

    @Override
    public boolean areActionBarMessagesEnabled() {
        return actionBarMessagesEnabled;
    }

    @Override
    public @NotNull List<String> getActionBarLines() {
        return actionBarLines;
    }

    @Override
    public @NotNull List<String> getOnlyPlayerMessage() {
        return onlyPlayerEnabled ? onlyPlayerMessage : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getNoPermissionMessage() {
        return noPermissionEnabled ? noPermissionMessage : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getCooldownMessage() {
        return cooldownMessageEnabled ? cooldownMessage : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getReloadedMessage() {
        return reloadedEnabled ? reloadedMessage : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getDisabledMessage() {
        return disabledEnabled ? disabledMessage : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getUsageMessage() {
        return usageEnabled ? usageMessage : Collections.emptyList();
    }

    /**
     * Перечитывает config.yml с диска и обновляет кэш значений.
     */
    public void reload() {
        loadConfig();
        cacheConfigValues();
    }
}
