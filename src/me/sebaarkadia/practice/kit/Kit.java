package me.sebaarkadia.practice.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit
{
    private boolean enabled;
    private String name;
    private ItemStack icon;
    private boolean combo;
    private boolean editable;
    private boolean ranked;
    private boolean builduhc;
    private ItemStack[] mainContents;
    private ItemStack[] armorContents;
    
    public Kit(final String name, final ItemStack icon, final boolean combo, final boolean editable, final boolean ranked, final ItemStack[] mainContents, final ItemStack[] armorContents, final boolean premium, final boolean build) {
        this.name = name;
        this.icon = icon;
        this.combo = combo;
        this.editable = editable;
        this.ranked = ranked;
        this.mainContents = mainContents;
        this.armorContents = armorContents;
        this.builduhc = build;
        this.mainContents = mainContents;
        this.mainContents = new ItemStack[36];
        this.armorContents = new ItemStack[4];
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    public boolean isCombo() {
        return this.combo;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public ItemStack[] getMainContents() {
        return this.mainContents;
    }
    
    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setIcon(final ItemStack icon) {
        this.icon = icon;
    }
    
    public void setCombo(final boolean combo) {
        this.combo = combo;
    }
    
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
    
    public void setRanked(final boolean ranked) {
        this.ranked = ranked;
    }
    
    public void setMainContents(final ItemStack[] mainContents) {
        this.mainContents = mainContents;
    }
    
    public void setArmorContents(final ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }
    
    public List<ItemStack> getEditableContents() {
        final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack[] mainContents;
        for (int length = (mainContents = this.getMainContents()).length, i = 0; i < length; ++i) {
            final ItemStack item = mainContents[i];
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        return items;
    }

    public void applyToPlayer(final Player player) {
        player.getInventory().setContents(this.mainContents);
        player.getInventory().setArmorContents(this.armorContents);
        player.updateInventory();
    }

    public ItemStack[] getContents() {
        return this.mainContents;
    }
    
    public boolean isBuilduhc() {
        return this.builduhc;
    }
    
    public void setBuilduhc(final boolean builduhc) {
        this.builduhc = builduhc;
    }
}
