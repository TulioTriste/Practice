package me.sebaarkadia.practice.events;

import me.sebaarkadia.practice.hosts.PracticeEvent;

public class EventStartEvent extends BaseEvent {
    private final PracticeEvent event;

    public EventStartEvent(final PracticeEvent event) {
        this.event = event;
    }

    public PracticeEvent getEvent() {
        return this.event;
    }
}
