package com.hakan.hologram;

import com.hakan.hologram.api.HologramAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        HologramAPI.setup(this);
    }

    @Override
    public void onDisable() {
        HologramAPI.unsetup();
    }
}