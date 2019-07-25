package me.sebaarkadia.practice.scoreboard.sidebar;

import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.scoreboard.SidebarEntry;
import me.sebaarkadia.practice.scoreboard.SidebarProvider;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.util.ColorAPI;
import me.sebaarkadia.practice.util.ColorAPI2;
import me.sebaarkadia.practice.util.PlayerUtility;

public class LobbyScoreboardProvider extends SidebarProvider
{
    private Practice plugin;
    
    public LobbyScoreboardProvider(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player player) {
        return LobbyScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        lines.add(new SidebarEntry("§8§m------------------------"));
        lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bOnline §7» §r" : "§bConectados: §r")) + PlayerUtility.getOnlinePlayers().size()));
        lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§bIn Fight §7» §r" : "§bEn juego: §r")) + this.getTotalInGame()));
        if (settings.isMod()) {
            lines.add(new SidebarEntry(""));
            lines.add(new SidebarEntry(" " + ColorAPI2.prefix + ColorAPI2.suffix + String.valueOf(settings.isPublicChat() ? " You are in StaffMode" : " Estas en StaffMode")));
        }
        if (party != null) {
            lines.add(new SidebarEntry(""));
            lines.add(new SidebarEntry("§bParty §7» §7(" + party.getAllMembersOnline().size() + " Member" + ((party.getAllMembersOnline().size() <= 1) ? "" : "s") + ")"));
            lines.add(new SidebarEntry(String.valueOf(settings.isPublicChat() ? "§bLeader §7» §c" : "§bJefe §7» §cf") + Bukkit.getPlayer(party.getLeader()).getName()));
        }
        if (practicePlayer.getCurrentState() == PlayerState.QUEUE) {
            lines.add(new SidebarEntry(""));
            lines.add(new SidebarEntry(" " + ColorAPI2.prefix + ColorAPI2.suffix + String.valueOf(settings.isPublicChat() ? " Wait Player" : " Esperando Jugadores") + ColorAPI.suffix));
        } 
        for (final Tournament tournament2 : Tournament.getTournaments()) {
            lines.add(new SidebarEntry(""));
            lines.add(new SidebarEntry(String.valueOf(settings.isPublicChat() ? "§bTournament §f(" : "§bTorneo §f(") + tournament2.getMaximumPerTeam() + "v" + tournament2.getMaximumPerTeam() + ")"));
            lines.add(new SidebarEntry("§fKit§7: §b" + tournament2.getDefaultKit().getName()));
            lines.add(new SidebarEntry(String.valueOf(settings.isPublicChat() ? "§fStage§7: §b" : "§fEstado§7: §b") + ((tournament2.getTournamentStage() == null) ? "Waiting" : StringUtils.capitalize(tournament2.getTournamentStage().name().replace("_", " ")))));
            lines.add(new SidebarEntry(String.valueOf(settings.isPublicChat() ? "§fTotal Players§7: §b" : "§fPlayers en total§7: §b") + tournament2.getTeams().size()));
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
    
    private int getTotalInGame() {
        int count = 0;
        for (final Duel duel : this.plugin.getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values()) {
            count += duel.getFirstTeam().size();
            count += duel.getSecondTeam().size();
        }
        return count;
    }
}
