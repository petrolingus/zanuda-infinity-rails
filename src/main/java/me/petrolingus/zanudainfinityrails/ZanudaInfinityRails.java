package me.petrolingus.zanudainfinityrails;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ZanudaInfinityRails extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new MinecartListener(), this);
    }

    @Override
    public void onDisable() {
        // Currently nothing
    }

    public static ZanudaInfinityRails getInstance() {
        return getPlugin(ZanudaInfinityRails.class);
    }

}