package com.hakan.hologram.listeners;

import com.hakan.hologram.Main;
import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                UUID playerUniqueId = player.getUniqueId();
                for (Hologram hologram : HologramAPI.getHolograms()) {
                    for (UUID uuid : hologram.getPlayers()) {
                        if (playerUniqueId.equals(uuid)) {
                            hologram.update();
                            break;
                        }
                    }
                }
            }
        }.runTaskLater(Main.instance, 15);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                UUID playerUniqueId = player.getUniqueId();
                for (Hologram hologram : HologramAPI.getHolograms()) {
                    for (UUID uuid : hologram.getPlayers()) {
                        if (playerUniqueId.equals(uuid)) {
                            hologram.update();
                            break;
                        }
                    }
                }
            }
        }.runTaskLater(Main.instance, 15);
    }
}