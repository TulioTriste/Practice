package me.sebaarkadia.practice.manager;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.manager.managers.ArenaManager;
import me.sebaarkadia.practice.manager.managers.DuelManager;
import me.sebaarkadia.practice.manager.managers.EditorManager;
import me.sebaarkadia.practice.manager.managers.EventManager;
import me.sebaarkadia.practice.manager.managers.InventoryManager;
import me.sebaarkadia.practice.manager.managers.InventorySnapshotManager;
import me.sebaarkadia.practice.manager.managers.ItemManager;
import me.sebaarkadia.practice.manager.managers.KitManager;
import me.sebaarkadia.practice.manager.managers.PartyManager;
import me.sebaarkadia.practice.manager.managers.PracticePlayerManager;
import me.sebaarkadia.practice.manager.managers.QueueManager;
import me.sebaarkadia.practice.manager.managers.RequestManager;
import me.sebaarkadia.practice.manager.managers.SpawnManager;
import me.sebaarkadia.practice.manager.managers.SpectatorManager;
import me.sebaarkadia.practice.scoreboard.ScoreboardHandler;

public class ManagerHandler
{
    private Practice plugin;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private KitManager kitManager;
    private EditorManager editorManager;
    private ItemManager itemManager;
    private PracticePlayerManager practicePlayerManager;
    private InventoryManager inventoryManager;
    private QueueManager queueManager;
    private RequestManager requestManager;
    private SpectatorManager spectatorManager;
    private InventorySnapshotManager inventorySnapshotManager;
    private PartyManager partyManager;
    private ScoreboardHandler scoreboardHandler;
    private EventManager eventManager;
    private SpawnManager spawnManager;
    
    public ManagerHandler(final Practice plugin) {
        this.plugin = plugin;
        this.loadManagers();
    }
    
    private void loadManagers() {
        this.arenaManager = new ArenaManager(this);
        this.duelManager = new DuelManager(this.plugin, this);
        this.kitManager = new KitManager(this);
        this.editorManager = new EditorManager(this);
        this.itemManager = new ItemManager(this);
        this.practicePlayerManager = new PracticePlayerManager(this);
        this.queueManager = new QueueManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.requestManager = new RequestManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.inventorySnapshotManager = new InventorySnapshotManager(this);
        this.partyManager = new PartyManager(this);
        this.scoreboardHandler = new ScoreboardHandler(this.plugin);
        this.eventManager = new EventManager();
        this.spawnManager = new SpawnManager();
    }
    
    public void disable() {
        this.arenaManager.disable();
        this.kitManager.disable();
        this.practicePlayerManager.disable();
        this.scoreboardHandler.clearBoards();
    }
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }
    
    public DuelManager getDuelManager() {
        return this.duelManager;
    }
    
    public KitManager getKitManager() {
        return this.kitManager;
    }
    
    public EditorManager getEditorManager() {
        return this.editorManager;
    }
    
    public ItemManager getItemManager() {
        return this.itemManager;
    }
    
    public PracticePlayerManager getPracticePlayerManager() {
        return this.practicePlayerManager;
    }
    
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
    
    public QueueManager getQueueManager() {
        return this.queueManager;
    }
    
    public RequestManager getRequestManager() {
        return this.requestManager;
    }
    
    public SpectatorManager getSpectatorManager() {
        return this.spectatorManager;
    }
    
    public InventorySnapshotManager getInventorySnapshotManager() {
        return this.inventorySnapshotManager;
    }
    
    public PartyManager getPartyManager() {
        return this.partyManager;
    }
    
    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
    }
    
    public EventManager getEventManager() {
        return this.eventManager;
    }
    
    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }
}
