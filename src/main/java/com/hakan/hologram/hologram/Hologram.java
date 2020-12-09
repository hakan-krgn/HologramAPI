package com.hakan.hologram.hologram;

import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Hologram {

    private String id;
    private List<String> lines;
    private Location location;
    private Set<UUID> players = new HashSet<>();
    private final Set<EntityArmorStand> entityArmorStands = new HashSet<>();

    public Hologram(String id, List<String> lines, Location location) {
        this.id = id;
        this.lines = lines;
        this.location = location;
        createArmorStands();
    }

    public void send(Player... players) {
        for (Player player : players) {
            this.players.add(player.getUniqueId());
        }
        if (this.entityArmorStands.size() > 0) {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                for (Player player : players) {
                    sendPacket(player, spawnPacket);
                }
            }
        }
    }

    public void sendToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player);
        }
    }

    public void delete() {
        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutEntityDestroy deletePacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            for (UUID uuid : getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) sendPacket(player, deletePacket);
            }
        }
        this.entityArmorStands.clear();
        Variables.holograms.remove(getId());
    }

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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        update();
    }

    public void setLine(int index, String line) {
        this.lines.set(index, line);
        update();
    }

    public void addLine(String line) {
        this.lines.add(line);
        update();
    }

    public void removeLine(int index) {
        this.lines.remove(index);
        update();
    }

    public void removeLine(String line) {
        this.lines.remove(line);
        update();
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<UUID> uuids) {
        this.players = uuids;
    }

    private void createArmorStands() {
        Variables.holograms.put(getId(), this);
        Location location = getLocation();
        List<String> lines = getLines();
        int size = lines.size();
        if (size == 0) return;
        double startY = location.getY() + (((size * 0.24) + ((size - 1) * 0.05)) / 2);
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer);
            entityArmorStand.getDataWatcher().watch(10, (byte) 16);
            entityArmorStand.b(nbtTagCompound);
            entityArmorStand.setArms(false);
            entityArmorStand.setBasePlate(false);
            entityArmorStand.setLocation(location.getX(), startY, location.getZ(), 0, 0);
            entityArmorStand.setGravity(false);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(line);
            this.entityArmorStands.add(entityArmorStand);
            startY = startY - 0.245;
        }
    }
}