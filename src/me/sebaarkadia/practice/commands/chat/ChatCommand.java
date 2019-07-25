package me.sebaarkadia.practice.commands.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.Color;

public class ChatCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (args.length == 0) {
			this.sendUsage(sender);
		} else if (args.length == 1) {
			if (args[0].equals("mute")) {
				if (sender.hasPermission("chat.commands")) {
					if (!Practice.getInstance().getChatControlHandler().isMuted()) {
						Practice.getInstance().getChatControlHandler().setMuted(true);
						Bukkit.broadcastMessage(Color.translate("&8[&6&lChat&8] &7Chat has been muted by &e&l" + sender.getName() + "&7."));
					} else {
						sender.sendMessage(Color.translate("&cChat is already muted."));
					}
				} else {
					sender.sendMessage(Color.translate("&cNo permissions."));
				}
			} else if (args[0].equals("unmute")) {
				if (sender.hasPermission("chat.commands")) {
					if (Practice.getInstance().getChatControlHandler().isMuted()) {
						Practice.getInstance().getChatControlHandler().setMuted(false);
						Bukkit.broadcastMessage(Color.translate("&8[&6&lChat&8] &7Chat has been unmuted by &e&l" + sender.getName() + "&7."));
					} else {
						sender.sendMessage(Color.translate("&cChat is not muted."));
					}
				} else {
					sender.sendMessage(Color.translate("&cNo permissions."));
				}
			} else if (args[0].equals("clear")) {
				if (sender.hasPermission("chat.commands")) {
					for (int i = 0; i < 100; i++) {
						Bukkit.broadcastMessage("");
					}
					Bukkit.broadcastMessage(Color.translate("&8[&6&lChat&8] &7Chat has been cleared by &e&l" + sender.getName() + "&7."));
				} else {
					sender.sendMessage(Color.translate("&cNo permissions."));
				}
			} else {
				this.sendUsage(sender);
			}
		} else if (args.length == 2) {
			if (args[0].equals("delay")) {
				if (sender.hasPermission("chat.commands")) {
					if (NumberUtils.isInteger(args[1])) {
						int delay = Math.abs(Integer.valueOf(args[1]));
						Practice.getInstance().getChatControlHandler().setDelay(delay);
						Bukkit.broadcastMessage(Color.translate("&8[&6&lChat&8] &7Chat has been slowed to &e&l" + delay + "&7."));
					} else {
						sender.sendMessage(Color.translate("&cPlease use a valid Number!"));
					}
				} else {
					sender.sendMessage(Color.translate("&cNo permissions."));
				}
			}
		} else {
			this.sendUsage(sender);
		}
		return false;
	}

	public void sendUsage(CommandSender sender) {
		sender.sendMessage(Color.translate("&7&m--------------------------------------------"));
		sender.sendMessage(Color.translate("&6Chat Control &7- &eHelp Commands"));
		sender.sendMessage(Color.translate(" &e* &e/chat mute &7- &fMute chat."));
		sender.sendMessage(Color.translate(" &e* &e/chat unmute &7- &fUnmute Chat."));
		sender.sendMessage(Color.translate(" &e* &e/chat clear &7- &fClear chat."));
		sender.sendMessage(Color.translate(" &e* &e/chat delay <time> &7- &fDelay chat."));
		sender.sendMessage(Color.translate("&7&m--------------------------------------------"));
	}

    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("mute", "unmute", "clear", "delay");
        }
        return Collections.emptyList();
    }
}










/*package me.iNotLazo.Lobby.commands.chat;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.command.TabCompleter;

import me.iNotLazo.Lobby.utils.Lang;

import org.bukkit.command.CommandExecutor;

public class ChatCommand implements CommandExecutor, TabCompleter
{
    public static boolean muted;
    public static Map<UUID, Long> nextChat;
    public static int slowTime;
    
    static {
        ChatCommand.nextChat = new HashMap<UUID, Long>();
        ChatCommand.slowTime = 0;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!sender.hasPermission("uPractice.commands.chat")) {
            sender.sendMessage(Lang.getString("NO_PERMISSION"));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                for (int i = 0; i < 105; ++i) {
                    Bukkit.broadcastMessage("");
                }
                Bukkit.broadcastMessage(Lang.getString("CHAT_CLEARED"));
                return true;
            }
            if (args[0].equalsIgnoreCase("mute")) {
                if (!ChatCommand.muted) {
                    ChatCommand.muted = true;
                    Bukkit.broadcastMessage(Lang.getString("CHAT_MUTE_ENABLED"));
                }
                else {
                    ChatCommand.muted = false;
                    Bukkit.broadcastMessage(Lang.getString("CHAT_MUTE_DISABLED"));
                }
            }
            else {
                if (args[0].equalsIgnoreCase("slow")) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " slow <time>");
                    return true;
                }
                this.showHelp(sender, label);
                return true;
            }
        }
        else {
            if (args.length != 2) {
                this.showHelp(sender, label);
                return true;
            }
            if (args[0].equalsIgnoreCase("slow")) {
                if (Ints.tryParse(args[1]) == null) {
                    sender.sendMessage(Lang.getString("INVALID_NUMBER"));
                    return true;
                }
                final int i = Integer.parseInt(args[1]);
                if (i == 0) {
                    Bukkit.broadcastMessage(Lang.getString("CHAT_SLOW_DISABLED"));
                    ChatCommand.slowTime = 0;
                    return true;
                }
                Bukkit.broadcastMessage(Lang.getString("CHAT_SLOW_ENABLED").replace("%seconds%", String.valueOf(i)));
                ChatCommand.slowTime = i;
            }
        }
        return false;
    }
    
    public void showHelp(final CommandSender p, final String label) {
        p.sendMessage(String.valueOf(ChatColor.BLUE.toString()) + ChatColor.BOLD + "Chat Help" + ChatColor.GRAY + ":");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " clear " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Clear the chat.");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " mute " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Mute or unmute the chat.");
        p.sendMessage(ChatColor.YELLOW + " /" + label + " clear " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Slow down the chat.");
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("clear", "mute", "slow");
        }
        return Collections.emptyList();
    }
}*/
