package com.ney.mooncompass.command;

import com.ney.mooncompass.MoonCompass;
import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.event.CompassUseEvent;
import com.ney.mooncompass.permission.CompassPermission;
import com.ney.mooncompass.permission.CompassPermissionService;
import com.ney.mooncompass.service.CompassCooldownService;
import com.ney.mooncompass.service.CompassMessageService;
import com.ney.mooncompass.service.CompassSenderService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Команда /compass — координаты и направление взгляда, перезагрузка конфигурации.
 */
public class CompassCommand implements TabExecutor {

    private static final String ARG_RELOAD = "reload";

    private final MoonCompass plugin;
    private final ConfigManager configManager;
    private final CompassMessageService messageService;
    private final CompassSenderService senderService;
    private final CompassCooldownService cooldownService;
    private final CompassPermissionService permissionService;

    public CompassCommand(@NotNull MoonCompass plugin,
                          @NotNull ConfigManager configManager,
                          @NotNull CompassMessageService messageService,
                          @NotNull CompassSenderService senderService,
                          @NotNull CompassCooldownService cooldownService,
                          @NotNull CompassPermissionService permissionService) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageService = messageService;
        this.senderService = senderService;
        this.cooldownService = cooldownService;
        this.permissionService = permissionService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String @NotNull [] args) {

        if (args.length == 0) {

            useCompass(sender);
            return true;

        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {

            case ARG_RELOAD -> reload(sender);

            default -> sendMessage(sender, configManager.getUsageMessage());

        }

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                String @NotNull [] args) {

        if (args.length == 1) {

            List<String> suggestions = new ArrayList<>();

            if (sender.hasPermission(configManager.getPermissionReload())) {
                suggestions.add(ARG_RELOAD);
            }

            return filter(suggestions, args[0]);

        }

        return Collections.emptyList();

    }

    /**
     * Показывает игроку координаты и направление взгляда.
     *
     * @param sender отправитель команды
     */
    private void useCompass(@NotNull CommandSender sender) {

        if (!(sender instanceof Player player)) {

            sendMessage(sender, configManager.getOnlyPlayerMessage());
            return;

        }

        new CompassUseEvent(configManager,
                messageService,
                senderService,
                cooldownService,
                permissionService
        ).onCompassUse(player, true);

    }

    /**
     * Перезагружает конфигурацию и все зависящие от неё реестры.
     *
     * @param sender отправитель команды
     */
    private void reload(@NotNull CommandSender sender) {

        if (!permissionService.hasPermission(sender, CompassPermission.RELOAD)) {

            sendMessage(sender, configManager.getNoPermissionMessage());
            return;

        }

        plugin.reloadPlugin();
        sendMessage(sender, configManager.getReloadedMessage());

    }

    private void sendMessage(@NotNull CommandSender sender, @NotNull List<String> lines) {
        lines.forEach(sender::sendMessage);
    }

    /**
     * Оставляет только значения, начинающиеся с введённого текста.
     *
     * @param values список вариантов
     * @param token  введённый текст
     * @return отфильтрованный список
     */
    public static @NotNull List<String> filter(@NotNull List<String> values, @NotNull String token) {

        String lowerToken = token.toLowerCase();

        return values.stream()
                .filter(value -> value.toLowerCase().startsWith(lowerToken))
                .toList();

    }
}
