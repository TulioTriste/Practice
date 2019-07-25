package me.sebaarkadia.practice.manager.managers;

import java.util.Collections;
import org.bukkit.plugin.Plugin;
import me.sebaarkadia.practice.runnables.UpdateInventoryTask;
import me.sebaarkadia.practice.events.DuelEndingEvent;
import me.sebaarkadia.practice.duel.DuelState;
import me.sebaarkadia.practice.player.PlayerInventorySnapshot;
import me.sebaarkadia.practice.scoreboard.PlayerBoard;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.player.PracticePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.sebaarkadia.practice.events.DuelCreateEvent;
import me.sebaarkadia.practice.scoreboard.sidebar.DuelScoreboardProvider;
import me.sebaarkadia.practice.party.PartyState;
import org.bukkit.Bukkit;
import me.sebaarkadia.practice.listeners.StartListener;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import me.sebaarkadia.practice.player.PlayerState;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.sebaarkadia.practice.player.PlayerKit;
import org.bukkit.inventory.ItemStack;
import me.sebaarkadia.practice.util.UtilItem;
import net.solexgame.solexspigot.SolexSpigot;
import net.solexgame.solexspigot.knockback.KnockbackProfile;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.arena.Arena;
import java.util.HashMap;
import me.sebaarkadia.practice.manager.ManagerHandler;
import java.util.Random;
import me.sebaarkadia.practice.duel.Duel;
import java.util.UUID;
import java.util.Map;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.manager.Manager;

public class DuelManager extends Manager
{
    private Practice plugin;
    private Map<UUID, Duel> uuidIdentifierToDuel;
    private Map<UUID, UUID> playerUUIDToDuelUUID;
    private Random random;
    
    public DuelManager(final Practice plugin, final ManagerHandler handler) {
        super(handler);
        this.plugin = plugin;
        this.uuidIdentifierToDuel = new HashMap<UUID, Duel>();
        this.playerUUIDToDuelUUID = new HashMap<UUID, UUID>();
        this.random = new Random();
    }
    
    public Duel getDuelByUUID(final UUID uuid) {
        return this.uuidIdentifierToDuel.get(uuid);
    }
    
    public void createDuel(final Arena arena, final Kit kit, final boolean ranked, final UUID firstTeamPartyLeaderUUID, final UUID secondTeamPartyLeaderUUID, final List<UUID> firstTeam, final List<UUID> secondTeam, final boolean tournament) {
        final UUID matchUUID = UUID.randomUUID();
        final Duel duel = new Duel(arena.getName(), kit.getName(), matchUUID, ranked, firstTeamPartyLeaderUUID, secondTeamPartyLeaderUUID, firstTeam, secondTeam, tournament);
        this.uuidIdentifierToDuel.put(matchUUID, duel);
        final KnockbackProfile defaultProfile = SolexSpigot.INSTANCE.getConfig().getKbProfileByName("Default");
        final KnockbackProfile comboProfile = SolexSpigot.INSTANCE.getConfig().getKbProfileByName("combo");
        final KnockbackProfile sumoProfile = SolexSpigot.INSTANCE.getConfig().getKbProfileByName("sumo");
        final List<Player> duelPlayers = new ArrayList<Player>();
        final ItemStack defaultBook = UtilItem.createItem(Material.BOOK, 1, (short)1, "§6Default Kit");
		String firstTeamRanked = "";
        String secondTeamRanked = "";
        if (ranked && firstTeam.size() == 1 && secondTeam.size() == 1) {
            firstTeamRanked = "§9" + this.handler.getPlugin().getServer().getPlayer(firstTeam.get(0)).getName() + "§9" + " §fwith " + "§9" + this.handler.getPracticePlayerManager().getPracticePlayer(firstTeam.get(0)).getEloMap().get(kit.getName()) + " elo";
            secondTeamRanked = "§9" + this.handler.getPlugin().getServer().getPlayer(secondTeam.get(0)).getName() + "§9" + " §fwith " + "§9" + this.handler.getPracticePlayerManager().getPracticePlayer(secondTeam.get(0)).getEloMap().get(kit.getName()) + " elo";
        }
        for (final UUID uuid : firstTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            InventoryManager.guiUnranked.remove(player);
            player.setFoodLevel(20);
            player.setHealth(20.0);
            player.setFireTicks(0);
            player.setFallDistance(0.0f);
            player.getInventory().clear();
            player.getInventory().setArmorContents((ItemStack[])null);
            player.updateInventory();
            this.playerUUIDToDuelUUID.put(player.getUniqueId(), matchUUID);
            player.teleport(arena.getFirstTeamLocation());
            final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
            final String kitName = kit.getName();
            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kitName);
            if (playerKitMap != null && !duel.isTournament()) {
                int i = 2;
                player.getInventory().setItem(0, defaultBook);
                for (final PlayerKit playerKit : playerKitMap.values()) {
                    player.getInventory().setItem(i, UtilItem.createItem(Material.BOOK, 1, (short)0, playerKit.getDisplayName()));
                    ++i;
                }
            }
            else {
                player.getInventory().clear();
                player.getInventory().setContents(kit.getMainContents());
                player.getInventory().setArmorContents(kit.getArmorContents());
                player.updateInventory();
            }
            if (kit.getName().contains("Sumo")) {
                player.setWalkSpeed(0.2f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 128));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 128));
            }
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.WAITING);
            practicePlayer.setTeamNumber(1);
            if (kit.isCombo()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                ((CraftPlayer)player).getHandle().setKnockbackProfile(comboProfile);
                player.setMaximumNoDamageTicks(1);
            }
            else if (kit.getName().contains("Sumo")) {
                ((CraftPlayer)player).getHandle().setKnockbackProfile(sumoProfile);
            }
            else {
                ((CraftPlayer)player).getHandle().setKnockbackProfile(defaultProfile);
            }
            final boolean party = secondTeam.size() >= 2;
            practicePlayer.setShowRematchItemFlag(true);
            practicePlayer.setLastDuelPlayer(this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName());
            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            if (settings.isPublicChat()) {
                player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Butterfly Clicking can result in a ban.");
                if (kit.getName().contains("BuildUHC")) {
                    player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Stacking Blocks will result in a ban.");
                }
            }
            else {
                player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "ATENCION " + ChatColor.RED + "el ButterFly clicking es ilegal, y resultara con un ban.");
            }
            StartListener.applyCooldown(player);
            if (settings.isPublicChat()) {
                player.sendMessage("§7Starting duel against §c" + (ranked ? secondTeamRanked : ("§a" + this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            }
            else {
                player.sendMessage("§7Iniciando duel denuevo §9" + (ranked ? secondTeamRanked : ("§9" + this.handler.getPlugin().getServer().getPlayer((UUID)secondTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            }
            if (settings.isSeeAll()) {
                settings.setSeeAll(!settings.isSeeAll());
                for (final Player pls : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(pls);
                }
            }
            player.setAllowFlight(false);
            player.setFlying(false);
            if (kit.getName().contains("NoDebuff") || kit.getName().contains("Debuff")) {
                practicePlayer.setPotion(28);
            }
            else {
                practicePlayer.setPotion(0);
            }
            duelPlayers.add(player);
        }
        for (final UUID uuid : secondTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            InventoryManager.guiUnranked.remove(player);
            player.setFoodLevel(20);
            player.setHealth(20.0);
            player.setFireTicks(0);
            player.setFallDistance(0.0f);
            player.getInventory().clear();
            player.getInventory().setArmorContents((ItemStack[])null);
            player.updateInventory();
            this.playerUUIDToDuelUUID.put(player.getUniqueId(), matchUUID);
            player.teleport(arena.getSecondTeamLocation());
            final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
            final String kitName = kit.getName();
            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kitName);
            if (playerKitMap != null && !duel.isTournament()) {
                int i = 2;
                player.getInventory().setItem(0, defaultBook);
                for (final PlayerKit playerKit : playerKitMap.values()) {
                    player.getInventory().setItem(i, UtilItem.createItem(Material.BOOK, 1, (short)0, playerKit.getDisplayName()));
                    ++i;
                }
            }
            else {
                player.getInventory().clear();
                player.getInventory().setContents(kit.getMainContents());
                player.getInventory().setArmorContents(kit.getArmorContents());
                player.updateInventory();
            }
            if (kit.getName().contains("Sumo")) {
                player.setWalkSpeed(0.2f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 128));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 128));
            }
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.WAITING);
            practicePlayer.setTeamNumber(2);
            if (kit.isCombo()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
                ((CraftPlayer)player).getHandle().setKnockbackProfile(comboProfile);
                player.setMaximumNoDamageTicks(1);
            }
            else if (kit.getName().contains("Sumo")) {
                ((CraftPlayer)player).getHandle().setKnockbackProfile(sumoProfile);
            }
            else {
                ((CraftPlayer)player).getHandle().setKnockbackProfile(defaultProfile);
            }
            final boolean party = firstTeam.size() >= 2;
            practicePlayer.setShowRematchItemFlag(true);
            try {
                practicePlayer.setLastDuelPlayer(this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName());
            }
            catch (Exception ex) {
                return;
            }
            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            if (settings.isPublicChat()) {
                player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Butterfly Clicking can result in a ban.");
                if (kit.getName().contains("BuildUHC")) {
                    player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Stacking Blocks will result in a ban.");
                }
            }
            else {
                player.sendMessage(String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "ATENCION " + ChatColor.RED + "el ButterFly clicking es ilegal, y resultara con un ban.");
            }
            StartListener.applyCooldown(player);
            if (settings.isPublicChat()) {
                player.sendMessage("§7Starting duel against §c" + (ranked ? secondTeamRanked : ("§a" + this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            }
            else {
                player.sendMessage("§7Iniciando duel denuevo §c" + (ranked ? secondTeamRanked : ("§a" + this.handler.getPlugin().getServer().getPlayer((UUID)firstTeam.get(0)).getName())) + (party ? (ChatColor.YELLOW + "'s party.") : ""));
            }
            if (settings.isSeeAll()) {
                settings.setSeeAll(!settings.isSeeAll());
                for (final Player pls : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(pls);
                }
            }
            player.setAllowFlight(false);
            player.setFlying(false);
            if (kit.getName().contains("NoDebuff") || kit.getName().contains("Debuff")) {
                practicePlayer.setPotion(28);
            }
            else {
                practicePlayer.setPotion(0);
            }
            duelPlayers.add(player);
        }
        if (firstTeamPartyLeaderUUID != null) {
            final Party party2 = this.handler.getPartyManager().getParty(firstTeamPartyLeaderUUID);
            if (party2 != null) {
                party2.setPartyState(PartyState.DUELING);
            }
        }
        if (secondTeamPartyLeaderUUID != null) {
            final Party party2 = this.handler.getPartyManager().getParty(secondTeamPartyLeaderUUID);
            if (party2 != null) {
                party2.setPartyState(PartyState.DUELING);
            }
        }
        for (final Player player2 : duelPlayers) {
            for (final Player player3 : duelPlayers) {
                player2.showPlayer(player3);
            }
        }
        for (final UUID uuid : firstTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard == null) {
                continue;
            }
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.handler.getPlugin()), 1L);
        }
        for (final UUID uuid : secondTeam) {
            final Player player = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (player == null) {
                continue;
            }
            final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
            if (playerBoard == null) {
                continue;
            }
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.handler.getPlugin()), 1L);
        }
        for (final Player playerInDuel : duelPlayers) {
            final PlayerBoard playerBoard2 = this.handler.getScoreboardHandler().getPlayerBoard(playerInDuel.getUniqueId());
            if (playerBoard2 != null) {
                playerBoard2.addUpdates(playerInDuel);
            }
        }
        duelPlayers.clear();
        this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelCreateEvent(duel));
    }
    
    public Duel getDuelFromPlayer(final UUID uuid) {
        final UUID matchUUID = this.playerUUIDToDuelUUID.get(uuid);
        return this.uuidIdentifierToDuel.get(matchUUID);
    }
    
    public void removePlayerFromDuel(final Player player) {
        final Duel currentDuel = this.getDuelFromPlayer(player.getUniqueId());
        this.playerUUIDToDuelUUID.remove(player.getUniqueId());
        if (currentDuel == null) {
            return;
        }
        final PlayerBoard playerBoard = Practice.getInstance().getManagerHandler().getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        final PlayerInventorySnapshot playerInventorySnapshot = new PlayerInventorySnapshot(player);
        currentDuel.addSnapshot(player.getUniqueId(), playerInventorySnapshot);
        if (currentDuel.getFfaPlayers() != null) {
            currentDuel.killPlayerFFA(player.getUniqueId());
            for (final UUID uuid : currentDuel.getFfaPlayers()) {
                final Player other = Bukkit.getPlayer(uuid);
                if (playerBoard != null) {
                    playerBoard.addUpdates(other);
                }
            }
            if (currentDuel.getFfaPlayersAlive().size() == 1) {
                final Player lastPlayer = this.handler.getPlugin().getServer().getPlayer((UUID)currentDuel.getFfaPlayersAlive().get(0));
                final PlayerInventorySnapshot lastPlayerSnapshot = new PlayerInventorySnapshot(lastPlayer);
                final UUID lastPlayerSnapUUID = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(lastPlayer.getUniqueId(), lastPlayerSnapUUID);
                this.handler.getInventorySnapshotManager().addSnapshot(lastPlayerSnapUUID, lastPlayerSnapshot);
                for (final Map.Entry<UUID, PlayerInventorySnapshot> entry : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                    final UUID playerUUID = entry.getKey();
                    final PlayerInventorySnapshot pSnapshot = entry.getValue();
                    final UUID snapUUID = UUID.randomUUID();
                    currentDuel.addUUIDSnapshot(playerUUID, snapUUID);
                    this.handler.getInventorySnapshotManager().addSnapshot(snapUUID, pSnapshot);
                }
                currentDuel.setDuelState(DuelState.ENDING);
                try {
                    player.getKiller().getInventory().setArmorContents((ItemStack[])null);
                    player.getKiller().getInventory().clear();
                }
                catch (Exception ex) {}
                currentDuel.setEndMatchTime(System.currentTimeMillis());
                this.playerUUIDToDuelUUID.remove(currentDuel.getFfaPlayersAlive().get(0));
                this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel));
            }
            return;
        }
        for (final UUID uuid : currentDuel.getFirstTeam()) {
            final Player other = Bukkit.getPlayer(uuid);
            if (other != null && playerBoard != null) {
                playerBoard.addUpdates(other);
            }
        }
        for (final UUID uuid : currentDuel.getSecondTeam()) {
            final Player other = Bukkit.getPlayer(uuid);
            if (other != null && playerBoard != null) {
                playerBoard.addUpdates(other);
            }
        }
        final int teamNumber = this.handler.getPracticePlayerManager().getPracticePlayer(player).getTeamNumber();
        if (teamNumber == 1) {
            currentDuel.killPlayerFirstTeam(player.getUniqueId());
        }
        else {
            currentDuel.killPlayerSecondTeam(player.getUniqueId());
        }
        if (currentDuel.getFirstTeamAlive().size() == 0) {
            for (final UUID lastPlayersUUID : currentDuel.getSecondTeamAlive()) {
                final Player lastPlayers = this.handler.getPlugin().getServer().getPlayer(lastPlayersUUID);
                final PlayerInventorySnapshot lastPlayerSnapshot2 = new PlayerInventorySnapshot(lastPlayers);
                currentDuel.addSnapshot(lastPlayers.getUniqueId(), lastPlayerSnapshot2);
            }
            for (final Map.Entry<UUID, PlayerInventorySnapshot> entry2 : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                final UUID playerUUID2 = entry2.getKey();
                final PlayerInventorySnapshot pSnapshot2 = entry2.getValue();
                final UUID snapUUID2 = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(playerUUID2, snapUUID2);
                this.handler.getInventorySnapshotManager().addSnapshot(snapUUID2, pSnapshot2);
            }
            currentDuel.setDuelState(DuelState.ENDING);
            try {
                player.getKiller().getInventory().setArmorContents((ItemStack[])null);
                player.getKiller().getInventory().clear();
            }
            catch (Exception ex2) {}
            currentDuel.setEndMatchTime(System.currentTimeMillis());
            this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel, 2));
            this.playerUUIDToDuelUUID.remove(currentDuel.getSecondTeamAlive().get(0));
        }
        else if (currentDuel.getSecondTeamAlive().size() == 0) {
            for (final UUID lastPlayersUUID : currentDuel.getFirstTeamAlive()) {
                final Player lastPlayers = this.handler.getPlugin().getServer().getPlayer(lastPlayersUUID);
                final PlayerInventorySnapshot lastPlayerSnapshot2 = new PlayerInventorySnapshot(lastPlayers);
                currentDuel.addSnapshot(lastPlayers.getUniqueId(), lastPlayerSnapshot2);
            }
            for (final Map.Entry<UUID, PlayerInventorySnapshot> entry2 : currentDuel.getPlayerUUIDToSnapshotMap().entrySet()) {
                final UUID playerUUID2 = entry2.getKey();
                final PlayerInventorySnapshot pSnapshot2 = entry2.getValue();
                final UUID snapUUID2 = UUID.randomUUID();
                currentDuel.addUUIDSnapshot(playerUUID2, snapUUID2);
                this.handler.getInventorySnapshotManager().addSnapshot(snapUUID2, pSnapshot2);
            }
            currentDuel.setDuelState(DuelState.ENDING);
            try {
                player.getKiller().getInventory().setArmorContents((ItemStack[])null);
                player.getKiller().getInventory().clear();
            }
            catch (Exception ex3) {}
            currentDuel.setEndMatchTime(System.currentTimeMillis());
            this.handler.getPlugin().getServer().getPluginManager().callEvent((Event)new DuelEndingEvent(currentDuel, 1));
            this.playerUUIDToDuelUUID.remove(currentDuel.getFirstTeamAlive().get(0));
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public void destroyDuel(final Duel duel) {
        this.uuidIdentifierToDuel.remove(duel.getUUID());
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.UNRANKED_PARTY));
    }
    
    public int getRankedDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && duel.isRanked()) {
                ++count;
            }
        }
        return count;
    }
    
    public int getRankedPartyDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && duel.isRanked() && duel.getFirstTeam().size() >= 2) {
                ++count;
            }
        }
        return count;
    }
    
    public int getUnRankedDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && !duel.isRanked()) {
                ++count;
            }
        }
        return count;
    }
    
    public int getUnRankedPartyDuelsFromKit(final String kitName) {
        int count = 0;
        for (final Duel duel : this.uuidIdentifierToDuel.values()) {
            if (duel.getKitName().equalsIgnoreCase(kitName) && !duel.isRanked() && duel.getFirstTeam().size() >= 2) {
                ++count;
            }
        }
        return count;
    }
    
    public Duel getRandomDuel() {
        final List<Duel> list = new ArrayList<Duel>(this.uuidIdentifierToDuel.values());
        Collections.shuffle(list);
        final Duel duel = list.get(this.random.nextInt(list.size()));
        if (duel != null) {
            return duel;
        }
        return null;
    }
    
    public Map<UUID, Duel> getUuidIdentifierToDuel() {
        return this.uuidIdentifierToDuel;
    }
    
    public Map<UUID, UUID> getPlayerUUIDToDuelUUID() {
        return this.playerUUIDToDuelUUID;
    }
}
