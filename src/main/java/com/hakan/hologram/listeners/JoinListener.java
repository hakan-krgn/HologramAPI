package com.hakan.hologram.listeners;

import com.hakan.hologram.Main;
import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                UUID playerUniqueId = player.getUniqueId();
                Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                    for (Hologram hologram : HologramAPI.getHolograms()) {
                        for (UUID uuid : hologram.getPlayers()) {
                            if (playerUniqueId.equals(uuid)) {
                                hologram.send(player);
                                break;
                            }
                        }
                    }
                });
            }
        }.runTaskLater(Main.instance, 15);
    }
}