package me.sebaarkadia.practice.runnables;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PracticePlayer;

public class SavePlayerElo implements Runnable
{
    private Player player;
    
    public SavePlayerElo(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        final PracticePlayer profile = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (profile != null) {
            profile.setGlobalPersonalElo(Practice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, true));
            profile.save();
        }
    }
}
