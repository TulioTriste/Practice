package me.sebaarkadia.practice.runnables;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PracticePlayer;

public class RemovePlayerTask implements Runnable
{
    private Player player;
    
    public RemovePlayerTask(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        final PracticePlayer profile = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (profile != null) {
            if (Practice.getInstance().getServer().getPluginManager().getPlugin("cLib") != null) {
                profile.setGlobalPersonalElo(Practice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, true));
                profile.save();
            }
            PracticePlayer.getProfiles().remove(profile);
        }
    }
}
