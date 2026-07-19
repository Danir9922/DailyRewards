package com.danir.dailyrewards;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryListener implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final JavaPlugin plugin;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Path logFile;

    public InventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logFile = plugin.getDataFolder().toPath().resolve("logs").resolve("dailyrewards.log");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getView().getTitle().equals("Daily Reward")
                && event.getCurrentItem().getType() == Material.DIAMOND) {
            event.setCancelled(true);

            UUID playerId = event.getWhoClicked().getUniqueId();
            long currentTime = System.currentTimeMillis();
            Player player = (Player) event.getWhoClicked();
            long cooldownHours = plugin.getConfig().getLong("cooldown-hours", 24L);
            long cooldownMillis = cooldownHours * 60L * 60L * 1000L;

            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                long remainingMinutes = (cooldowns.get(playerId) - currentTime) / 1000 / 60;
                String waitMessage = plugin.getConfig().getString("messages.wait", "§cYou must wait %time% minutes!");
                player.sendMessage(waitMessage.replace("%time%", String.valueOf(remainingMinutes)));
            } else {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                cooldowns.put(playerId, currentTime + cooldownMillis);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                String claimed = plugin.getConfig().getString("messages.claimed", "§aYou claimed your daily reward!");
                player.sendMessage(claimed);
                logRewardClaim(player, "diamond");
            }
            player.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerJump(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (player.getFallDistance() > 3.0f) {
            player.sendMessage("§eYou landed safely after a big jump!");
            String message = "[DailyRewards] " + player.getName() + " jumped and landed at "
                    + formatter.format(new Date());
            plugin.getLogger().info(message);
            appendToLog(message);
        }
    }

    private void logRewardClaim(Player player, String rewardName) {
        String message = "[DailyRewards] " + player.getName() + " claimed " + rewardName + " at "
                + formatter.format(new Date());
        plugin.getLogger().info(message);
        appendToLog(message);
    }

    private void appendToLog(String message) {
        try {
            Files.createDirectories(logFile.getParent());
            String entry = formatter.format(new Date()) + " " + message + System.lineSeparator();
            Files.writeString(logFile, entry, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            plugin.getLogger().warning("Unable to write to log file: " + exception.getMessage());
        }
    }
}