package net.crossager.blockteleporter;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerData {
    private final BlockTeleporter plugin;
    private BlockTeleport currentlyEditing = null;
    private final Map<Location, Long> lastPressed = new HashMap<>();

    public PlayerData(BlockTeleporter plugin) {
        this.plugin = plugin;
    }

    public void clickBlock(Location location) {
        lastPressed.put(location, System.currentTimeMillis());
        System.out.println(lastPressed);
    }

    public boolean hasClicked(List<Location> locations) {
        return locations.stream().allMatch(location -> {
            Long l = lastPressed.get(location);
            if (l == null) return false;
            if (l > System.currentTimeMillis() - (plugin.getConfigData(ConfigData.BLOCK_COOLDOWN, 20) * 1000))
                return true;
            lastPressed.remove(location);
            return false;
        });
    }

    public Optional<BlockTeleport> currentlyEditing() {
        return Optional.ofNullable(currentlyEditing);
    }

    public void setCurrentlyEditing(BlockTeleport currentlyEditing) {
        this.currentlyEditing = currentlyEditing;
    }

    public void removeClicks(List<Location> locations) {
        locations.forEach(lastPressed::remove);
    }
}
