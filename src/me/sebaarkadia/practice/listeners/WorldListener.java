package me.sebaarkadia.practice.listeners;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

	public void onRain(WeatherChangeEvent event) {
		event.setCancelled(true);;
	}
	
	public void AlwaysDay(World event) {
		event.setFullTime(8000);
	}
}
