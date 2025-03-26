package me.snake.heightLimiter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class HeightLimiter extends JavaPlugin implements Listener {

    private int maxHeight;
    private String restrictionMessage;
    private List<String> restrictedWorlds;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Create config.yml if it doesn’t exist
        loadConfigValues(); // Load values from config

        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("HeightLimiter has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HeightLimiter has been disabled!");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        maxHeight = config.getInt("max-height", 318); // Default 318
        restrictionMessage = config.getString("restriction-message", "§cYou cannot go above Y=%height%!").replace("%height%", String.valueOf(maxHeight));
        restrictedWorlds = config.getStringList("restricted-worlds"); // List of worlds
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        World world = loc.getWorld();

        // Allow bypass if player has permission
        if (player.hasPermission("heightlimiter.bypass")) {
            return;
        }

        // Check if world is restricted and if height exceeds maxHeight
        if (restrictedWorlds.contains(world.getName()) && loc.getY() > maxHeight) {
            Location safeLocation = new Location(world, loc.getX(), maxHeight, loc.getZ(), loc.getYaw(), loc.getPitch());
            player.teleport(safeLocation);
            player.sendMessage(restrictionMessage.replace("%height%", String.valueOf(maxHeight)));
        }
    }
}