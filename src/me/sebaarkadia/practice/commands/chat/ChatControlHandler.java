package me.sebaarkadia.practice.commands.chat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.StringUtils;

public class ChatControlHandler implements Listener {

	private boolean muted;
	private int delay;
	private HashMap<UUID, Long> chatCooldowns;
	
	public ChatControlHandler(Practice plugin) {
		this.muted = false;
		this.delay = 0;
		
		this.chatCooldowns = new HashMap<UUID, Long>();
	}
	
	public void enable() {
		Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
	}
	
	public void disable() {
		this.chatCooldowns.clear();
	}
	
	public void applyCooldown(Player player) {
    	this.chatCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (this.delay * 1000));
    }
	
	public boolean isActive(Player player) {
        return this.chatCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() < this.chatCooldowns.get(player.getUniqueId());
    }
	
	public long getMillisecondsLeft(Player player) {
	    if(this.chatCooldowns.containsKey(player.getUniqueId())) {
	    	return Math.max(this.chatCooldowns.get(player.getUniqueId()) - System.currentTimeMillis(), 0L);
	    }
	    return 0L;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public HashMap<UUID, Long> getChatCooldowns() {
		return chatCooldowns;
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(this.muted) {
			if(!player.hasPermission("chat.bypass")) {
				event.setCancelled(true);
				player.sendMessage(Color.translate("&cChat is currently muted."));
			}
		}
		
		if(this.delay > 0) {
			if(!this.muted) {
				if(!player.hasPermission("chat.bypass")) {
					if(this.isActive(player)) {
						event.setCancelled(true);
						player.sendMessage(Color.translate("&cYou cannot use chat for another &l" + StringUtils.formatMilisecondsToSeconds(this.getMillisecondsLeft(player)) + "&c."));
					} else {
						this.applyCooldown(player);
					}
				}
			}
		}
	}
}
