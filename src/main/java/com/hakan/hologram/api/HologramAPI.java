package com.hakan.hologram.api;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.hologram.nms.*;
import com.hakan.hologram.listeners.JoinListener;
import com.hakan.hologram.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HologramAPI {

    public static Hologram getHologram(String id) {
        return Variables.holograms.get(id);
    }

    public static boolean isAlive(String id) {
        return getHologram(id) != null;
    }

    public static void setup(Plugin plugin) {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), plugin);

        File file = new File("plugins/Holograms/data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (String id : data.getConfigurationSection("").getKeys(false)) {
            String[] locString = data.getString(id + ".location").split(",");
            List<String> lines = data.getStringList(id + ".lines");
            Location location = new Location(Bukkit.getWorld(locString[0]), Double.parseDouble(locString[1]), Double.parseDouble(locString[2]), Double.parseDouble(locString[3]));

            HologramAPI.HologramManager hologramManager = HologramAPI.getHologramManager().setId(id).setLines(lines).setLocation(location);
            Hologram hologram = hologramManager.create();
            Set<UUID> uuids = new HashSet<>();
            for (String uuidString : data.getStringList(id + ".players")) {
                uuids.add(UUID.fromString(uuidString));
            }
            hologram.setPlayers(uuids);

            Variables.holograms.put(id, hologram);

            hologram.update();
        }
        file.delete();
    }

    public static void unsetup() {
        Set<Map.Entry<String, Hologram>> set = new HashSet<>(Variables.holograms.entrySet());
        File file = new File("plugins/Holograms/data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Hologram> entry : set) {

            Hologram hologram = entry.getValue();
            hologram.delete();

            String id = hologram.getId();
            Location location = hologram.getLocation();

            data.set(id + ".lines", hologram.getLines());
            data.set(id + ".location", location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
            List<String> uuidStringList = new ArrayList<>();
            for (UUID uuid : hologram.getPlayers()) {
                uuidStringList.add(uuid.toString());
            }
            data.set(id + ".players", uuidStringList);

        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
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
            switch (serverVersion) {
                case "v1_8_R3":
                    return new Hologram_v1_8_R3(this.id, this.lines, this.location);
                case "v1_9_R1":
                    return new Hologram_v1_9_R1(this.id, this.lines, this.location);
                case "v1_9_R2":
                    return new Hologram_v1_9_R2(this.id, this.lines, this.location);
                case "v1_10_R1":
                    return new Hologram_v1_10_R1(this.id, this.lines, this.location);
                case "v1_11_R1":
                    return new Hologram_v1_11_R1(this.id, this.lines, this.location);
                case "v1_12_R1":
                    return new Hologram_v1_12_R1(this.id, this.lines, this.location);
                case "v1_13_R1":
                    return new Hologram_v1_13_R1(this.id, this.lines, this.location);
                case "v1_13_R2":
                    return new Hologram_v1_13_R2(this.id, this.lines, this.location);
                case "v1_14_R1":
                    return new Hologram_v1_14_R1(this.id, this.lines, this.location);
                case "v1_15_R1":
                    return new Hologram_v1_15_R1(this.id, this.lines, this.location);
                case "v1_16_R1":
                    return new Hologram_v1_16_R1(this.id, this.lines, this.location);
                case "v1_16_R2":
                    return new Hologram_v1_16_R2(this.id, this.lines, this.location);
                case "v1_16_R3":
                    return new Hologram_v1_16_R3(this.id, this.lines, this.location);
            }
            return null;
        }
    }
}