package com.danir.dailyrewards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public RewardCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.command-only", "Only players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("dailyrewards.use")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission", "§cYou do not have permission to use this command."));
            return true;
        }

        String opening = plugin.getConfig().getString("messages.opening-menu", "Opening reward menu...");
        player.sendMessage(opening);
        player.openInventory(RewardGUI.getMenu(plugin));
        return true;
    }
}