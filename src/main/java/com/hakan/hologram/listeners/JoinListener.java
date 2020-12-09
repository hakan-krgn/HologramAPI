package com.hakan.hologram.listeners;

import com.hakan.hologram.hologram.Hologram;
import com.hakan.hologram.utils.Variables;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();
        for (Map.Entry<String, Hologram> entry : Variables.holograms.entrySet()) {
            Hologram hologram = entry.getValue();
            for (UUID uuid : hologram.getPlayers()) {
                if (playerUniqueId.equals(uuid)) {
                    hologram.send(player);
                    break;
                }
            }
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        
    }
}