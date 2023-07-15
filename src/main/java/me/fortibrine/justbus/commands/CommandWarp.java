package me.fortibrine.justbus.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import me.fortibrine.justbus.JustBus;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class CommandWarp implements CommandExecutor {

    private JustBus plugin;
    public CommandWarp(JustBus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        FileConfiguration config = plugin.getConfig();

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(plugin.getDescription().getPermissions().get(1))) {
            player.sendMessage(config.getString("messages.permission"));
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        Essentials essentials = plugin.getEssentials();

        if (!essentials.getWarps().getList().contains(args[0])) {
            player.sendMessage(config.getString("messages.unknown"));
            return true;
        }

        Inventory inventory = plugin.generateInventory(args[0]);

        player.closeInventory();

        Map<Player, Inventory> inventories = plugin.getInventories();
        Map<Player, Location> warps = plugin.getWarps();

        inventories.put(player, inventory);

        Location warp;
        try {

            warp = essentials.getWarps().getWarp(args[0]);
        } catch (WarpNotFoundException | InvalidWorldException e) {
            throw new RuntimeException(e);
        }

        if (warp == null) {
            return true;
        }

        warps.put(player, warp);

        player.openInventory(inventory);

        plugin.setInventories(inventories);
        plugin.setWarps(warps);

        return true;
    }
}
