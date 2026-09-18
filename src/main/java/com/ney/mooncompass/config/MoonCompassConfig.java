package com.ney.mooncompass.config;

import com.ney.mooncompass.config.type.CompassHand;
import com.ney.mooncompass.config.type.DisplayMode;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface MoonCompassConfig {

    boolean isEnabled();

    boolean isOpBypass();

    boolean isClickEnabled();

    @NotNull Material getClickItem();

    @NotNull CompassHand getClickHand();

    boolean isNameRequired();

    @Nullable String getRequiredItemName();

    @NotNull DisplayMode getDisplayMode();

    boolean isCooldownEnabled();

    long getCooldownMillis();

    boolean isCooldownAppliedToCommand();

    boolean arePermissionsEnabled();

    @NotNull String getPermissionUse();

    @NotNull String getPermissionCooldownBypass();

    @NotNull String getPermissionReload();

    @NotNull Map<String, String> getDirectionNames();

    @NotNull Map<String, String> getDirectionShortNames();

    @NotNull Map<String, String> getDirectionLocalizedNames();

    @NotNull String getPrefix();

    boolean areChatMessagesEnabled();

    @NotNull List<String> getChatLines();

    boolean areActionBarMessagesEnabled();

    @NotNull List<String> getActionBarLines();

    @NotNull List<String> getOnlyPlayerMessage();

    @NotNull List<String> getNoPermissionMessage();

    @NotNull List<String> getCooldownMessage();

    @NotNull List<String> getReloadedMessage();

    @NotNull List<String> getDisabledMessage();

    @NotNull List<String> getUsageMessage();

}
