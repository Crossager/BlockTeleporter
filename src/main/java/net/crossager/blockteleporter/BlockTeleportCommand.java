package net.crossager.blockteleporter;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockTeleportCommand implements CommandExecutor, TabCompleter {
    private final BlockTeleporter plugin;

    public BlockTeleportCommand(BlockTeleporter plugin) {
        this.plugin = plugin;
        plugin.getCommand("blockteleport").setExecutor(this);
        plugin.getCommand("blockteleport").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Command only available for players");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("§4Too few arguments");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop-editing")) {
            plugin.getPlayerData(player).setCurrentlyEditing(null);
            sender.sendMessage(plugin.formatMessage("Stopped editing!"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§4Too few arguments");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (plugin.getBlockTeleport(args[1]).isPresent()) {
                sender.sendMessage(String.format("§4%s already exists", args[1]));
                return true;
            }
            plugin.blockTeleports().add(new BlockTeleport(args[1], new ArrayList<>(), player.getLocation()));
            sender.sendMessage(plugin.formatMessage("Created block teleport §e%s", args[1]));
            return true;
        }
        Optional<BlockTeleport> blockTeleport = plugin.getBlockTeleport(args[1]);
        if (!blockTeleport.isPresent()) {
            sender.sendMessage(String.format("§4Invalid blockteleport \"%s\"", args[1]));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "edit-block-locations":
                sender.sendMessage(plugin.formatMessage("You are now editing §e%s", blockTeleport.get().name()));
                plugin.getPlayerData(player).setCurrentlyEditing(blockTeleport.get());
                break;
            case "remove":
                sender.sendMessage(plugin.formatMessage("Removed §e%s", blockTeleport.get().name()));
                plugin.blockTeleports().remove(blockTeleport.get());
                break;
            case "set-tp-location":
                sender.sendMessage(plugin.formatMessage("Set teleport location for §e%s§a to §e%s§a in world §e%s", blockTeleport.get().name(), formatLocation(player.getLocation()), player.getLocation()));
                blockTeleport.get().setTeleportLocation(player.getLocation());
                break;
            case "rename":
                if (args.length < 3) {
                    sender.sendMessage("§4Too few arguments");
                    return true;
                }
                if (plugin.getBlockTeleport(args[2]).isPresent()) {
                    sender.sendMessage(String.format("§4%s already exists", args[2]));
                    return true;
                }
                sender.sendMessage(plugin.formatMessage("Changed name of §e%s§a to §e%s", blockTeleport.get().name(), args[2]));
                blockTeleport.get().setName(args[2]);
                break;
            case "info":
                sender.sendMessage(plugin.formatMessage("§e%s§a teleports to §e%s§a and has block locations at:", blockTeleport.get().name(), formatLocation(blockTeleport.get().teleportLocation())));
                for (Location blockLocation : blockTeleport.get().blockLocations()) {
                    sender.sendMessage("§a - §e" + formatLocation(blockLocation));
                }
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("stop-editing", "edit-block-locations", "create", "set-tp-location", "rename", "info", "remove").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("stop-editing") || args[0].equalsIgnoreCase("create")) return Collections.emptyList();
            return plugin.blockTeleports().stream().map(BlockTeleport::name).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String formatLocation(Location location) {
        return String.format("x: %s, y: %s, z: %s", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
