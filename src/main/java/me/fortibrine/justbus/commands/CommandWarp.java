package me.fortibrine.justbus.commands;

import me.fortibrine.justbus.JustBus;
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

        Inventory inventory = plugin.generateInventory(args[0]);

        player.closeInventory();
        player.openInventory(inventory);

        Map<Player, Inventory> inventories = plugin.getInventories();
        Map<Player, String> warps = plugin.getWarps();

        inventories.put(player, inventory);
        warps.put(player, args[0]);

        plugin.setInventories(inventories);
        plugin.setWarps(warps);

        return true;
    }
}
