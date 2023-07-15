package me.fortibrine.justbus.listeners;

import com.earth2me.essentials.commands.WarpNotFoundException;
import me.fortibrine.justbus.JustBus;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public class Listener implements org.bukkit.event.Listener {

    private JustBus plugin;
    public Listener(JustBus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws WarpNotFoundException, InvalidWorldException {
        FileConfiguration config = plugin.getConfig();

        Player player = (Player) event.getWhoClicked();
        Map<Player, Inventory> inventories = plugin.getInventories();
        Map<Player, Location> warps = plugin.getWarps();
        Map<Player, Integer> time = plugin.getTime();
        List<Player> gps = plugin.getGPS();

        Inventory inventory = inventories.get(player);

        if (event.getClickedInventory() != inventory) return;

        event.setCancelled(true);

        if (inventories.containsKey(player)) {
            inventories.remove(player);
            plugin.setInventories(inventories);
        }

        player.closeInventory();

        switch (event.getSlot()) {
            case (10):

                if (!player.hasPermission(plugin.getDescription().getPermissions().get(2))) {
                    player.sendMessage(config.getString("messages.permission"));
                    return;
                }

                player.teleport(warps.get(player));
                player.sendMessage(config.getString("messages.teleport"));

                warps.remove(player);

                break;
            case (13):

                if (!player.hasPermission(plugin.getDescription().getPermissions().get(3))) {
                    player.sendMessage(config.getString("messages.permission"));
                    return;
                }

                int seconds = (int) player.getLocation().distance(warps.get(player)) * 3;

                time.put(player, seconds);

                plugin.setTime(time);

                Location bus = new Location(
                        Bukkit.getWorld(config.getString("bus.world")),
                        config.getDouble("bus.x"),
                        config.getDouble("bus.y"),
                        config.getDouble("bus.z"));

                player.teleport(bus);

                break;
            case (16):

                if (!player.hasPermission(plugin.getDescription().getPermissions().get(4))) {
                    player.sendMessage(config.getString("messages.permission"));
                    return;
                }

               gps.add(player);

                break;
        }

        plugin.setTime(time);
        plugin.setWarps(warps);
        plugin.setGPS(gps);
    }

    @EventHandler
    public void onSwap(InventoryMoveItemEvent event) {
        Player player = (Player) event.getInitiator().getViewers().get(0);
        Map<Player, Inventory> inventories = plugin.getInventories();

        Inventory inventory = inventories.get(player);

        if (event.getSource() != inventory) return;
        if (event.getDestination() != inventory) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Map<Player, Inventory> inventories = plugin.getInventories();

        Inventory inventory = inventories.get(player);

        if (event.getInventory() != inventory) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onServerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Map<Player, Inventory> inventories = plugin.getInventories();
        Map<Player, Location> warps = plugin.getWarps();
        Map<Player, Integer> time = plugin.getTime();
        List<Player> gps = plugin.getGPS();

        if (inventories.containsKey(player)) {
            inventories.remove(player);
            plugin.setInventories(inventories);
        }
        if (warps.containsKey(player)) {
            warps.remove(player);
            plugin.setWarps(warps);
        }
        if (time.containsKey(player)) {
            time.remove(player);
            plugin.setTime(time);
        }
        if (gps.contains(player)) {
            gps.remove(player);
            plugin.setGPS(gps);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        Map<Player, Inventory> inventories = plugin.getInventories();
//        Map<Player, String> warps = plugin.getWarps();

        if (inventories.containsKey(player)) {
            inventories.remove(player);
            plugin.setInventories(inventories);
        }
//        if (warps.containsKey(player)) {
//            warps.remove(player);
//            plugin.setWarps(warps);
//        }
    }

}
