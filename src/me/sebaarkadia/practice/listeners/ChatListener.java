package me.sebaarkadia.practice.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.deluxetags.DeluxeTag;
import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.Color;
import net.mineaus.lunarapi.LunarClientAPI;

import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    public boolean isLunarClient(Player player) {
        return LunarClientAPI.getApi().isAuthenticated(player);
    }
	
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        final String message = event.getMessage();
        if (Practice.getInstance().getConfig().getBoolean("enabled_chat_format") && !Practice.getInstance().getChatControlHandler().isMuted()) {
        	//.replace("%tags%", DeluxeTag.getPlayerDisplayTag(player))
            event.setFormat(Color.translate(Practice.getInstance().getConfig().getString("chat_format"))
            		.replace("%lunar%", Color.translate(this.isLunarClient(player) ? " §9§lＬ§b§lＣ §r" : ""))
            		.replace("%player%", player.getName())
            		.replace("%message%", message)
            		.replace("%prefix%", Color.translate(Practice.getInstance().chat.getPlayerPrefix(player))
            		.replace("_", " "))
            		.replace("%star1%", "\u2724")
            		.replace("%star2%", "\u273B")
            		.replace("%star3%", "\u2739")
            		.replace("%star3%", "\u273A")
            		.replace("%star4%", "\u2725")
            		.replace("%star5%", "\u2735"));
        }
        else if (Practice.getInstance().getChatControlHandler().isMuted() && !player.hasPermission("practice.chat")) {
        	event.setCancelled(true);
        }
        else if (Practice.getInstance().getChatControlHandler().isMuted() && player.hasPermission("practice.chat") && Practice.getInstance().getConfig().getBoolean("enabled_chat_format")) {
        	event.setFormat(Color.translate(Practice.getInstance().getConfig().getString("chat_format"))
        			.replace("%lunar%", Color.translate(this.isLunarClient(player) ? " §9§lＬ§b§lＣ §r" : ""))
        			.replace("%player%", player.getName())
        			.replace("%message%", message)
            		.replace("%prefix%", Color.translate(Practice.getInstance().chat.getPlayerPrefix(player))
        			.replace("_", " "))
        			.replace("%star1%", "\u2724")
        			.replace("%star2%", "\u273B")
        			.replace("%star3%", "\u2739")
        			.replace("%star3%", "\u273A")
        			.replace("%star4%", "\u2725")
        			.replace("%star5%", "\u2735"));
            
        }
    }
}

