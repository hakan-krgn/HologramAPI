package com.hakan.hologram.api;

import com.hakan.hologram.Main;
import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.hologram.nms.*;
import com.hakan.hologram.listeners.JoinListener;
import com.hakan.hologram.listeners.TeleportListener;
import com.hakan.hologram.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class HologramAPI {

    public static List<Hologram> getHolograms() {
        return new ArrayList<>(Variables.holograms.values());
    }

    public static Hologram getHologram(String id) {
        return Variables.holograms.get(id);
    }

    public static boolean isAlive(String id) {
        return getHologram(id) != null;
    }

    public static void setup(Plugin plugin) {
        if (Main.instance == null) {
            Main.instance = plugin;
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvents(new JoinListener(), plugin);
            pm.registerEvents(new TeleportListener(), plugin);
        } else {
            Bukkit.getLogger().warning("HologramAPI already registered.");
        }
    }

    public static HologramManager getHologramManager() {
        return new HologramManager();
    }

    public static class HologramManager {

        private String id;
        private List<String> lines;
        private Location location;

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

        public HologramManager setLocation(Location location) {
            this.location = location;
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
                    return new Hologram_v1_16_R2(this.id, lines, this.location);
                case "v1_16_R3":
                    return new Hologram_v1_16_R3(this.id, lines, this.location);
            }
            return null;
        }
    }
}