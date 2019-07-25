package me.sebaarkadia.practice.manager.managers;

import java.util.concurrent.TimeUnit;

import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.player.PlayerInventorySnapshot;
import me.sebaarkadia.practice.util.TtlHashMap;

import java.util.UUID;
import java.util.Map;

public class InventorySnapshotManager extends Manager
{
    private Map<UUID, PlayerInventorySnapshot> snapshotMap;
    
    public InventorySnapshotManager(final ManagerHandler handler) {
        super(handler);
        this.snapshotMap = new TtlHashMap<UUID, PlayerInventorySnapshot>(TimeUnit.MINUTES, 1L);
    }
    
    public void addSnapshot(final UUID uuid, final PlayerInventorySnapshot playerInventorySnapshot) {
        this.snapshotMap.put(uuid, playerInventorySnapshot);
    }
    
    public PlayerInventorySnapshot getSnapshotFromUUID(final UUID uuid) {
        return this.snapshotMap.get(uuid);
    }
}
