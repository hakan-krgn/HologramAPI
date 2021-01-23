package com.hakan.hologram.hologram.nms;

import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hologram_v1_15_R1 implements Hologram {

    private final Set<EntityArmorStand> entityArmorStands = new HashSet<>();

    private Player player;
    private String id;
    private List<String> lines;
    private Location location;
    private boolean visible = false;

    public Hologram_v1_15_R1(Player player, String id, List<String> lines, Location location) {
        this.player = player;
        this.id = id;
        this.lines = lines;
        this.location = location;
        this.entityArmorStands.addAll(createArmorstand(lines));
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
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
    }

    @Override
    public void setLine(int index, String line) {
        this.lines.set(index, line);
    }

    @Override
    public void addLine(String line) {
        this.lines.add(line);
    }

    @Override
    public void removeLine(int index) {
        this.lines.remove(index);
    }

    @Override
    public void removeLine(String line) {
        int index = 0;
        for (String holoLine : this.lines) {
            if (holoLine.equals(line)) {
                break;
            }
            index++;
        }
        removeLine(index);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void delete() {
        Variables.holograms.remove(this.player);
    }

    @Override
    public void update() {
        if (player == null || !player.isOnline()) {
            return;
        }
        if (HologramAPI.isAlive(player)) {
            this.entityArmorStands.clear();
            this.entityArmorStands.addAll(createArmorstand(this.lines));

            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityArmorStand);
                if (!visible) {
                    PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                    sendPacket(player, teleportPacket, destroyPacket);
                } else {
                    PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                    sendPacket(player, teleportPacket, spawnPacket);
                }
            }
        } else {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                sendPacket(player, destroyPacket);
            }
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

        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, location.getX(), startY, location.getZ());
            entityArmorStand.setMarker(true);
            entityArmorStand.setArms(false);
            entityArmorStand.setBasePlate(false);
            entityArmorStand.setNoGravity(true);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + line + "\"}"));
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
}