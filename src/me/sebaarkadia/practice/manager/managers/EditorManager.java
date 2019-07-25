package me.sebaarkadia.practice.manager.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.player.PlayerKit;
import me.sebaarkadia.practice.scoreboard.PlayerBoard;
import me.sebaarkadia.practice.scoreboard.sidebar.EditingScoreboardProvider;

import java.util.Map;

public class EditorManager extends Manager
{
    private Map<UUID, String> editingKit;
    private Map<UUID, PlayerKit> renamingKit;
    
    public EditorManager(final ManagerHandler handler) {
        super(handler);
        this.editingKit = new HashMap<UUID, String>();
        this.renamingKit = new HashMap<UUID, PlayerKit>();
    }
    
    public String getPlayerEditingKit(final UUID uuid) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());

        playerBoard.setDefaultSidebar(new EditingScoreboardProvider(this.handler.getPlugin()), 1L);
    	return this.editingKit.get(uuid);
    }
    
    public PlayerKit getKitRenaming(final UUID uuid) {
        return this.renamingKit.get(uuid);
    }
    
    public void addEditingKit(final UUID uuid, final Kit kit) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());

        playerBoard.setDefaultSidebar(new EditingScoreboardProvider(this.handler.getPlugin()), 1L);
        this.editingKit.put(uuid, kit.getName());
        this.handler.getInventoryManager().addEditKitItemsInventory(uuid, kit);
        this.handler.getInventoryManager().addEditKitKitsInventory(uuid, kit);
    }
    
    public void addRenamingKit(final UUID uuid, final PlayerKit playerKit) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());

        playerBoard.setDefaultSidebar(new EditingScoreboardProvider(this.handler.getPlugin()), 1L);
        this.renamingKit.put(uuid, playerKit);
    }
    
    public void removeEditingKit(final UUID uuid) {
        this.editingKit.remove(uuid);
        this.renamingKit.remove(uuid);
        this.handler.getInventoryManager().destroyEditKitItemsInventory(uuid);
        this.handler.getInventoryManager().destroyEditKitKitsInventory(uuid);
    }
    
    public void removeRenamingKit(final UUID uuid) {
        this.renamingKit.remove(uuid);
    }
    
    public Map<UUID, PlayerKit> getRenamingKit(final UUID uuid) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());

        playerBoard.setDefaultSidebar(new EditingScoreboardProvider(this.handler.getPlugin()), 1L);
        return this.renamingKit;
    }
}
