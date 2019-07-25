package me.sebaarkadia.practice.listeners;

import java.util.Set;
import java.util.HashSet;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import java.lang.reflect.Field;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import com.google.common.collect.Lists;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.arena.Arena;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.duel.DuelState;
import me.sebaarkadia.practice.events.DuelEndEvent;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.party.PartyState;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.SavePlayerElo;
import me.sebaarkadia.practice.scoreboard.PlayerBoard;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.tournament.TournamentMatch;
import me.sebaarkadia.practice.util.Config;
import me.sebaarkadia.practice.util.UtilActionMessage;
import me.sebaarkadia.practice.util.UtilElo;
import me.sebaarkadia.practice.events.*;

import org.bukkit.event.EventHandler;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class DuelListener implements Listener
{
    private Practice plugin;
    private String NO_ARENA_AVAILABLE;
    private Map<UUID, BukkitTask> duelTickThreadMap;
    private Map<UUID, Integer> duelCountdownMap;
    
    public DuelListener(final Practice plugin) {
        this.NO_ARENA_AVAILABLE = ChatColor.RED + "There are no arenas available at this moment";
        this.duelTickThreadMap = new HashMap<UUID, BukkitTask>();
        this.duelCountdownMap = new HashMap<UUID, Integer>();
        plugin.getManagerHandler().getArenaManager().mainConfig = new Config(plugin, "", "arena");
        this.plugin = plugin;
    }
    
    @EventHandler
    public void DuelPreCreateEvent(final DuelPreCreateEvent e) {
        final Kit kit = e.getKit();
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getRandomArena();
        if (arena == null || kit == null) {
            for (final UUID uuid : e.getFirstTeam()) {
                final Player player = this.plugin.getServer().getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.sendMessage(this.NO_ARENA_AVAILABLE);
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            }
            for (final UUID uuid : e.getSecondTeam()) {
                final Player player = this.plugin.getServer().getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.sendMessage(this.NO_ARENA_AVAILABLE);
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            }
        }
        else {
            final List<Arena> avaiable = new ArrayList<Arena>();
            for (final Arena allarenas : this.plugin.getManagerHandler().getArenaManager().getArenas()) {
                avaiable.add(allarenas);
            }
            for (@SuppressWarnings("unused") final Arena allarenas : this.plugin.getManagerHandler().getArenaManager().getArenas()) {
                for (arena = this.plugin.getManagerHandler().getArenaManager().getRandomArena(); !avaiable.contains(arena); arena = this.plugin.getManagerHandler().getArenaManager().getRandomArena()) {}
                final FileConfiguration fileConfig = this.plugin.getManagerHandler().getArenaManager().mainConfig.getConfig();
                final List<String> kits = (List<String>)fileConfig.getStringList("arenas." + arena.getName() + ".kits");
                if (kits.contains(kit.getName())) {
                    this.plugin.getManagerHandler().getDuelManager().createDuel(arena, kit, e.isRanked(), e.getFirstTeamPartyLeaderUUID(), e.getSecondTeamPartyLeaderUUID(), e.getFirstTeam(), e.getSecondTeam(), false);
                    return;
                }
                avaiable.remove(arena);
            }
        }
        for (final UUID uuid : e.getFirstTeam()) {
            final Player firstplayer = Bukkit.getPlayer(uuid);
            firstplayer.sendMessage(this.NO_ARENA_AVAILABLE);
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(firstplayer);
        }
        for (final UUID uuid : e.getSecondTeam()) {
            final Player firstplayer = Bukkit.getPlayer(uuid);
            firstplayer.sendMessage(this.NO_ARENA_AVAILABLE);
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(firstplayer);
        }
    }
    
    public void unSetHealthBars(final Duel duel) {
        final List<Player> allPlayers = Lists.newArrayList();
        if (duel.getFirstTeam() != null) {
            for (final UUID uuid : duel.getFirstTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getSecondTeam() != null) {
            for (final UUID uuid : duel.getSecondTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayers()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        for (final Player player2 : allPlayers) {
            final Scoreboard sb = player2.getScoreboard();
            if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
                continue;
            }
            final Objective objective = sb.getObjective("showhealth");
            if (objective == null) {
                continue;
            }
            objective.unregister();
        }
    }
    
    public void setHealthBars(final Duel duel) {
        final List<Player> allPlayers = Lists.newArrayList();
        if (duel.getFirstTeam() != null) {
            for (final UUID uuid : duel.getFirstTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getSecondTeam() != null) {
            for (final UUID uuid : duel.getSecondTeam()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayers()) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    allPlayers.add(player);
                }
            }
        }
        for (final Player player2 : allPlayers) {
            final Scoreboard sb = player2.getScoreboard();
            if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
                continue;
            }
            Objective objective = sb.getObjective("showhealth");
            if (objective != null) {
                continue;
            }
            try {
                final Field field = EntityPlayer.class.getDeclaredField("bP");
                field.setAccessible(true);
                for (final Player other : allPlayers) {
                    field.setFloat(((CraftPlayer)other).getHandle(), Float.MIN_VALUE);
                }
            }
            catch (ReflectiveOperationException ex) {}
            objective = sb.registerNewObjective("showhealth", "health");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.RED + "\u2764");
        }
    }
    
    @EventHandler
    public void DuelCreateEvent(final me.sebaarkadia.practice.events.DuelCreateEvent e) {
        final Duel duel = e.getDuel();
        if (this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            this.setHealthBars(duel);
            this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).setOpen(false);
        }
        this.duelCountdownMap.put(duel.getUUID(), 6);
        final BukkitTask tickThread = new BukkitRunnable() {
            public void run() {
                int countdown = DuelListener.this.duelCountdownMap.get(duel.getUUID());
                if (duel.getDuelState() == DuelState.STARTING) {
                    if (--countdown == 0) {
                        duel.setDuelState(DuelState.FIGHTING);
                        if (duel.getFfaPlayers() != null) {
                            for (final UUID uuid : duel.getFfaPlayersAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage("§a" + String.valueOf(settings.isPublicChat() ? "§7§l» §c§lThe game has started! §7§l«" : "§7§l» §c§lEl Duelo ha comenzado! §7§l«"));
                                duel.setStartMatchTime(System.currentTimeMillis());
                                final Location location = player.getLocation();
                                player.playSound(location, Sound.FIREWORK_BLAST, 7.5f, 2.0f);
                                player.setSaturation(20.0f);
                            }
                        }
                        else {
                            for (final UUID uuid : duel.getFirstTeamAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage("§a" + String.valueOf(settings.isPublicChat() ? "§7§l» §c§lThe game has started! §7§l«" : "§7§l» §c§lEl Duelo ha comenzado! §7§l«"));
                                duel.setStartMatchTime(System.currentTimeMillis());
                                final Location location = player.getLocation();
                                player.playSound(location, Sound.FIREWORK_BLAST, 7.5f, 2.0f);
                                player.setSaturation(20.0f);
                            }
                            for (final UUID uuid : duel.getSecondTeamAlive()) {
                                final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                                final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                                final PracticePlayer practicePlayer = DuelListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
                                practicePlayer.setCurrentState(PlayerState.FIGHTING);
                                player.sendMessage("§a" + String.valueOf(settings.isPublicChat() ? "§7§l» §c§lThe game has started! §7§l«" : "§7§l» §c§lEl Duelo ha comenzado! §7§l«"));
                                duel.setStartMatchTime(System.currentTimeMillis());
                                final Location location = player.getLocation();
                                player.playSound(location, Sound.FIREWORK_BLAST, 7.5f, 2.0f);
                                player.setSaturation(20.0f);
                            }
                        }
                        return;
                    }
                    if (duel.getFfaPlayers() != null) {
                        for (final UUID uuid : duel.getFfaPlayersAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                            if (settings.isPublicChat()) {
                                player.sendMessage("§7The game will begin in §c§l" + countdown + " §7seconds.");
                            }
                            else {
                                player.sendMessage("§7La partida comenzara en §c§l" + countdown + "§7 segundos.");
                            }
                            final Location location2 = player.getLocation();
                            player.playSound(location2, Sound.NOTE_PLING, 7.5f, 2.0f);
                            player.setSaturation(20.0f);
                        }
                    }
                    else {
                        for (final UUID uuid : duel.getFirstTeamAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                            if (settings.isPublicChat()) {
                                player.sendMessage("§7The game will begin in §c§l" + countdown + " §7seconds.");
                            }
                            else {
                                player.sendMessage("§7La partida comenzara en §c§l" + countdown + "§7 segundos.");
                            }
                            final Location location2 = player.getLocation();
                            player.playSound(location2, Sound.NOTE_PLING, 7.5f, 2.0f);
                            player.setSaturation(20.0f);
                        }
                        for (final UUID uuid : duel.getSecondTeamAlive()) {
                            final Player player = DuelListener.this.plugin.getServer().getPlayer(uuid);
                            if (player == null) {
                                continue;
                            }
                            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
                            if (settings.isPublicChat()) {
                                player.sendMessage("§7The game will begin in §c§l" + countdown + " §7seconds.");
                            }
                            else {
                                player.sendMessage("§7La partida comenzara en §c§l" + countdown + "§f segundos.");
                            }
                            final Location location2 = player.getLocation();
                            player.playSound(location2, Sound.NOTE_PLING, 7.5f, 2.0f);
                            player.setSaturation(20.0f);
                        }
                    }
                }
                else if (duel.getDuelState() == DuelState.ENDING) {
                    countdown -= 2;
                    if (countdown <= 0) {
                        DuelListener.this.plugin.getServer().getPluginManager().callEvent((Event)new DuelEndEvent(duel));
                    }
                }
                DuelListener.this.duelCountdownMap.put(duel.getUUID(), countdown);
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L);
        this.duelTickThreadMap.put(duel.getUUID(), tickThread);
    }
    
    @EventHandler
    public void DuelEndingEvent(final me.sebaarkadia.practice.events.DuelEndingEvent e) {
        final Duel duel = e.getDuel();
        if (this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            this.unSetHealthBars(duel);
            this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).getBlockChangeTracker().rollback();
            this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName()).setOpen(true);
        }
        this.duelCountdownMap.put(duel.getUUID(), 6);
        final UtilActionMessage winnerMessage = new UtilActionMessage();
        final UtilActionMessage loserMessage = new UtilActionMessage();
        final Set<Player> duelPlayers = new HashSet<Player>();
        if (duel.getFfaPlayers() != null) {
            final Player lastPlayer = this.plugin.getServer().getPlayer((UUID)duel.getFfaPlayersAlive().get(0));
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(lastPlayer);
            practicePlayer.setCurrentState(PlayerState.WAITING);
            winnerMessage.addText(ChatColor.GREEN + lastPlayer.getName() + " ").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(lastPlayer.getUniqueId()));
            for (final UUID uuidPlayer : duel.getFfaPlayers()) {
                if (uuidPlayer.equals(lastPlayer.getUniqueId())) {
                    continue;
                }
                final Player player = this.plugin.getServer().getPlayer(uuidPlayer);
                if (player == null) {
                    continue;
                }
                duelPlayers.add(player);
                loserMessage.addText(ChatColor.RED + player.getName() + " ").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player.getUniqueId()));
            }
            duelPlayers.add(lastPlayer);
        }
        else {
            List<UUID> winningTeam = null;
            List<UUID> losingTeam = null;
            final int teamNumber = e.getTeamNumber();
            switch (teamNumber) {
                case 1: {
                    winningTeam = duel.getFirstTeam();
                    losingTeam = duel.getSecondTeam();
                    break;
                }
                case 2: {
                    winningTeam = duel.getSecondTeam();
                    losingTeam = duel.getFirstTeam();
                    break;
                }
            }
            for (final UUID uuidPlayer2 : duel.getFirstTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuidPlayer2);
                if (player2 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player2);
                practicePlayer2.setCurrentState(PlayerState.WAITING);
            }
            for (final UUID uuidPlayer2 : duel.getSecondTeamAlive()) {
                final Player player2 = this.plugin.getServer().getPlayer(uuidPlayer2);
                if (player2 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player2);
                practicePlayer2.setCurrentState(PlayerState.WAITING);
            }
            if (duel.isTournament() && Tournament.getTournaments().size() > 0) {
                for (final Tournament tournament : Tournament.getTournaments()) {
                    if (tournament != null) {
                        final Iterator<TournamentMatch> iterator = tournament.getCurrentMatches().iterator();
                        while (iterator.hasNext()) {
                            final TournamentMatch match = iterator.next();
                            if (match.getFirstTeam().getPlayers().equals(duel.getFirstTeam()) && match.getSecondTeam().getPlayers().equals(duel.getSecondTeam())) {
                                final String winningTeamOne = "§7[§dEvent " + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam() + "§7] §a" + Bukkit.getOfflinePlayer(duel.getFirstTeamPartyLeaderUUID()).getName() + ((duel.getFirstTeam().size() > 1) ? "'s Team" : "") + " §fhas eliminated " + ChatColor.RED + Bukkit.getOfflinePlayer(duel.getSecondTeamPartyLeaderUUID()).getName() + ((duel.getSecondTeam().size() > 1) ? "'s Team" : "");
                                final String winningTeamTwo = "§7[§dEvent " + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam() + "§7] §a" + Bukkit.getOfflinePlayer(duel.getSecondTeamPartyLeaderUUID()).getName() + ((duel.getSecondTeam().size() > 1) ? "'s Team" : "") + "§f has eliminated " + ChatColor.RED + Bukkit.getOfflinePlayer(duel.getFirstTeamPartyLeaderUUID()).getName() + ((duel.getFirstTeam().size() > 1) ? "'s Team" : "");
                                this.plugin.getServer().broadcastMessage((e.getTeamNumber() == 1) ? winningTeamOne : winningTeamTwo);
                                match.setWinndingId(e.getTeamNumber());
                                match.setMatchState(TournamentMatch.MatchState.ENDING);
                                tournament.getTeams().remove((e.getTeamNumber() == 1) ? match.getSecondTeam() : match.getFirstTeam());
                                tournament.getCurrentQueue().remove((e.getTeamNumber() == 1) ? match.getSecondTeam() : match.getFirstTeam());
                                iterator.remove();
                            }
                        }
                    }
                }
            }
            String winnerElo = "";
            String loserElo = "";
            if (duel.isRanked() && duel.getFirstTeam().size() == 1 && duel.getSecondTeam().size() == 1) {
                final PracticePlayer winnerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(winningTeam.get(0));
                final PracticePlayer loserPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(losingTeam.get(0));
                final int currentWinnerElo = winnerPracPlayer.getEloMap().get(duel.getKitName());
                final int currentLoserElo = loserPracPlayer.getEloMap().get(duel.getKitName());
                final int[] newElos = UtilElo.getNewRankings(currentWinnerElo, currentLoserElo, true);
                winnerElo = ChatColor.GREEN + " +" + (newElos[0] - currentWinnerElo) + " (" + newElos[0] + ")";
                loserElo = ChatColor.RED + " -" + (currentLoserElo - newElos[1]) + " (" + newElos[1] + ")";
                winnerPracPlayer.addElo(duel.getKitName(), newElos[0]);
                loserPracPlayer.addElo(duel.getKitName(), newElos[1]);
            }
            boolean partyMatch = false;
            if (duel.getFirstTeam() != null && duel.getFirstTeam().size() > 1) {
                partyMatch = true;
            }
            for (final UUID uuidPlayer3 : winningTeam) {
                final Player player3 = this.plugin.getServer().getPlayer(uuidPlayer3);
                if (player3 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer3 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player3.getUniqueId());
                if (!duel.isRanked() && practicePlayer3 != null) {
                    practicePlayer3.setUnrankedWins(practicePlayer3.getUnrankedWins() + 1);
                }
                duelPlayers.add(player3);
                winnerMessage.addText(ChatColor.DARK_GREEN + (partyMatch ? "Winner§7:" : "Winner§7: ") + ChatColor.GREEN + player3.getName() + " §8[§c" + practicePlayer3.getPotion() + "§8]" + winnerElo + " ").addHoverText(ChatColor.GRAY + "Click here to view inventory").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player3.getUniqueId()));
            }
            for (final UUID uuidPlayer3 : losingTeam) {
                final Player player3 = this.plugin.getServer().getPlayer(uuidPlayer3);
                if (player3 == null) {
                    continue;
                }
                final PracticePlayer practicePlayer3 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player3.getUniqueId());
                duelPlayers.add(player3);
                loserMessage.addText(ChatColor.DARK_RED + (partyMatch ? "Loser§7: " : "Loser§7: ") + ChatColor.RED + player3.getName() + " §8[§c" + practicePlayer3.getPotion() + "§8]" + loserElo + " ").addHoverText(ChatColor.GRAY + "Click here to view inventory").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/inventory " + duel.getPlayerUUIDtoSnapshotUUIDMap().get(player3.getUniqueId()));
            }
        }
        for (final UUID spectatorUUID : duel.getSpectators()) {
            final Player player4 = this.plugin.getServer().getPlayer(spectatorUUID);
            if (player4 == null) {
                continue;
            }
            duelPlayers.add(player4);
        }
        for (final Player player5 : duelPlayers) {
            ((CraftPlayer)player5).getHandle().getDataWatcher().watch(9, (Object)(byte)0);
            XpListener.removeCooldown(player5);
            final String[] information = { String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "»---------------------«" };
            player5.sendMessage(information);
            winnerMessage.sendToPlayer(player5);
            loserMessage.sendToPlayer(player5);
            player5.sendMessage(information);
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)Practice.getInstance(), (Runnable)new SavePlayerElo(player5.getPlayer()), 3L);
        }
        for (final Player playerInDuel : duelPlayers) {
            final PlayerBoard playerBoard = this.plugin.getManagerHandler().getScoreboardHandler().getPlayerBoard(playerInDuel.getUniqueId());
            if (playerBoard != null) {
                try {
                    playerBoard.addUpdates(playerInDuel);
                }
                catch (Exception ex) {}
            }
        }
        duelPlayers.clear();
    }
    
    @EventHandler
    public void onDuelEnd(final DuelEndEvent e) {
        final Duel duel = e.getDuel();
        if (e.getDuel().getFfaPlayers() == null) {
            this.duelCountdownMap.remove(duel.getUUID());
            final BukkitTask task = this.duelTickThreadMap.get(duel.getUUID());
            task.cancel();
            this.duelTickThreadMap.remove(duel.getUUID());
            if (duel.getFfaPlayers() != null) {
                if (duel.getFfaPartyLeaderUUID() != null) {
                    final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getFfaPartyLeaderUUID());
                    if (party != null) {
                        party.setPartyState(PartyState.LOBBY);
                    }
                }
                final Player player = this.plugin.getServer().getPlayer((UUID)duel.getFfaPlayersAlive().get(0));
                if (player == null) {
                    return;
                }
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            }
            else {
                if (duel.getFirstTeamPartyLeaderUUID() != null) {
                    final Party firstTeamParty = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getFirstTeamPartyLeaderUUID());
                    if (firstTeamParty != null) {
                        firstTeamParty.setPartyState(PartyState.LOBBY);
                    }
                }
                if (duel.getSecondTeamPartyLeaderUUID() != null) {
                    final Party secondTeamParty = this.plugin.getManagerHandler().getPartyManager().getParty(duel.getSecondTeamPartyLeaderUUID());
                    if (secondTeamParty != null) {
                        secondTeamParty.setPartyState(PartyState.LOBBY);
                    }
                }
                for (final UUID uuid : duel.getFirstTeamAlive()) {
                    final Player player2 = this.plugin.getServer().getPlayer(uuid);
                    if (player2 == null) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
                }
                for (final UUID uuid : duel.getSecondTeamAlive()) {
                    final Player player2 = this.plugin.getServer().getPlayer(uuid);
                    if (player2 == null) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
                }
                if (duel.getSpectators().size() > 0) {
                    final Iterator<UUID> iterator = duel.getSpectators().iterator();
                    while (iterator.hasNext()) {
                        final UUID value = iterator.next();
                        final Player player2 = this.plugin.getServer().getPlayer(value);
                        if (player2 == null) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player2);
                        this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player2, false);
                        iterator.remove();
                    }
                }
            }
            this.plugin.getManagerHandler().getDuelManager().destroyDuel(duel);
        }
    }
    
	@EventHandler(ignoreCancelled=true)
	public void onDrop(PlayerDropItemEvent e) {
		if(PlayerState.FIGHTING(e.getPlayer(), Practice.getInstance()) == null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onConsume(PlayerItemConsumeEvent e) {
		if(e.getItem().getType() == Material.POTION) {
			Player p = e.getPlayer();
			new BukkitRunnable() {

				@Override
				public void run() {
					if(p != null) {
						p.getInventory().remove(Material.GLASS_BOTTLE);
					}
				}
			}.runTaskLater(plugin, 1);
		}
	}
}
