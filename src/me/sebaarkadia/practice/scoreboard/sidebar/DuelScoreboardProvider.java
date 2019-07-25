package me.sebaarkadia.practice.scoreboard.sidebar;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.duel.DuelState;
import me.sebaarkadia.practice.scoreboard.SidebarEntry;
import me.sebaarkadia.practice.scoreboard.SidebarProvider;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.util.PlayerUtil;

public class DuelScoreboardProvider extends SidebarProvider
{
    private Practice plugin;
    
    public DuelScoreboardProvider(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player paramPlayer) {
        return DuelScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        if (duel != null) {
            final boolean isParty = duel.getOtherDuelTeam(player).size() >= 2;
            final String opponent = (duel.getOtherDuelTeam(player).get(0) != null) ? Bukkit.getOfflinePlayer(duel.getOtherDuelTeam(player).get(0)).getName() : ("Undefined" + (isParty ? "'s Party" : ""));
            final Player enemy = this.plugin.getServer().getPlayer(duel.getOtherDuelTeam(player).get(0));
            final String map = duel.getArenaName();
            lines.add(new SidebarEntry("§8§m------------------------"));
            if (duel.getDuelState() != DuelState.STARTING) {
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§cOpponent§7: §r" : "§cOponente§7: §r")) + opponent));
                lines.add(new SidebarEntry(""));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bYour Ping§7: §r" + PlayerUtil.getPing(player) : "§bPing§7: §r" + PlayerUtil.getPing(player)))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bOpponent Ping§7: §r" + PlayerUtil.getPing(enemy) : "§bPing del oponente§7: §r" + PlayerUtil.getPing(enemy)))));
                lines.add(new SidebarEntry(""));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bMap§7: §r" : "§bMapa§7: §r") + map)));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bDuration§7: §r" : "§bDuracion§7: §r")) + this.getRemaining((duel.getEndMatchTime() != 0L) ? duel.getDuration() : duel.getStartDuration())));
            }
            else {
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§cOpponent§7: §r" : "§cOponente§7: §r")) + opponent));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bMap§7: §r" : "§bMapa§7: §r") + map)));
                lines.add(new SidebarEntry(""));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bLadder§7: §r" : "§bKit§7: §r")) + duel.getKitName()));
            }
            if (player.isOp()) {
                final DecimalFormat df = new DecimalFormat("00.00");
                lines.add(new SidebarEntry("§r"));
                lines.add(new SidebarEntry("§f§oTPS§7: §r" + df.format(Practice.getInstance().getTps())));
            }
            else {
                lines.add(new SidebarEntry("§r"));
                lines.add(new SidebarEntry("§f§o" + Practice.getInstance().getConfig().getString("serverip")));
            }
            lines.add(new SidebarEntry("§8§m------------------------"));
            return lines;
        }
        return lines;
    }
    
    private String getRemaining(final long duration) {
        return DurationFormatUtils.formatDuration(duration, "mm:ss");
    }
}
