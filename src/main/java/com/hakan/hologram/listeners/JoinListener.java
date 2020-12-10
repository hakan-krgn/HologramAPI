package com.hakan.hologram.listeners;

import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();
        for (Hologram hologram : HologramAPI.getHolograms()) {
            for (UUID uuid : hologram.getPlayers()) {
                if (playerUniqueId.equals(uuid)) {
                    hologram.send(player);
                    break;
                }
            }
        }
    }
}