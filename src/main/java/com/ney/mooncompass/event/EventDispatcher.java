package com.ney.mooncompass.event;

import com.ney.mooncompass.MoonCompass;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class EventDispatcher {

    private final MoonCompass plugin;

    public EventDispatcher(@NotNull MoonCompass plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }
}
