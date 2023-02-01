package net.crossager.blockteleporter.listeners;

import net.crossager.blockteleporter.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerDataListener implements Listener {
    private final Map<Player, PlayerData> playerData;

    public PlayerDataListener(Map<Player, PlayerData> playerData) {
        this.playerData = playerData;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerData.remove(event.getPlayer());
    }
}
