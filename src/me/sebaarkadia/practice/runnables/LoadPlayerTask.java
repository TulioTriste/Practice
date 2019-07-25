package me.sebaarkadia.practice.runnables;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import org.bukkit.inventory.ItemStack;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.player.PlayerKit;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.util.Config;

public class LoadPlayerTask implements Runnable
{
    private PracticePlayer practicePlayer;
    private Practice plugin;
    
    public LoadPlayerTask(final Practice plugin, final PracticePlayer practicePlayer) {
        this.plugin = plugin;
        this.practicePlayer = practicePlayer;
    }
    
    @Override
    public void run() {
        if (this.practicePlayer == null) {
            return;
        }
        if (Practice.getInstance().getServer().getPluginManager().getPlugin("cLib") != null) {
            for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
                if (!this.practicePlayer.getEloMap().containsKey(kit.getName())) {
                    this.practicePlayer.addElo(kit.getName(), 1000);
                }
            }
        }
        final Config config = new Config(this.plugin, "/players", this.practicePlayer.getUUID().toString());
        final ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
        if (playerKitsSection != null) {
            for (final Kit kit2 : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
                final ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit2.getName());
                if (kitSection == null) {
                    continue;
                }
                for (final String kitKey : kitSection.getKeys(false)) {
                    final Integer kitIndex = Integer.parseInt(kitKey);
                    final String displayName = kitSection.getString(String.valueOf(kitKey) + ".displayName");
					final ItemStack[] mainContents = (ItemStack[]) ((List<?>)kitSection.get(String.valueOf(kitKey) + ".mainContents")).toArray(new ItemStack[0]);
					final ItemStack[] armorContents = (ItemStack[]) ((List<?>)kitSection.get(String.valueOf(kitKey) + ".armorContents")).toArray(new ItemStack[0]);
                    final PlayerKit playerKit = new PlayerKit(kit2.getName(), kitIndex, displayName, mainContents, armorContents);
                    this.practicePlayer.addKit(kit2.getName(), kitIndex, playerKit);
                }
            }
        }
        this.practicePlayer.setCurrentState(PlayerState.LOBBY);
    }
}
