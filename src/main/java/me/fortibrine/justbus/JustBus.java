package me.fortibrine.justbus;

import me.fortibrine.justbus.commands.CommandWarp;
import me.fortibrine.justbus.listeners.Listener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.earth2me.essentials.Essentials;

public class JustBus extends JavaPlugin {

    private Map<Player, Inventory> inventories = new HashMap<>();
    private Map<Player, String> warps = new HashMap<>();
    private Map<Player, Integer> time = new HashMap<>();
    private Map<Player, Location> gps = new HashMap<>();

    private Essentials essentials;

    @Override
    public void onEnable() {

        PluginManager pluginManager = Bukkit.getPluginManager();

//        if (pluginManager.getPlugin("EssentialsX") == null) {
//            pluginManager.disablePlugin(this);
//            return;
//        }

        essentials = (Essentials) pluginManager.getPlugin("Essentials");

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }

        this.getCommand("warp").setExecutor(new CommandWarp(this));

        pluginManager.registerEvents(new Listener(this), this);

        int couldown = 3;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : this.gps.keySet()) {
                Location playerLocation = player.getLocation();
                Location currentLocation = this.gps.get(player);
                String message = this.getConfig().getString("gps")
                        .replace("%x", String.valueOf((int) currentLocation.getX()))
                        .replace("%y", String.valueOf((int) currentLocation.getY()))
                        .replace("%z", String.valueOf((int) currentLocation.getZ()))
                        .replace("%distance", String.valueOf((int) playerLocation.distance(currentLocation)));
                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(message));
            }

            for (Player player : this.time.keySet()) {
                int time = this.time.get(player);

                time -= couldown;

                if (time <= 0) {
                    this.time.remove(player);
                }
            }
        }, couldown * 20L, 0L);

    }

    public Map<Player, Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(Map<Player, Inventory> inventories) {
        this.inventories = inventories;
    }

    public Essentials getEssentials() {
        return this.essentials;
    }

    public Map<Player, String> getWarps() {
        return this.warps;
    }

    public void setWarps(Map<Player, String> warps) {
        this.warps = warps;
    }

    public Map<Player, Integer> getTime() {
        return this.time;
    }

    public void setTime(Map<Player, Integer> time) {
        this.time = time;
    }

    public Map<Player, Location> getGPS() {
        return this.gps;
    }

    public void setGPS(Map<Player, Location> gps) {
        this.gps = gps;
    }

    public Inventory generateInventory(String warpName) {
        FileConfiguration config = this.getConfig();

        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                config.getString("inventory.title")
                        .replace("%warp", warpName));

        ItemStack teleport = new ItemStack(
                Material.matchMaterial(config.getString("inventory.teleport.material")));

        ItemMeta teleportMeta = teleport.getItemMeta();

        List<String> teleportLore = config.getStringList("inventory.teleport.lore");

        teleportLore.replaceAll(s -> s.replace("%warp", warpName));

        teleportMeta.setDisplayName(config.getString("inventory.teleport.name")
                .replace("%warp", warpName));

        teleportMeta.setLore(teleportLore);

        teleport.setItemMeta(teleportMeta);

        ItemStack drive = new ItemStack(
                Material.matchMaterial(config.getString("inventory.drive.material")));

        ItemMeta driveMeta = drive.getItemMeta();

        List<String> driveLore = config.getStringList("inventory.drive.lore");

        driveLore.replaceAll(s -> s.replace("%warp", warpName));

        driveMeta.setDisplayName(config.getString("inventory.drive.name")
                .replace("%warp", warpName));

        driveMeta.setLore(driveLore);

        drive.setItemMeta(driveMeta);

        ItemStack gps = new ItemStack(
                Material.matchMaterial(config.getString("inventory.gps.material")));

        ItemMeta gpsMeta = gps.getItemMeta();

        List<String> gpsLore = config.getStringList("inventory.gps.lore");

        gpsLore.replaceAll(s -> s.replace("%warp", warpName));

        gpsMeta.setDisplayName(config.getString("inventory.gps.name")
                .replace("%warp", warpName));

        gpsMeta.setLore(gpsLore);

        gps.setItemMeta(gpsMeta);

        inventory.setItem(10, teleport);
        inventory.setItem(12, drive);
        inventory.setItem(14, gps);

        return inventory;
    }

}
