package com.ney.mooncompass.service;

import com.ney.mooncompass.config.ConfigManager;
import com.ney.mooncompass.util.HexColorUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Определяет, является ли предмет в руке игрока «компасом» плагина.
 */
public class CompassItemService {

    private final ConfigManager configManager;

    public CompassItemService(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Проверяет предмет на соответствие настройкам settings.click.
     *
     * @param itemStack предмет из PlayerInteractEvent
     * @return true, если предмет должен сработать как компас
     */
    public boolean isCompassItem(@Nullable ItemStack itemStack) {

        if (!configManager.isClickEnabled()) return false;
        if (!isValidItemStack(itemStack)) return false;
        if (!isConfiguredMaterial(itemStack)) return false;
        if (!configManager.isNameRequired()) return true;

        return isNameMatching(itemStack);

    }

    private boolean isValidItemStack(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    private boolean isConfiguredMaterial(@NotNull ItemStack itemStack) {
        return itemStack.getType() == configManager.getClickItem();
    }

    /**
     * Сравнивает display name предмета с settings.click.name.
     * Намеренно используется legacy-API имён: config.yml хранит строки
     * с цветовыми кодами (& и HEX), а не Adventure-компоненты.
     */
    @SuppressWarnings("deprecation")
    private boolean isNameMatching(@NotNull ItemStack itemStack) {

        String requiredName = configManager.getRequiredItemName();
        if (requiredName == null) return true;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName()) return false;

        return HexColorUtil.color(itemMeta.getDisplayName()).equals(requiredName);

    }
}
