package me.sebaarkadia.practice.hosts.oitc;

import org.bukkit.ChatColor;

import me.sebaarkadia.practice.hosts.EventCountdownTask;
import me.sebaarkadia.practice.hosts.PracticeEvent;

import java.util.Arrays;

public class OITCCountdownTask extends EventCountdownTask {
    public OITCCountdownTask(final PracticeEvent event) {
        super(event, 60);
    }

    @Override
    public boolean shouldAnnounce(final int timeUntilStart) {
        return Arrays.asList(45, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return this.getEvent().getPlayers().size() >= 1;
    }

    @Override
    public void onCancel() {
        this.getEvent().sendMessage(ChatColor.RED + "Not enough players. Event has been cancelled");
        this.getEvent().end();
        this.getEvent().getPlugin().getManagerHandler().getEventManager().setCooldown(0L);
    }
}
