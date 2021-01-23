package com.hakan.hologram.listeners;

import com.hakan.hologram.Main;
import com.hakan.hologram.api.HologramAPI;
import com.hakan.hologram.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                List<Hologram> holograms = HologramAPI.getHolograms(player);
                if (holograms != null && holograms.size() != 0) {
                    for (Hologram hologram : holograms) {
                        hologram.removePlayer(player.getName());
                        hologram.addPlayer(player.getName());
                    }
                }
            }
        }.runTaskLater(Main.instance, 15);
    }
}