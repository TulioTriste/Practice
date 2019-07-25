package me.sebaarkadia.practice.manager.managers;

import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.runnables.UpdateInventoryTask;

import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class QueueManager extends Manager
{
    private Map<String, UUID> unrankedKitQueueMap;
    private Map<String, List<UUID>> rankedKitQueueMap;
    private Map<String, UUID> partyUnrankedKitQueueMap;
    private int rankedQueueCount;
    private int unrankedQueueCount;
    
    public QueueManager(final ManagerHandler handler) {
        super(handler);
        this.unrankedKitQueueMap = new ConcurrentHashMap<String, UUID>();
        this.rankedKitQueueMap = new ConcurrentHashMap<String, List<UUID>>();
        this.partyUnrankedKitQueueMap = new ConcurrentHashMap<String, UUID>();
        this.rankedQueueCount = 0;
        this.unrankedQueueCount = 0;
    }
    
    public boolean isUnrankedQueueEmpty(final String kitName) {
        return !this.unrankedKitQueueMap.containsKey(kitName);
    }
    
    public void addToUnrankedQueue(final String kitName, final UUID uuid) {
        this.unrankedKitQueueMap.put(kitName, uuid);
        ++this.unrankedQueueCount;
    }
    
    public UUID getQueuedForUnrankedQueue(final String kitName) {
        return this.unrankedKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromUnrankedQueue(final String kitName) {
        this.unrankedKitQueueMap.remove(kitName);
        --this.unrankedQueueCount;
    }
    
    public boolean isRankedQueueEmpty(final String kitName) {
        return !this.rankedKitQueueMap.containsKey(kitName) || this.rankedKitQueueMap.get(kitName).size() == 0;
    }
    
    public void addToRankedQueue(final String kitName, final UUID uuid) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            this.rankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.rankedKitQueueMap.get(kitName);
        uuidList.add(uuid);
        this.rankedKitQueueMap.put(kitName, uuidList);
        ++this.rankedQueueCount;
    }
    
    public List<UUID> getQueuedForRankedQueue(final String kitName) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            return null;
        }
        return this.rankedKitQueueMap.get(kitName);
    }
    
    public void removePlayerFromRankedQueue(final String kitName, final UUID playerUuid) {
        if (!this.rankedKitQueueMap.containsKey(kitName)) {
            this.rankedKitQueueMap.put(kitName, new ArrayList<UUID>());
        }
        final List<UUID> uuidList = this.rankedKitQueueMap.get(kitName);
        uuidList.remove(playerUuid);
        this.rankedKitQueueMap.put(kitName, uuidList);
        --this.rankedQueueCount;
    }
    
    @SuppressWarnings("unchecked")
	public void unqueueSingleQueue(final UUID playerUuid) {
        for (final Map.Entry<String, UUID> mapEntry : this.unrankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry.getKey();
            final UUID queued = mapEntry.getValue();
            if (queued != playerUuid) {
                continue;
            }
            --this.unrankedQueueCount;
            this.unrankedKitQueueMap.remove(kitName);
        }
        for (@SuppressWarnings("rawtypes") final Map.Entry mapEntry2 : this.rankedKitQueueMap.entrySet()) {
            final String kitName = (String) mapEntry2.getKey();
            final List<?> queuedUuidList = (List<?>) mapEntry2.getValue();
            if (!queuedUuidList.contains(playerUuid)) {
                continue;
            }
            queuedUuidList.remove(playerUuid);
            this.rankedKitQueueMap.put(kitName, (List<UUID>) queuedUuidList);
            --this.rankedQueueCount;
        }
    }
    
    public boolean isUnrankedPartyQueueEmpty(final String kitName) {
        return !this.partyUnrankedKitQueueMap.containsKey(kitName);
    }
    
    public void addToPartyUnrankedQueue(final String kitName, final UUID uuid) {
        this.partyUnrankedKitQueueMap.put(kitName, uuid);
        ++this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public UUID getQueuedForPartyUnrankedQueue(final String kitName) {
        return this.partyUnrankedKitQueueMap.get(kitName);
    }
    
    public void removePartyFromPartyUnrankedQueue(final String kitName) {
        this.partyUnrankedKitQueueMap.remove(kitName);
        --this.unrankedQueueCount;
        Bukkit.getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public void unqueuePartyQueue(final UUID leaderUuid) {
        for (final Map.Entry<String, UUID> mapEntry : this.partyUnrankedKitQueueMap.entrySet()) {
            final String kitName = mapEntry.getKey();
            final UUID queued = mapEntry.getValue();
            if (queued != leaderUuid) {
                continue;
            }
            this.partyUnrankedKitQueueMap.remove(kitName);
            --this.unrankedQueueCount;
            Bukkit.getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new UpdateInventoryTask(this.handler.getPlugin(), UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
        }
    }
    
    public Map<String, List<UUID>> getRankedKitQueueMap() {
        return this.rankedKitQueueMap;
    }
    
    public int getTotalInQueues() {
        return this.rankedQueueCount + this.unrankedQueueCount;
    }
}
