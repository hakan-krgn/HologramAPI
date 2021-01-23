package com.hakan.hologram.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Hologram {

    List<String> getPlayers();

    void addPlayer(String playerName);

    void removePlayer(String playerName);

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

    boolean isVisible(String playerName);

    void setVisible(String playerName, boolean visible);

    void delete();

    void sendAgain(Player player);
}