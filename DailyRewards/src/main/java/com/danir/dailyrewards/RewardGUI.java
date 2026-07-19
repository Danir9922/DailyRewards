package com.danir.dailyrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardGUI {

    public static Inventory getMenu(JavaPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 9, "Daily Reward");

        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta meta = diamond.getItemMeta();
        if (meta != null) {
            String displayName = plugin.getConfig().getString("gui.item-name", "§aClick to claim Reward!");
            meta.setDisplayName(displayName);
            diamond.setItemMeta(meta);
        }

        inv.setItem(4, diamond);
        return inv;
    }
}