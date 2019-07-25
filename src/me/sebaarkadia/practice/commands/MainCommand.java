package me.sebaarkadia.practice.commands;

import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.LocationSerializer;

import org.bukkit.command.CommandExecutor;

public class MainCommand implements CommandExecutor, Listener
{
    private Practice plugin;
    
    public MainCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String msg, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("practice") && args.length == 0) {
                p.sendMessage("§eWelcome to §6§ljPractice §ePlugin");
                if (p.isOp()) {
                    p.sendMessage(" §7- §6/practice setspawn §7- §eSet the spawnpoint of the server");
                    p.sendMessage(" §7- §6/practice seteditkit §7- §eSet the spawnpoint of the editkit");
                    p.sendMessage(" §7- §6/practice setnpc §7- §eSet custom npc for editing kit");
                    p.sendMessage("");
                }
            }
            else if (args[0].contains("setspawn")) {
                if (p.isOp()) {
                    this.plugin.getConfig().set("spawn", LocationSerializer.serializeLocation(p.getLocation()));
                    this.plugin.saveConfig();
                    this.plugin.reloadConfig();
                    this.plugin.setSpawn(p.getLocation());
                    p.sendMessage(ChatColor.GREEN + "Spawn is now set.");
                }
            }
            else if (args[0].contains("seteditkit")) {
                if (p.isOp()) {
                    this.plugin.getConfig().set("editkit", LocationSerializer.serializeLocation(p.getLocation()));
                    this.plugin.saveConfig();
                    this.plugin.reloadConfig();
                    this.plugin.seteditkit(p.getLocation());
                    p.sendMessage(ChatColor.GREEN + "Editkit is now set.");
                }
            }
            else if (args[0].contains("setnpc") && p.isOp()) {
                final File file = new File(Practice.getInstance().getDataFolder(), "NpcLoc.yml");
                final YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
                final String world = p.getWorld().getName();
                final double x = p.getLocation().getX();
                final double y = p.getLocation().getY();
                final double z = p.getLocation().getZ();
                c.set("x", x);
                c.set("y", y);
                c.set("z", z);
                c.set("world", world);
                try {
                    p.sendMessage(ChatColor.GREEN + "Editkit NPC is now set.");
                    c.save(file);
                }
                catch (IOException e) {
                    p.sendMessage("§cError while saving the Location!");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
