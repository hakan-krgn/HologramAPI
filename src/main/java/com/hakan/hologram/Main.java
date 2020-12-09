package com.hakan.hologram;

import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.listeners.JoinListener;
import com.hakan.hologram.utils.ServerStatus;
import com.hakan.hologram.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin implements ServerStatus {

    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        whenEnabled(this);
    }

    @Override
    public void onDisable() {
        whenDisabled(this);
    }

    public void whenEnabled(Plugin plugin) {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), plugin);
        Main.instance = plugin;

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

    public void whenDisabled(Plugin plugin) {
        File file = new File("plugins/Holograms/data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Hologram> entry : Variables.holograms.entrySet()) {

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
}