package com.ney.mooncompass;

import com.ney.mooncompass.command.CommandDispatcher;
import com.ney.mooncompass.command.CompassCommand;
import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.direction.DirectionRegistry;
import com.ney.mooncompass.direction.DirectionService;
import com.ney.mooncompass.event.EventDispatcher;
import com.ney.mooncompass.listener.CompassInteractListener;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.placeholder.MoonCompassExpansion;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassItemService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MoonCompass extends JavaPlugin {

    private ConfigManager configManager;

    private DirectionRegistry directionRegistry;

    private CompassItemService compassItemService;
    private CompassCooldownService compassCooldownService;
    private CompassMessageService compassMessageService;
    private CompassSenderService compassSenderService;
    private CompassPermissionService compassPermissionService;

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);

        initializeRegistries();
        initializeServices();

        // Регистрация команд
        new CommandDispatcher(this).registerCommand(
                "compass",
                new CompassCommand(this,
                        configManager,
                        compassMessageService,
                        compassSenderService,
                        compassCooldownService,
                        compassPermissionService
                )
        );

        // Регистрация слушателей
        new EventDispatcher(this).registerEvents(
                new CompassInteractListener(this)
        );

        registerPlaceholderExpansion();

        getLogger().info("MoonCompass успешно запущен!");

    }

    @Override
    public void onDisable() {
        getLogger().info("MoonCompass остановлен!");
    }

    private void initializeRegistries() {
        this.directionRegistry = new DirectionRegistry(configManager);
    }

    private void initializeServices() {

        DirectionService directionService = new DirectionService(directionRegistry);

        this.compassItemService = new CompassItemService(configManager);
        this.compassCooldownService = new CompassCooldownService(configManager);
        this.compassMessageService = new CompassMessageService(
                configManager,
                directionService
        );
        this.compassSenderService = new CompassSenderService(compassMessageService);
        this.compassPermissionService = new CompassPermissionService(configManager);

    }

    private void registerPlaceholderExpansion() {

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        new MoonCompassExpansion(compassMessageService).register();
        getLogger().info("PlaceholderAPI найден — экспансия зарегистрирована!");

    }

    /**
     * Полная перезагрузка плагина: конфигурация и все зависящие от неё реестры.
     */
    public void reloadPlugin() {

        configManager.reload();
        directionRegistry.reloadRegistry();

        getLogger().info("Конфигурация MoonCompass перезагружена!");

    }

    public @NotNull ConfigManager getConfigManager() {
        return configManager;
    }

    public @NotNull CompassItemService getCompassItemService() {
        return compassItemService;
    }

    public @NotNull CompassCooldownService getCompassCooldownService() {
        return compassCooldownService;
    }

    public @NotNull CompassMessageService getCompassMessageService() {
        return compassMessageService;
    }

    public @NotNull CompassSenderService getCompassSenderService() {
        return compassSenderService;
    }

    public @NotNull CompassPermissionService getCompassPermissionService() {
        return compassPermissionService;
    }
}
