package com.hakan.hologram.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Hologram {

    void send(Player... players);

    void sendToAll();

    void delete();

    void update();

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

    Set<UUID> getPlayers();

    void setPlayers(Set<UUID> uuids);

}