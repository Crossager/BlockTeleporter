package net.crossager.blockteleporter;

import net.crossager.blockteleporter.listeners.BlockTeleportListener;
import net.crossager.blockteleporter.listeners.GoatHornListener;
import net.crossager.blockteleporter.listeners.PlayerDataListener;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class BlockTeleporter extends JavaPlugin {
    private List<BlockTeleport> blockTeleports;
    private final Map<Player, PlayerData> playerData = new HashMap<>();
    private YamlConfiguration dataConfiguration;
    private File dataFile;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        blockTeleports = new ArrayList<>();
        dataFile = new File(getDataFolder(), "data.yml");
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        dataConfiguration.getKeys(false).forEach(key -> {
            try {
                blockTeleports.add(new BlockTeleport(
                        key,
                        dataConfiguration.getList(key + ".blocklocations").stream().map(Location.class::cast).collect(Collectors.toList()),
                        dataConfiguration.getLocation(key + ".teleportlocation")
                        ));
            } catch (NullPointerException e) {
                getLogger().warning(String.format("Malformed config file, skipping key '%s'", key));
            }
        });
        new BlockTeleportCommand(this);
        getServer().getPluginManager().registerEvents(new BlockTeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new GoatHornListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(playerData), this);
    }

    @Override
    public void onDisable() {
        try {
            blockTeleports.forEach(blockTeleport -> {
                dataConfiguration.set(blockTeleport.name() + ".teleportlocation", blockTeleport.teleportLocation());
                dataConfiguration.set(blockTeleport.name() + ".blocklocations", blockTeleport.blockLocations());
            });
            dataConfiguration.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BlockTeleport> blockTeleports() {
        return blockTeleports;
    }

    public Optional<BlockTeleport> getBlockTeleport(String name) {
        return blockTeleports.stream().filter(blockTeleport -> blockTeleport.name().equalsIgnoreCase(name)).findAny();
    }

    public PlayerData getPlayerData(Player player) {
        return playerData.entrySet().stream().filter(entry -> entry.getKey().equals(player)).findAny().map(Map.Entry::getValue).orElseGet(() -> {
            PlayerData data = new PlayerData(this);
            playerData.put(player, data);
            return data;
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfigData(ConfigData data, T def) {
        return (T) getConfig().get(data.name().toLowerCase().replaceAll("_", "-"), def);
    }

    public String formatMessage(String message, Object... args) {
        return "§e[BlockTeleporter] §a" + String.format(message, args);
    }
}
