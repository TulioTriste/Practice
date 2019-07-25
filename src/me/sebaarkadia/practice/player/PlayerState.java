package me.sebaarkadia.practice.player;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;

public enum PlayerState
{
    LOADING("LOADING", 0), 
    LOBBY("LOBBY", 1), 
    QUEUE("QUEUE", 2), 
    WAITING("WAITING", 3), 
    FIGHTING("FIGHTING", 4), 
    EVENT("EVENT", 5), 
    SPECTATING("SPECTATING", 6), 
    BUILDER("BUILDER", 7), 
    EDITING("EDITING", 8);
    
    private PlayerState(final String s, final int n) {
    }

	public static Object FIGHTING(Player player, Practice instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
