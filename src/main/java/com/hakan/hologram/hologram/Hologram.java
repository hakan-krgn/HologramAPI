package com.hakan.hologram.hologram;

import org.bukkit.Location;

import java.util.List;

public interface Hologram {

    void send(String playerName);

    void sendAll();

    void remove(String playerName);

    void removeAll();

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

    void delete();
}