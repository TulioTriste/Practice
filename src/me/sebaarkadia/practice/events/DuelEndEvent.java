package me.sebaarkadia.practice.events;

import org.bukkit.event.HandlerList;

import me.sebaarkadia.practice.duel.Duel;

import org.bukkit.event.Event;

public class DuelEndEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    static {
        DuelEndEvent.handlerList = new HandlerList();
    }
    
    public DuelEndEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelEndEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return DuelEndEvent.handlerList;
    }
}
