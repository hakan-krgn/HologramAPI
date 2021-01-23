package com.hakan.hologram.hologram.nms;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Hologram_v1_8_R3 implements Hologram {

    private final Set<EntityArmorStand> entityArmorStands = new HashSet<>();
    private final List<String> playerList = new ArrayList<>();
    private final HashMap<String, Boolean> visible = new HashMap<>();

    private String id;
    private List<String> lines;
    private Location location;

    public Hologram_v1_8_R3(String id, List<String> lines, Location location) {
        this.id = id;
        this.lines = lines;
        this.location = location;
        this.entityArmorStands.addAll(createArmorstand(lines));
        Variables.holograms.put(id, this);
    }

    @Override
    public List<String> getPlayers() {
        return playerList;
    }

    @Override
    public void addPlayer(String playerName) {
        if (playerList.contains(playerName)) return;
        this.playerList.add(playerName);
        visible.put(playerName, true);
        List<Hologram> holograms = Variables.playerHolograms.getOrDefault(playerName, new ArrayList<>());
        holograms.add(this);
        Variables.playerHolograms.put(playerName, holograms);

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return;
        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
            sendPacket(player, spawnPacket, metadataPacket);
        }
    }

    @Override
    public void removePlayer(String playerName) {
        if (!playerList.contains(playerName)) return;
        this.playerList.remove(playerName);
        List<Hologram> holograms = Variables.playerHolograms.getOrDefault(playerName, new ArrayList<>());
        holograms.remove(this);
        Variables.playerHolograms.put(playerName, holograms);

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return;
        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            sendPacket(player, destroyPacket);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<String> getLines() {
        return this.lines;
    }

    @Override
    public void setLines(List<String> lines) {
        this.lines = lines;

        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            sendPacket(playerList, destroyPacket);
        }

        this.entityArmorStands.clear();
        this.entityArmorStands.addAll(createArmorstand(lines));

        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
            sendPacket(playerList, spawnPacket, metadataPacket);
        }
    }

    @Override
    public void setLine(int index, String line) {
        this.lines.set(index, line);
        setLines(this.lines);
    }

    @Override
    public void addLine(String line) {
        this.lines.add(line);
        setLines(this.lines);
    }

    @Override
    public void removeLine(int index) {
        this.lines.remove(index);
        setLines(this.lines);
    }

    @Override
    public void removeLine(String line) {
        int index = 0;
        boolean isFound = false;
        for (String holoLine : this.lines) {
            if (holoLine.equals(line)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            removeLine(index);
        }
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;

        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityArmorStand);
            sendPacket(playerList, teleportPacket);
        }
    }

    @Override
    public boolean isVisible(String playerName) {
        return this.visible.get(playerName);
    }

    @Override
    public void setVisible(String playerName, boolean visible) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            if (this.visible.get(playerName).equals(visible)) {
                return;
            } else if (visible) {
                for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                    PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                    PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                    sendPacket(player, spawnPacket, metadataPacket);
                }
            } else {
                for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                    PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                    sendPacket(player, destroyPacket);
                }
            }
        }
        this.visible.put(playerName, visible);
    }

    @Override
    public void delete() {
        Variables.holograms.remove(id);
        for (String playerName : playerList) {
            List<Hologram> holograms = Variables.playerHolograms.getOrDefault(playerName, new ArrayList<>());
            holograms.remove(this);
            Variables.playerHolograms.put(playerName, holograms);
        }

        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            sendPacket(playerList, destroyPacket);
        }
    }

    private List<EntityArmorStand> createArmorstand(List<String> lines) {

        Location location = getLocation();
        if (location == null) return new ArrayList<>();

        if (lines == null) return new ArrayList<>();
        int size = lines.size();
        if (size == 0) return new ArrayList<>();

        double startY = location.getY() + (((size * 0.24) + ((size - 1) * 0.05)) / 2);

        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld == null) return new ArrayList<>();
        WorldServer worldServer = craftWorld.getHandle();

        List<EntityArmorStand> entityArmorStands = new ArrayList<>();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, location.getX(), startY, location.getZ());
            entityArmorStand.getDataWatcher().watch(10, (byte) 16);
            entityArmorStand.b(nbtTagCompound);
            entityArmorStand.setArms(false);
            entityArmorStand.setBasePlate(false);
            entityArmorStand.setGravity(false);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(line);
            entityArmorStands.add(entityArmorStand);
            startY = startY - 0.245;
        }

        return entityArmorStands;
    }

    private void sendPacket(Player player, Packet... packets) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Packet packet : packets) {
            if (packet == null) continue;
            playerConnection.sendPacket(packet);
        }
    }

    private void sendPacket(List<String> players, Packet... packets) {
        for (String playerName : players) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) continue;
            sendPacket(player, packets);
        }
    }
}