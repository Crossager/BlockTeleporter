package net.crossager.blockteleporter;

import org.bukkit.Location;

import java.util.List;

public class BlockTeleport {
    private String name;
    private final List<Location> blockLocations;
    private Location teleportLocation;

    public BlockTeleport(String name, List<Location> blockLocations, Location teleportLocation) {
        this.name = name;
        this.blockLocations = blockLocations;
        this.teleportLocation = teleportLocation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public String name() {
        return name;
    }

    public List<Location> blockLocations() {
        return blockLocations;
    }

    public Location teleportLocation() {
        return teleportLocation;
    }
}
