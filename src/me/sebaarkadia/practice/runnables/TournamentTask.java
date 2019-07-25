package me.sebaarkadia.practice.runnables;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.tournament.Tournament;

public class TournamentTask extends BukkitRunnable
{
    private Tournament tournament;
    
    public TournamentTask(final Tournament tournament) {
        this.tournament = tournament;
    }
    
    public void run() {
        this.tournament.generateRoundMatches();
        this.tournament.getPlugin().getServer().getScheduler().runTask((Plugin)this.tournament.getPlugin(), (Runnable)new UpdateInventoryTask(this.tournament.getPlugin(), UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
    }
}
