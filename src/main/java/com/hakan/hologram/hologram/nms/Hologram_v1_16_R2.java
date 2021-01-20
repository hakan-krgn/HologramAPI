package com.hakan.hologram.hologram.nms;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Hologram_v1_16_R2 implements Hologram {

    private final Set<EntityArmorStand> entityArmorStands = new HashSet<>();
    private String id;
    private List<String> lines;
    private Location location;
    private boolean visible;
    private Set<UUID> players = new HashSet<>();

    public Hologram_v1_16_R2(String id, List<String> lines, Location location) {
        this.id = id;
        this.lines = lines;
        this.location = location;
        createArmorStands();
    }

    @Override
    public void send(Player... players) {
        for (Player player : players) {
            this.players.add(player.getUniqueId());
        }
        if (this.entityArmorStands.size() > 0) {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                for (Player player : players) {
                    sendPacket(player, spawnPacket);
                    sendPacket(player, metadataPacket);
                }
            }
        }
    }

    @Override
    public void sendToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player);
        }
    }

    @Override
    public void addPlayer(OfflinePlayer player) {
        this.players.add(player.getUniqueId());
    }

    @Override
    public void removePlayer(OfflinePlayer offlinePlayer) {
        this.players.remove(offlinePlayer.getUniqueId());
        Player player = Bukkit.getPlayerExact(offlinePlayer.getName());
        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityDestroy deletePacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            if (player != null) sendPacket(player, deletePacket);
        }
    }

    @Override
    public void delete() {
        if (this.entityArmorStands.size() > 0) {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutEntityDestroy deletePacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                for (UUID uuid : getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) sendPacket(player, deletePacket);
                }
            }
        }
        this.entityArmorStands.clear();
        Variables.holograms.remove(getId());
    }

    @Override
    public void update() {
        delete();
        createArmorStands();
        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) send(player);
        }
    }

    private void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
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
        update();
    }

    @Override
    public void setLine(int index, String line) {
        this.lines.set(index, line);
        update();
    }

    @Override
    public void addLine(String line) {
        this.lines.add(line);
        update();
    }

    @Override
    public void removeLine(int index) {
        this.lines.remove(index);
        update();
    }

    @Override
    public void removeLine(String line) {
        this.lines.remove(line);
        update();
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    @Override
    public Set<UUID> getPlayers() {
        return this.players;
    }

    @Override
    public void setPlayers(Set<UUID> uuids) {
        this.players = uuids;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            if (visible && !this.visible) {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    sendPacket(player, spawnPacket);
                    sendPacket(player, metadataPacket);
                }
            } else if (!visible && this.visible) {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    sendPacket(player, destroyPacket);
                }
            }
        }
        this.visible = visible;
    }

    private void createArmorStands() {
        Variables.holograms.put(getId(), this);
        Location location = getLocation();
        if (location == null) return;
        List<String> lines = getLines();
        if (lines == null) return;
        int size = lines.size();
        if (size == 0) return;
        double startY = location.getY() + (((size * 0.24) + ((size - 1) * 0.05)) / 2);
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld == null) return;
        WorldServer worldServer = craftWorld.getHandle();
        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, location.getX(), startY, location.getZ());
            entityArmorStand.setMarker(true);
            entityArmorStand.setArms(false);
            entityArmorStand.setBasePlate(false);
            entityArmorStand.setNoGravity(true);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + line + "\"}"));
            this.entityArmorStands.add(entityArmorStand);
            startY = startY - 0.245;
        }
    }
}