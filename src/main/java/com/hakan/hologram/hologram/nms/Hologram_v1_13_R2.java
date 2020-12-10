package com.hakan.hologram.hologram.nms;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Hologram_v1_13_R2 implements Hologram {

    private final Set<EntityArmorStand> entityArmorStands = new HashSet<>();
    private String id;
    private List<String> lines;
    private Location location;
    private Set<UUID> players = new HashSet<>();

    public Hologram_v1_13_R2(String id, List<String> lines, Location location) {
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
                for (Player player : players) {
                    sendPacket(player, spawnPacket);
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

    private void createArmorStands() {
        Variables.holograms.put(getId(), this);
        Location location = getLocation();
        List<String> lines = getLines();
        int size = lines.size();
        if (size == 0) return;
        double startY = location.getY() + (((size * 0.24) + ((size - 1) * 0.05)) / 2);
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer);
            entityArmorStand.setMarker(true);
            entityArmorStand.setArms(false);
            entityArmorStand.setBasePlate(false);
            entityArmorStand.setLocation(location.getX(), startY, location.getZ(), 0, 0);
            entityArmorStand.setNoGravity(true);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + line + "\"}"));
            this.entityArmorStands.add(entityArmorStand);
            startY = startY - 0.245;
        }
    }
}