package net.crossager.blockteleporter.listeners;

import net.crossager.blockteleporter.BlockTeleport;
import net.crossager.blockteleporter.BlockTeleporter;
import net.crossager.blockteleporter.ConfigData;
import net.crossager.blockteleporter.PlayerData;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class BlockTeleportListener implements Listener {
    private final BlockTeleporter plugin;

    public BlockTeleportListener(BlockTeleporter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Optional<BlockTeleport> blockTeleport = plugin.getPlayerData(event.getPlayer()).currentlyEditing();
        if (blockTeleport.isPresent()) {
            if (blockTeleport.get().blockLocations().remove(event.getClickedBlock().getLocation())) {
                event.getPlayer().sendMessage(plugin.formatMessage(
                        "Removed block location §e%s§a for §e%s",
                        formatLocation(event.getClickedBlock().getLocation()),
                        blockTeleport.get().name()
                ));
                return;
            }
            event.getPlayer().sendMessage(plugin.formatMessage(
                    "Added block location §e%s§a for §6%s",
                    formatLocation(event.getClickedBlock().getLocation()),
                    blockTeleport.get().name()
            ));
            blockTeleport.get().blockLocations().add(event.getClickedBlock().getLocation());
            return;
        }
        PlayerData playerData = plugin.getPlayerData(event.getPlayer());
        playerData.clickBlock(event.getClickedBlock().getLocation());

        for (BlockTeleport teleport : plugin.blockTeleports()) {
            if (!teleport.blockLocations().contains(event.getClickedBlock().getLocation())) continue;
            if (playerData.hasClicked(teleport.blockLocations())) {
                event.getPlayer().teleport(teleport.teleportLocation());
                event.getPlayer().sendMessage("§aTeleported to §e" + teleport.name());
                playerData.removeClicks(teleport.blockLocations());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigData(ConfigData.REMOVE_BLOCK_LOCATION_ON_BREAK, true)) return;
        Optional<BlockTeleport> blockTeleport = plugin.getPlayerData(event.getPlayer()).currentlyEditing();
        if (!blockTeleport.isPresent()) return;
        if (blockTeleport.get().blockLocations().remove(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage(plugin.formatMessage(
                    "Removed block location §e%s§a for §e%s",
                    formatLocation(event.getBlock().getLocation()),
                    blockTeleport.get().name()
            ));
        }
    }

    private String formatLocation(Location location) {
        return String.format("x: %s, y: %s, z: %s", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
