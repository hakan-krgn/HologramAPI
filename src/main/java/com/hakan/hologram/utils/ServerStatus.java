package com.hakan.hologram.utils;

import org.bukkit.plugin.Plugin;

public interface ServerStatus {

    void whenEnabled(Plugin plugin);

    void whenDisabled(Plugin plugin);

}