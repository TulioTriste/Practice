package me.sebaarkadia.practice.commands;

import java.util.UUID;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerInventorySnapshot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class InventoryCommand implements CommandExecutor
{
    private Practice plugin;
    
    public InventoryCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        if (!args[0].matches(Practice.UUID_PATTER.pattern())) {
            player.sendMessage("§cCannot find the requested inventory. Maybe it expired?");
            return true;
        }
        final UUID invUUID = UUID.fromString(args[0]);
        final PlayerInventorySnapshot playerInventorySnapshot = this.plugin.getManagerHandler().getInventorySnapshotManager().getSnapshotFromUUID(invUUID);
        if (playerInventorySnapshot == null) {
            player.sendMessage("§cCannot find the requested inventory. Maybe it expired?");
            return true;
        }
        player.openInventory(playerInventorySnapshot.getInventory());
        return true;
    }
}
