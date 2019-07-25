package me.sebaarkadia.practice.events;

import org.bukkit.event.HandlerList;

import me.sebaarkadia.practice.duel.Duel;

import org.bukkit.event.Event;

public class DuelCreateEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    static {
        DuelCreateEvent.handlerList = new HandlerList();
    }
    
    public DuelCreateEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelCreateEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return DuelCreateEvent.handlerList;
    }
}
