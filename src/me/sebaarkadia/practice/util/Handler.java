package me.sebaarkadia.practice.util;

import me.sebaarkadia.practice.Practice;

public class Handler {

	private Practice instance;
    
    public Handler(Practice instance) {
        this.instance = instance;
    }
    
    public void enable() { }
    
    public void disable() { }
    
    public Practice getInstance() {
        return this.instance;
	}
}
