package com.hakan.hologram;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.hologram.nms.*;
import com.hakan.hologram.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HologramAPI {

    public static Plugin instance;

    public static Hologram getHologram(String id) {
        return Variables.hologramList.get(id);
    }

    public static boolean isAlive(String id) {
        return Variables.hologramList.containsKey(id);
    }

    public static void setup(Plugin plugin) {
        if (instance == null) {
            instance = plugin;
        } else {
            Bukkit.getLogger().warning("HologramAPI already registered.");
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onDisable(PluginDisableEvent event) {
                if (event.getPlugin().equals(plugin)) {
                    for (Hologram hologram : Variables.hologramList.values()) {
                        hologram.delete();
                    }
                }
            }
        }, plugin);
    }

    public static HologramManager getHologramManager() {
        return new HologramManager();
    }

    public static class HologramManager {

        private String id;
        private List<String> lines = new ArrayList<>();
        private Location location;

        public HologramManager setLocation(Location location) {
            this.location = location;
            return this;
        }

        public HologramManager setId(String id) {
            this.id = id;
            return this;
        }

        public HologramManager setLines(List<String> lines) {
            this.lines = lines;
            return this;
        }

        public HologramManager setLine(int index, String line) {
            this.lines.set(index, line);
            return this;
        }

        public HologramManager addLine(String line) {
            this.lines.add(line);
            return this;
        }

        public Hologram create() {
            String serverVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
            List<String> lines = new ArrayList<>();
            for (String line : this.lines) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            switch (serverVersion) {
                case "v1_8_R3":
                    return new Hologram_v1_8_R3(this.id, lines, this.location);
                case "v1_9_R1":
                    return new Hologram_v1_9_R1(this.id, lines, this.location);
                case "v1_9_R2":
                    return new Hologram_v1_9_R2(this.id, lines, this.location);
                case "v1_10_R1":
                    return new Hologram_v1_10_R1(this.id, lines, this.location);
                case "v1_11_R1":
                    return new Hologram_v1_11_R1(this.id, lines, this.location);
                case "v1_12_R1":
                    return new Hologram_v1_12_R1(this.id, lines, this.location);
                case "v1_13_R1":
                    return new Hologram_v1_13_R1(this.id, lines, this.location);
                case "v1_13_R2":
                    return new Hologram_v1_13_R2(this.id, lines, this.location);
                case "v1_14_R1":
                    return new Hologram_v1_14_R1(this.id, lines, this.location);
                case "v1_15_R1":
                    return new Hologram_v1_15_R1(this.id, lines, this.location);
                case "v1_16_R1":
                    return new Hologram_v1_16_R1(this.id, lines, this.location);
                case "v1_16_R2":
                    return new Hologram_v1_13_R1(this.id, lines, this.location);
                case "v1_16_R3":
                    return new Hologram_v1_16_R3(this.id, lines, this.location);
            }
            return null;
        }
    }
}