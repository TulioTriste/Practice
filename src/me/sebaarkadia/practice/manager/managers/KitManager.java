package me.sebaarkadia.practice.manager.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.util.Config;

import java.util.HashMap;
import java.util.Map;

public class KitManager extends Manager
{
    private Map<String, Kit> kitMap;
    private Config mainConfig;
    
    public KitManager(final ManagerHandler handler) {
        super(handler);
        this.kitMap = new HashMap<String, Kit>();
        this.mainConfig = new Config(handler.getPlugin(), "", "kit");
        this.loadKits();
    }
    
    public void disable() {
        this.saveKits();
    }
    
    public Map<String, Kit> getKitMap() {
        return this.kitMap;
    }
    
    public Kit getKit(final String kitName) {
        return this.getKitMap().get(kitName);
    }
    
    public Kit createKit(final String kitName) {
        final Kit kit = new Kit(kitName, null, false, true, true, new ItemStack[36], new ItemStack[36], false, false);
        this.kitMap.put(kitName, kit);
        return kit;
    }
    
    public void destroyKit(final String kitName) {
        this.kitMap.remove(kitName);
    }
    
    public void loadKits() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        final ConfigurationSection arenaSection = fileConfig.getConfigurationSection("kits");
        if (arenaSection == null) {
            return;
        }
        for (final String kitName : arenaSection.getKeys(false)) {
            final boolean enabled = arenaSection.getBoolean(String.valueOf(kitName) + ".enabled");
            final ItemStack icon = (ItemStack)arenaSection.get(String.valueOf(kitName) + ".icon");
            final boolean combo = arenaSection.getBoolean(String.valueOf(kitName) + ".combo");
            final boolean editable = arenaSection.getBoolean(String.valueOf(kitName) + ".editable");
            final boolean ranked = arenaSection.getBoolean(String.valueOf(kitName) + ".ranked");
            final boolean premium = arenaSection.getBoolean(String.valueOf(kitName) + ".premium");
            final boolean builduhc = arenaSection.getBoolean(String.valueOf(kitName) + ".builduhc");
			final ItemStack[] mainContents = (ItemStack[]) ((List<?>)arenaSection.get(String.valueOf(kitName) + ".mainContents")).toArray(new ItemStack[0]);
            final ItemStack[] armorContents = (ItemStack[]) ((List<?>)arenaSection.get(String.valueOf(kitName) + ".armorContents")).toArray(new ItemStack[0]);
            final Kit kit = new Kit(kitName, icon, combo, editable, ranked, mainContents, armorContents, premium, builduhc);
            kit.setEnabled(enabled);
            this.kitMap.put(kitName, kit);
        }
    }
    
    public void saveKits() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        fileConfig.set("kits", null);
        for (final Map.Entry<String, Kit> kitEntry : this.kitMap.entrySet()) {
            final String kitName = kitEntry.getKey();
            final Kit kit = kitEntry.getValue();
            if (kit.getIcon() != null && kit.getMainContents() != null) {
                if (kit.getArmorContents() == null) {
                    continue;
                }
                fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
                fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
                fileConfig.set("kits." + kitName + ".combo", kit.isCombo());
                fileConfig.set("kits." + kitName + ".editable", kit.isEditable());
                fileConfig.set("kits." + kitName + ".ranked", kit.isRanked());
                fileConfig.set("kits." + kitName + ".mainContents", kit.getMainContents());
                fileConfig.set("kits." + kitName + ".armorContents", kit.getArmorContents());
                fileConfig.set("kits." + kitName + ".builduhc", kit.isBuilduhc());
            }
        }
        this.mainConfig.save();
    }
}
