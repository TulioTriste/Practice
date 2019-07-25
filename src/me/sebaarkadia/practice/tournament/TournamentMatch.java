package me.sebaarkadia.practice.tournament;

import org.bukkit.plugin.Plugin;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.arena.Arena;
import me.sebaarkadia.practice.kit.Kit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class TournamentMatch
{
    private TournamentTeam firstTeam;
    private TournamentTeam secondTeam;
    private List<UUID> matchPlayers;
    private MatchState matchState;
    private int winndingId;
    
    public void start(final Practice plugin, final Kit defaultKit) {
        (this.matchPlayers = new ArrayList<UUID>()).addAll(this.firstTeam.getPlayers());
        this.matchPlayers.addAll(this.secondTeam.getPlayers());
        Bukkit.getScheduler().runTaskLater((Plugin)plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                Arena arena = Practice.getInstance().getManagerHandler().getArenaManager().getRandomArena();
                final List<Arena> avaiable = new ArrayList<Arena>();
                for (final Arena allarenas : Practice.getInstance().getManagerHandler().getArenaManager().getArenas()) {
                    avaiable.add(allarenas);
                }
                for (@SuppressWarnings("unused") final Arena allarenas : Practice.getInstance().getManagerHandler().getArenaManager().getArenas()) {
                    for (arena = Practice.getInstance().getManagerHandler().getArenaManager().getRandomArena(); !avaiable.contains(arena); arena = Practice.getInstance().getManagerHandler().getArenaManager().getRandomArena()) {}
                    final FileConfiguration fileConfig = Practice.getInstance().getManagerHandler().getArenaManager().mainConfig.getConfig();
                    final List<String> kits = (List<String>)fileConfig.getStringList("arenas." + arena.getName() + ".kits");
                    if (kits.contains(defaultKit.getName())) {
                        Practice.getInstance().getManagerHandler().getDuelManager().createDuel(arena, defaultKit, false, TournamentMatch.this.firstTeam.getPlayers().get(0), TournamentMatch.this.secondTeam.getPlayers().get(0), TournamentMatch.this.firstTeam.getPlayers(), TournamentMatch.this.secondTeam.getPlayers(), true);
                        return;
                    }
                    avaiable.remove(arena);
                }
            }
        }, 40L);
        this.matchState = MatchState.FIGHTING;
        this.winndingId = 0;
    }
    
    public TournamentTeam getTournamentTeam(final TournamentTeam team) {
        if (this.firstTeam == team) {
            return this.firstTeam;
        }
        if (this.secondTeam == team) {
            return this.secondTeam;
        }
        return null;
    }
    
    public TournamentTeam getOtherTeam(final TournamentTeam tournamentTeam) {
        return this.getOtherDuelTeam(this.getTournamentTeam(tournamentTeam));
    }
    
    public TournamentTeam getOtherDuelTeam(final TournamentTeam duelTeam) {
        return (duelTeam == null) ? null : (duelTeam.equals(this.firstTeam) ? this.secondTeam : this.firstTeam);
    }
    
    public TournamentTeam getFirstTeam() {
        return this.firstTeam;
    }
    
    public void setFirstTeam(final TournamentTeam firstTeam) {
        this.firstTeam = firstTeam;
    }
    
    public TournamentTeam getSecondTeam() {
        return this.secondTeam;
    }
    
    public void setSecondTeam(final TournamentTeam secondTeam) {
        this.secondTeam = secondTeam;
    }
    
    public List<UUID> getMatchPlayers() {
        return this.matchPlayers;
    }
    
    public void setMatchPlayers(final List<UUID> matchPlayers) {
        this.matchPlayers = matchPlayers;
    }
    
    public MatchState getMatchState() {
        return this.matchState;
    }
    
    public void setMatchState(final MatchState matchState) {
        this.matchState = matchState;
    }
    
    public int getWinndingId() {
        return this.winndingId;
    }
    
    public void setWinndingId(final int winndingId) {
        this.winndingId = winndingId;
    }
    
    public enum MatchState
    {
        WAITING("WAITING", 0), 
        FIGHTING("FIGHTING", 1), 
        ENDING("ENDING", 2);
        
        private MatchState(final String s, final int n) {
        }
    }
}
