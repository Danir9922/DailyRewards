package com.danir.dailyrewards;

import org.bukkit.plugin.java.JavaPlugin;

public class DailyRewardsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getCommand("reward") != null) {
            getCommand("reward").setExecutor(new RewardCommand(this));
        }
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getLogger().info("DailyRewards has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DailyRewards has been disabled!");
    }
}