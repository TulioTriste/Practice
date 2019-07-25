package me.sebaarkadia.practice.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Color {
	
	public static String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String translate(CommandSender commandSender, String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public List<String> translateFromArray(List<String> text) {
		List<String> messages = new ArrayList<>();

		for (String string : text) {
			messages.add(translate(string));
		}
		return messages;
	}
	
	public static void sendMessage(String message) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			online.sendMessage(Color.translate(message));
		}
	}
	

	public static void sendMessage(String message, String permission) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			
			if(online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		
		}
	}
	

	public static void sendMessageWithoutPlayer(Player player, String message) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(online != player) {
				online.sendMessage(message);
			}
		}
	}
	

	public static void sendMessageWithoutPlayer(Player player, String message, String permission) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			
			if(online != player && online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		
		}
	}
	
}
