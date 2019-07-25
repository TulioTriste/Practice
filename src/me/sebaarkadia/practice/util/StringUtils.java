package me.sebaarkadia.practice.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StringUtils {

	public static String formatMilisecondsToSeconds(Long time) {
        float seconds = (time + 0.0f) / 1000.0f;
        
        String string = String.format("%1$.1f", seconds);
        
        return string;
	}
	
	public static String formatMilisecondsToMinutes(Long time) {
		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / 1000) / 60);
		
		String string = String.format("%02d:%02d", minutes, seconds);
		
		return string;
	}
	
	public static String formatSecondsToMinutes(int time) {
		int seconds = time % 60;
		int minutes = time / 60;
		
		String string = String.format("%02d:%02d", minutes, seconds);
		
		return string;
	}
	
	public static String formatSecondsToHours(int time) {
		int hours = time / 3600;
		int minutes = (time % 3600) / 60;
		int seconds = time % 60;
		
		String string = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				
		return string;
	}
	
	public static String formatMinutes(int time) {
		int minutes = time / 60;
		
		String string = String.format("%02d", minutes);
		
		return string;
	}
	
	public static String formatInt(int i) {
		int r = i * 1000;
		int sec = r / 1000 % 60;
		int min = r / 60000 % 60;
		int h = r / 3600000 % 24;
		
		return (h > 0 ? h + ":" : "") + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
	}
	
	public static String getEffectNamesList(ArrayList<PotionEffect> effects) {
		StringBuilder names = new StringBuilder();
		for(PotionEffect effect : effects) {
			names.append(getPotionEffectName(effect.getType())).append(", ");
		}
		if(names.length() != 0) {
			names.delete(names.length() - 2, names.length());
		}

		return names.toString();
	}
	
	public static String getPotionEffectName(PotionEffectType type) {
		switch(type.getName()) {
			case "ABSORPTION": return "Absorption";
			case "BLINDNESS": return "Blindness";
			case "CONFUSION": return "Confusion";
			case "DAMAGE_RESISTANCE": return "Resistance";
			case "FAST_DIGGING": return "Haste";
			case "FIRE_RESISTANCE": return "Fire Resistance";
			case "HARM": return "Instant Damage";
			case "HEAL": return "Instant Health";
			case "HEALTH_BOOST": return "Health Boost";
			case "HUNGER": return "Hunger";
			case "INCREASE_DAMAGE": return "Strength";
			case "INVISIBILITY": return "Invisibility";
			case "JUMP": return "Jump";
			case "NIGHT_VISION": return "Night Vision";
			case "POISON": return "Poison";
			case "REGENERATION": return "Regeneration";
			case "SATURATION": return "Saturation";
			case "SLOW": return "Slowness";
			case "SLOW_DIGGING": return "Slow Digging";
			case "SPEED": return "Speed";
			case "WATER_BREATHING": return "Water Breathing";
			case "WEAKNESS": return "Weakness";
			case "WITHER": return "Wither";
		}
		return "";
	}	
	
	public static String getEntityName(Entity entity) {
		switch(entity.getType().name()) {
			case "BLAZE": return "Blaze"; 
			case "CAVE_SPIDER": return "Cave Spider";
			case "CREEPER": return "Creeper";
			case "ENDERMAN": return "Enderman";
			case "IRON_GOLEM": return "Iron Golem";
			case "MAGMA_CUBE": return "Magma Cube";
			case "PIG_ZOMBIE": return "Pig Zombie";
			case "PLAYER": return "Player";
			case "SILVERFISH": return "Silverfish";
			case "SKELETON": return "Skeleton";
			case "SLIME": return "Slime";
			case "SPIDER": return "Spider";
			case "VILLAGER": return "Villager";
			case "WITCH": return "Witch";
			case "WITHER": return "Wither";
			case "WOLF":return "Wolf";
			case "ZOMBIE": return "Zombie";
		}
		return "";
	}
	
	public static String getWorldName(Location location) {
		String worldName = "";
		World world = location.getWorld();
		if(world.getEnvironment().equals(Environment.NORMAL)) {
			worldName = "World";
		} else if(world.getEnvironment().equals(Environment.NETHER)) {
			worldName = "Nether";
		} else if(world.getEnvironment().equals(Environment.THE_END)) {
			worldName = "End";
		} else {
			worldName = world.getName();
		}
		return worldName;
	}
	
	public static String getVanishOptionsList(Player player) {
		StringBuilder builder = new StringBuilder();
		for(OptionType optionType : OptionType.values()) {
			if(optionType.getPlayers().contains(player.getUniqueId())) {
				builder.append(ChatColor.GREEN + optionType.getName()).append(", ");
			} else {
				builder.append(ChatColor.RED + optionType.getName()).append(ChatColor.GRAY + ", ");
			}
		}
		if(builder.length() != 0) {
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
	
	public static Object getTime(int seconds) {
	    if (seconds < 60) {
	    	return seconds + "s";
	    }
	    int minutes = seconds / 60;
	    int s = 60 * minutes;
	    int secondsLeft = seconds - s;
	    if (minutes < 60) {
	    	if (secondsLeft > 0) {
	    		return String.valueOf(minutes + "m " + secondsLeft + "s");
	    	}
	    	return String.valueOf(minutes + "m");
	    }
	    if (minutes < 1440) {
	    	String time = "";
	        int hours = minutes / 60;
	        time = hours + "h";
	        int inMins = 60 * hours;
	        int leftOver = minutes - inMins;
	        if (leftOver >= 1) {
	        	time = time + " " + leftOver + "m";
	        }
	        if (secondsLeft > 0) {
	        	time = time + " " + secondsLeft + "s";
	        }
	        return time;
	    }
	    String time = "";
	    int days = minutes / 1440;
	    time = days + "d";
	    int inMins = 1440 * days;
	    int leftOver = minutes - inMins;
	    if (leftOver >= 1) {
	    	if (leftOver < 60) {
	    		time = time + " " + leftOver + "m";
	    	} else {
	    		int hours = leftOver / 60;
	            time = time + " " + hours + "h";
     	        int hoursInMins = 60 * hours;
	            int minsLeft = leftOver - hoursInMins;
	            if (leftOver >= 1) {
	            	time = time + " " + minsLeft + "m";
	            }
	    	}
	    }
	    if (secondsLeft > 0) {
	    	time = time + " " + secondsLeft + "s";
	    }
	    return time;
	}
}

