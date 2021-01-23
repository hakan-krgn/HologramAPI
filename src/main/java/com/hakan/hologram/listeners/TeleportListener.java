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

public class TeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                Hologram hologram = HologramAPI.getHologram(player);
                if (hologram != null) {
                    hologram.setVisible(hologram.getLocation().getWorld().equals(player.getWorld()));
                }
            }
        }.runTaskLater(Main.instance, 15);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                Hologram hologram = HologramAPI.getHologram(player);
                if (hologram != null) {
                    hologram.setVisible(hologram.getLocation().getWorld().equals(player.getWorld()));
                }
            }
        }.runTaskLater(Main.instance, 15);
    }
}