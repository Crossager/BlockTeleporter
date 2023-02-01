package net.crossager.blockteleporter.listeners;

import net.crossager.blockteleporter.BlockTeleporter;
import net.crossager.blockteleporter.ConfigData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class GoatHornListener implements Listener {
    private final BlockTeleporter plugin;

    public GoatHornListener(BlockTeleporter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfigData(ConfigData.NO_GOAT_HORN_COOLDOWN, false)) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.GOAT_HORN) return;
        event.getPlayer().setCooldown(Material.GOAT_HORN, 0);
    }
}
