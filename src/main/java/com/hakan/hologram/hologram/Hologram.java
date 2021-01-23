package com.hakan.hologram.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Hologram {

    Player getPlayer();

    void setPlayer(Player player);

    String getId();

    void setId(String id);

    List<String> getLines();

    void setLines(List<String> lines);

    void setLine(int index, String line);

    void addLine(String line);

    void removeLine(int index);

    void removeLine(String line);

    Location getLocation();

    void setLocation(Location location);

    boolean isVisible();

    void setVisible(boolean visible);

    void delete();

    void update();
}