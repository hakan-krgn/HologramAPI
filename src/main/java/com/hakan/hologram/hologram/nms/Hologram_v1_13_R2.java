package com.hakan.hologram.hologram.nms;

import com.hakan.hologram.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Hologram_v1_13_R2 implements Hologram {

    private final Set<String> players = new HashSet<>();
    private final Map<String, Listener> playerListener;

    private Set<EntityArmorStand> entityArmorStands;
    private String id;
    private Location location;
    private List<String> lines;

    private Listener allListener;

    public Hologram_v1_13_R2(String id, List<String> lines, Location location) {
        this.id = id;
        this.lines = lines;
        this.location = location;
        this.allListener = null;
        this.playerListener = new HashMap<>();
        this.entityArmorStands = createArmorstand(lines);

        Variables.hologramList.put(id, this);
    }

    @Override
    public void send(String playerName) {

        if (!Variables.hologramList.containsKey(this.id) || this.allListener != null) {
            return;
        }

        this.players.add(playerName);

        if (this.playerListener.containsKey(playerName)) {
            PlayerJoinEvent.getHandlerList().unregister(this.playerListener.get(playerName));
        }

        Listener listener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (player.getName().equals(playerName)) {
                    for (EntityArmorStand entityArmorStand : entityArmorStands) {
                        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                        sendPacket(player, spawnPacket, metadataPacket);
                    }
                }
            }
        };
        this.playerListener.put(playerName, listener);
        Bukkit.getPluginManager().registerEvents(listener, HologramAPI.instance);

        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                sendPacket(player, spawnPacket, metadataPacket);
            }
        }
    }

    @Override
    public void sendAll() {

        if (!Variables.hologramList.containsKey(this.id) || this.allListener != null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player.getName());
            for (EntityArmorStand entityArmorStand : entityArmorStands) {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                sendPacket(player, spawnPacket, metadataPacket);
            }
        }
        this.players.clear();
        for (Listener playerListener : this.playerListener.values()) {
            PlayerJoinEvent.getHandlerList().unregister(playerListener);
        }
        this.playerListener.clear();

        this.allListener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                for (EntityArmorStand entityArmorStand : entityArmorStands) {
                    PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                    PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                    sendPacket(player, spawnPacket, metadataPacket);
                }
            }

            @EventHandler
            public void onTeleport(PlayerTeleportEvent event) {
                if (entityArmorStands.size() <= 0) {
                    return;
                }
                EntityArmorStand entityArmorStand = new ArrayList<>(entityArmorStands).get(0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = event.getPlayer();

                        World armorstandWorld = entityArmorStand.getWorld().getWorld();

                        Location from = event.getFrom();
                        Location to = event.getTo();

                        if (!armorstandWorld.equals(from.getWorld()) && armorstandWorld.equals(to.getWorld())) {
                            for (EntityArmorStand entityArmorStand : entityArmorStands) {
                                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
                                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
                                sendPacket(player, spawnPacket, metadataPacket);
                            }
                        }
                    }
                }.runTaskLater(HologramAPI.instance, 2);
            }
        };
        Bukkit.getPluginManager().registerEvents(this.allListener, HologramAPI.instance);
    }

    @Override
    public void remove(String playerName) {

        this.players.remove(playerName);

        Listener listener = this.playerListener.remove(playerName);
        if (listener != null) {
            PlayerJoinEvent.getHandlerList().unregister(listener);
        }

        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
                sendPacket(player, destroyPacket);
            }
        }
    }

    @Override
    public void removeAll() {
        if (this.allListener != null) {
            PlayerJoinEvent.getHandlerList().unregister(this.allListener);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player.getName());
        }
        this.players.clear();
        this.playerListener.clear();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        Variables.hologramList.put(id, this);
        Variables.hologramList.remove(this.id);
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
            if (this.allListener != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendPacket(player, destroyPacket);
                }
            } else {
                sendPacket(players, destroyPacket);
            }
        }

        this.entityArmorStands = createArmorstand(lines);

        for (EntityArmorStand entityArmorStand : this.entityArmorStands) {
            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
            if (this.allListener != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendPacket(player, spawnPacket, metadataPacket);
                }
            } else {
                sendPacket(players, spawnPacket, metadataPacket);
            }
        }
    }

    @Override
    public void setLine(int index, String line) {
        if (this.lines.size() <= index) return;
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
        for (String holoLine : this.lines) {
            if (holoLine.equals(line)) {
                removeLine(index);
                return;
            }
            index++;
        }
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
    public void delete() {
        Variables.hologramList.remove(this.id);
        removeAll();
    }

    private Set<EntityArmorStand> createArmorstand(List<String> lines) {

        if (this.location == null) return new HashSet<>();

        if (lines == null) return new HashSet<>();
        int size = lines.size();
        if (size == 0) return new HashSet<>();

        double startY = this.location.getY() + (((size * 0.24) + ((size - 1) * 0.05)) / 2);

        CraftWorld craftWorld = ((CraftWorld) this.location.getWorld());
        if (craftWorld == null) return new HashSet<>();
        WorldServer worldServer = craftWorld.getHandle();

        Set<EntityArmorStand> entityArmorStands = new HashSet<>();

        for (String line : lines) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, this.location.getX(), startY, this.location.getZ());
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

    private void sendPacket(Set<String> players, Packet... packets) {
        for (String playerName : players) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) continue;
            sendPacket(player, packets);
        }
    }
}