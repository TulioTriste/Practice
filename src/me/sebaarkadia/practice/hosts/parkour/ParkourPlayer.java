package me.sebaarkadia.practice.hosts.parkour;

import java.util.UUID;

import me.sebaarkadia.practice.hosts.EventPlayer;
import me.sebaarkadia.practice.hosts.PracticeEvent;
import me.sebaarkadia.practice.util.CustomLocation;

public class ParkourPlayer extends EventPlayer {
    private ParkourState state;
    private CustomLocation lastCheckpoint;
    private int checkpointId;

    public ParkourPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = ParkourState.WAITING;
    }

    public ParkourState getState() {
        return this.state;
    }

    public void setState(final ParkourState state) {
        this.state = state;
    }

    public CustomLocation getLastCheckpoint() {
        return this.lastCheckpoint;
    }

    public void setLastCheckpoint(final CustomLocation lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public int getCheckpointId() {
        return this.checkpointId;
    }

    public void setCheckpointId(final int checkpointId) {
        this.checkpointId = checkpointId;
    }

    public enum ParkourState {
        WAITING,
        INGAME
    }
}
