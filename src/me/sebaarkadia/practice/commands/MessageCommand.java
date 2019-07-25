package me.sebaarkadia.practice.commands;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.Color;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.command.CommandExecutor;

public class MessageCommand implements CommandExecutor
{
    private Map<String, String> lastMessage;
    
    public MessageCommand() {
        this.lastMessage = Practice.getInstance().getLastMessage();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cYou must be player to execute this command."));
        }
        final Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("message") || cmd.getName().equalsIgnoreCase("msg") || cmd.getName().equalsIgnoreCase("tell") || cmd.getName().equalsIgnoreCase("whisp") || cmd.getName().equalsIgnoreCase("w")) {
            if (args.length <= 1) {
                player.sendMessage(Color.translate("&cSend a private message to other players."));
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <text..>"));
            }
            else if (args.length >= 2) {
                final Player target = Practice.getInstance().getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Color.translate("&cPlayer '&7" + args[0] + "&c' not found."));
                }
                else {
                    String message = "";
                    for (int i = 1; i < args.length; ++i) {
                        message = String.valueOf(message) + args[i] + " ";
                    }
                    player.sendMessage(Color.translate("&9(To &a" + target.getDisplayName() + "&9) " + "&7" + message));
                    target.sendMessage(Color.translate("&9(From &a" + player.getDisplayName() + "&9) " + "&7" + message));
                    this.lastMessage.put(player.getName(), target.getName());
                    this.lastMessage.put(target.getName(), player.getName());
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("reply") || cmd.getName().equalsIgnoreCase("r")) {
            if (args.length < 1) {
                player.sendMessage(Color.translate("&cReply to the latest message received or sent."));
                player.sendMessage(Color.translate("&cUsage: /" + label + " <text..>"));
            }
            else if (args.length >= 1) {
                if (this.lastMessage.containsKey(player.getName())) {
                    final Player target = Practice.getInstance().getServer().getPlayer(this.lastMessage.get(player.getName()));
                    if (target == null) {
                        player.sendMessage(Color.translate("&cCould not find anyone to reply to."));
                    }
                    else {
                        String message = "";
                        for (int i = 0; i < args.length; ++i) {
                            message = String.valueOf(message) + args[i] + " ";
                        }
                        player.sendMessage(Color.translate("&9(To &r" + target.getDisplayName() + "&9) " + "&7" + message));
                        target.sendMessage(Color.translate("&9(From &r" + player.getDisplayName() + "&9) " + "&7" + message));
                        this.lastMessage.put(player.getName(), target.getName());
                        this.lastMessage.put(target.getName(), player.getName());
                    }
                }
                else {
                    player.sendMessage(Color.translate("&cCould not find anyone to reply to."));
                }
            }
        }
        return true;
    }
}
