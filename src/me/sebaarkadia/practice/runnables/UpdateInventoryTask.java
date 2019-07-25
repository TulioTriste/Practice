package me.sebaarkadia.practice.runnables;

import me.sebaarkadia.practice.Practice;

public class UpdateInventoryTask implements Runnable
{
    private Practice plugin;
    private InventoryTaskType inventoryTaskType;
    
    public UpdateInventoryTask(final Practice plugin, final InventoryTaskType inventoryTaskType) {
        this.plugin = plugin;
        this.inventoryTaskType = inventoryTaskType;
    }
    
    @Override
    public void run() {
        if (this.inventoryTaskType == InventoryTaskType.UNRANKED_PARTY) {
            this.plugin.getManagerHandler().getInventoryManager().setUnrankedPartyInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.TOURNAMENT) {
            this.plugin.getManagerHandler().getInventoryManager().setJoinTournamentInventory();
        }
    }
    
    public enum InventoryTaskType
    {
        UNRANKED_PARTY("UNRANKED_PARTY", 0), 
        TOURNAMENT("TOURNAMENT", 1);
        
        private InventoryTaskType(final String s, final int n) {
        }
    }
}
