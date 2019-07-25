package me.sebaarkadia.practice.commands.chat;

public class NumberUtils {

	public static boolean isInteger(String value) {
	    try {
	        Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    return true;
	}
}
