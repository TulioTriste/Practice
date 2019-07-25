package me.sebaarkadia.practice.manager.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.util.UtilItem;

public class ItemManager extends Manager
{
    private ItemStack[] spawnItems;
    private ItemStack[] queueItems;
    private ItemStack[] specItems;
    private ItemStack[] partyItems;
    private ItemStack[] tourItems;
    private ItemStack[] eventItems;
    
    public ItemManager(final ManagerHandler handler) {
        super(handler);
        this.loadSpawnItems();
        this.loadQueueItems();
        this.loadPartyItems();
        this.loadSpecItems();
        this.tourItems();
        this.eventItems();
    }
    
    private void loadSpawnItems() {
        this.spawnItems = new ItemStack[] { 
        		UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, "§7» §e§lUnRanked Queue §7«"),
        		UtilItem.createItem(Material.DIAMOND_SWORD, 1, (short)0, "§7» §6§lRanked Queue §7«"),
        		null,
        		UtilItem.createItem(Material.CHEST, 1, (short)0, "§7» §b§lEvents §7«"),
        		null,
        		UtilItem.createItem(Material.REDSTONE_COMPARATOR, 1, (short)0, "§7» §c§lSettings §7«"),
        		null,
        		UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§7» §e§lParty §7«"),
        		UtilItem.createItem(Material.ENCHANTED_BOOK, 1, (short)0, "§7» §e§lKit Editor §7«") };
    }
    
    private void loadPartyItems() {
        this.partyItems = new ItemStack[] { 
        		UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§7» §e§lInformation §7«"),
        		null,
        		UtilItem.createItem(Material.SKULL_ITEM, 1, (short)0, "§7» §b§lFight Other Party §7«"),
        		null,
        		UtilItem.createItem(Material.GOLD_SWORD, 1, (short)0, "§7» §e§l2v2 §7«"),
        		UtilItem.createItem(Material.DIAMOND_CHESTPLATE, 1, (short)0, "§7» §e§lTeam Fights §7«"),
        		null,
        		UtilItem.createItem(Material.REDSTONE_COMPARATOR, 1, (short)0, "§7» §c§lSettings §7«"),
        		UtilItem.createItem(Material.INK_SACK, 1, (short)1, "§cLeave §7(Right Click)") };
    }
    
    private void loadSpecItems() {
        this.specItems = new ItemStack[] { 
        		UtilItem.createItem(Material.COMPASS, 1, (short)0, "§aInventory §7(Right Click)"),
        		null,
        		null,
        		null,
        		null,
        		null,
        		null,
        		null,
        		UtilItem.createItem(Material.INK_SACK, 1, (short)1, "§cLeave §7(Right Click)") };
    }
    
    private void loadQueueItems() {
        this.queueItems = new ItemStack[] { 
        		UtilItem.createItem(Material.REDSTONE_COMPARATOR, 1, (short)0, "§cSettings §7(Right Click)"),
        		null,
        		null,
        		null,
        		null,
        		null,
        		null,
        		null,
        		UtilItem.createItem(Material.INK_SACK, 1, (short)1, "§cLeave §7(Right Click)") };
    }
    
    private void tourItems() {
        this.tourItems = new ItemStack[] { 
        		null,
        		null,
        		null,
        		null,
        		UtilItem.createItem(Material.INK_SACK, 1, (short)1, "§cLeave §7(Right Click)"),
        		null,
        		null,
        		null,
        		null };
    }
    
    private void eventItems() {
        this.tourItems = new ItemStack[] { 
        		null,
        		null,
        		null,
        		null,
        		UtilItem.createItem(Material.INK_SACK, 1, (short)1, "§cLeave Event §7(Right Click)"),
        		null,
        		null,
        		null,
        		null };
    }
    
    public ItemStack[] getSpawnItems() {
        return this.spawnItems;
    }
    
    public ItemStack[] getQueueItems() {
        return this.queueItems;
    }
    
    public ItemStack[] getSpecItems() {
        return this.specItems;
    }
    
    public ItemStack[] getPartyItems() {
        return this.partyItems;
    }
    
    public ItemStack[] getTourItems() {
        return this.tourItems;
    }
    
    public ItemStack[] getEventItems() {
        return this.eventItems;
    }
}
