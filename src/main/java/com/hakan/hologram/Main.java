package com.hakan.hologram;

import com.hakan.hologram.api.HologramAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        HologramAPI.setup(this);
    }
}