package me.sebaarkadia.practice.commands;

import org.bukkit.inventory.meta.ItemMeta;

import me.sebaarkadia.practice.util.Color;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class RenameCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cYou must be player to execute this command."));
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.command.rename")) {
            player.sendMessage(Color.translate("&cYou do not have permissions!"));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <newName>"));
        }
        else {
            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                player.sendMessage(Color.translate("&cYou must have anything in your hand."));
                return true;
            }
            String name = "";
            for (int r = 0; r < args.length; ++r) {
                name = name + args[r] + " ";
            }
            final ItemStack item = player.getItemInHand();
            final ItemMeta meta = player.getItemInHand().getItemMeta();
            if (player.getItemInHand().getItemMeta().getDisplayName() == null) {
                player.sendMessage(Color.translate("&eYou have successfully renamed from &bno name &eto " + name + '.'));
            }
            else {
                player.sendMessage(Color.translate("&eYou have successfully rename from &b" + player.getItemInHand().getItemMeta().getDisplayName() + " &eto " + name));
            }
            if (player.isOp()) {
                meta.setDisplayName(Color.translate(name));
                item.setItemMeta(meta);
            }
            else {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
        }
        return true;
    }
}
