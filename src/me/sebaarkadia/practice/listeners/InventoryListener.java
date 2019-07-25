package me.sebaarkadia.practice.listeners;

import java.util.Set;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.EventHandler;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.manager.managers.QueueManager;
import org.bukkit.inventory.Inventory;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.player.PracticePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.sebaarkadia.practice.util.UtilItem;
import me.sebaarkadia.practice.player.PlayerKit;
import java.util.Map;
import org.bukkit.command.CommandSender;
import me.sebaarkadia.practice.util.UtilString;
import org.bukkit.plugin.Plugin;
import me.sebaarkadia.practice.runnables.UpdateInventoryTask;
import me.sebaarkadia.practice.util.UtilPlayer;
import java.util.Collections;
import me.sebaarkadia.practice.tournament.TournamentTeam;
import me.sebaarkadia.practice.util.PlayerUtility;
import me.sebaarkadia.practice.commands.HostCommand;
import me.sebaarkadia.practice.tournament.Tournament;
import org.bukkit.Material;
import me.sebaarkadia.practice.util.UtilActionMessage;
import me.sebaarkadia.practice.duel.DuelRequest;
import me.sebaarkadia.practice.manager.managers.InventoryManager;
import org.bukkit.event.Event;
import java.util.List;
import me.sebaarkadia.practice.events.DuelPreCreateEvent;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import me.sebaarkadia.practice.kit.Kit;
import org.bukkit.event.block.Action;
import me.sebaarkadia.practice.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.sebaarkadia.practice.Practice;
import org.bukkit.event.Listener;

public class InventoryListener implements Listener
{
    private Practice plugin;
    
    public InventoryListener(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unlikely-arg-type")
	@EventHandler
    public void InventoryClickEvent(final InventoryClickEvent e) {
        final Player player = (Player)e.getWhoClicked();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final String invTitle = e.getInventory().getTitle().toLowerCase();
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (settings.isMod() || practicePlayer.getCurrentState() == PlayerState.LOBBY || practicePlayer.getCurrentState() == PlayerState.SPECTATING || practicePlayer.getCurrentState() == PlayerState.QUEUE) {
            e.setCancelled(true);
        }
        if (invTitle.contains("inventory")) {
            e.setCancelled(true);
            return;
        }
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        if (!e.getCurrentItem().hasItemMeta() || e.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }
        final ItemStack itemClicked = e.getCurrentItem();
        final Inventory inventory = e.getInventory();
        if (practicePlayer.getCurrentState() == PlayerState.LOBBY || practicePlayer.getCurrentState() == PlayerState.QUEUE || practicePlayer.getCurrentState() == PlayerState.EDITING) {
            if (invTitle.contains("unranked queue")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final String kitName = kit.getName();
                    final QueueManager queueManager = this.plugin.getManagerHandler().getQueueManager();
                    final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    if (party2 != null) {
                        if (party2.getSize() != 2) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There must be at least 2 players in your party to do this." : "Se necesitan 2 players para esto."));
                            return;
                        }
                        if (queueManager.isUnrankedPartyQueueEmpty(kitName)) {
                            player.closeInventory();
                            queueManager.addToPartyUnrankedQueue(kitName, player.getUniqueId());
                            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, "§fYour party are now queued for §9Unranked 2v2 " + kitName + "§f.");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            final PracticePlayer partyMember = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(party2.getMembers().get(0));
                            partyMember.setCurrentState(PlayerState.QUEUE);
                            final Player partyPlayer = this.plugin.getServer().getPlayer(partyMember.getUUID());
                            partyPlayer.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            partyPlayer.updateInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                        else {
                            player.closeInventory();
                            final UUID targetLeaderUUID = queueManager.getQueuedForPartyUnrankedQueue(kitName);
                            queueManager.removePartyFromPartyUnrankedQueue(kitName);
                            final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
                            final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
                            final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(targetLeaderUUID);
                            firstTeam.add(player.getUniqueId());
                            firstTeam.add(party2.getMembers().get(0));
                            secondTeam.add(targetLeaderUUID);
                            secondTeam.add(targetParty.getMembers().get(0));
                            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, player.getUniqueId(), targetLeaderUUID, firstTeam, secondTeam, false));
                        }
                    }
                    else if (queueManager.isUnrankedQueueEmpty(kitName)) {
                        player.closeInventory();
                        player.updateInventory();
                        queueManager.addToUnrankedQueue(kitName, player.getUniqueId());
                        if (settings.isPublicChat()) {
                            player.sendMessage("§7Queued for §cunranked §a" + kitName + "§7.");
                        }
                        else {
                            player.sendMessage("§7En la Cola de §a" + kitName + "§7 en §cunranked.");
                        }
                        InventoryManager.guiUnranked.remove(player);
                        practicePlayer.setCurrentState(PlayerState.QUEUE);
                        player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getQueueItems());
                    }
                    else {
                        player.closeInventory();
                        final UUID queuedUuid = queueManager.getQueuedForUnrankedQueue(kitName);
                        queueManager.removePlayerFromUnrankedQueue(kitName);
                        final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
                        final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
                        firstTeam.add(queuedUuid);
                        secondTeam.add(player.getUniqueId());
                        this.plugin.getServer().getPluginManager().callEvent(new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
                    }
                }
            }
            else if (invTitle.contains("ranked queue")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final String kitName = kit.getName();
                    final QueueManager queueManager = this.plugin.getManagerHandler().getQueueManager();
                    final int playerElo = practicePlayer.getEloMap().get(kitName);
                    if (queueManager.isRankedQueueEmpty(kitName)) {
                        player.closeInventory();
                        queueManager.addToRankedQueue(kitName, player.getUniqueId());
                        if (settings.isPublicChat()) {
                            player.sendMessage("§eYou are now queued for §aRanked " + kitName + "§f. §9[" + playerElo + " elo]");
                        }
                        else {
                            player.sendMessage("§eEstas en la cola de §a" + kitName + "§e en §aranked. §9[" + playerElo + " elo]");
                        }
                        practicePlayer.setCurrentState(PlayerState.QUEUE);
                        player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                        player.updateInventory();
                    }
                    else {
                        player.closeInventory();
                        for (final UUID queuedUuid2 : queueManager.getQueuedForRankedQueue(kitName)) {
                            final PracticePlayer queuedPracticePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(queuedUuid2);
                            final int queuedElo = queuedPracticePlayer2.getEloMap().get(kitName);
                            final int diff2 = Math.abs(queuedElo - playerElo);
                            if (diff2 < 325) {
                                this.plugin.getManagerHandler().getQueueManager().removePlayerFromRankedQueue(kitName, queuedUuid2);
                                final List<UUID> firstTeam2 = new ArrayList<UUID>();
                                final List<UUID> secondTeam2 = new ArrayList<UUID>();
                                firstTeam2.add(queuedUuid2);
                                secondTeam2.add(player.getUniqueId());
                                this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, true, null, null, firstTeam2, secondTeam2, false));
                                return;
                            }
                        }
                        queueManager.addToRankedQueue(kitName, player.getUniqueId());
                        if (settings.isPublicChat()) {
                            player.sendMessage("§eYou are now queued for §aRanked " + kitName + "§f. §9[" + playerElo + " elo]");
                        }
                        else {
                            player.sendMessage("§eEstas en la cola de " + kitName + "§e en §aranked. §9[" + playerElo + " elo]");
                        }
                        practicePlayer.setCurrentState(PlayerState.QUEUE);
                        player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                        player.updateInventory();
                    }
                }
            }
            else if (invTitle.contains("kit editor")) {
                e.setCancelled(true);
                player.closeInventory();
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    Practice.getInstance().getManagerHandler().getEditorManager().addEditingKit(player.getUniqueId(), kit);
                    practicePlayer.setCurrentState(PlayerState.EDITING);
                    player.teleport(Practice.getInstance().geteditkit());
                    player.getInventory().clear();
                    player.updateInventory();
                    player.sendMessage(ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? ("You are now editing " + kit.getName()) : "Estas editando el kit "));
                    Practice.getInstance().npc(player);
                }
            }
            else if (invTitle.contains("send request")) {
                e.setCancelled(true);
                final Player target = this.plugin.getServer().getPlayer(this.plugin.getManagerHandler().getInventoryManager().getSelectingDuelPlayerUUID(player.getUniqueId()));
                final Kit kit2 = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit2 != null) {
                    final String kitName2 = kit2.getName();
                    if (target == null) {
                        player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
                        player.closeInventory();
                        return;
                    }
                    final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
                    player.closeInventory();
                    this.plugin.getManagerHandler().getInventoryManager().removeSelectingDuel(player.getUniqueId());
                    if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
                        player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is bussy." : "§cEl jugador esta ocupado."));
                        return;
                    }
                    final Party party3 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    final Settings settings2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target.getUniqueId()).getSettings();
                    if (settings.isPublicChat()) {
                        final Party targetParty2;
                        player.sendMessage("§7Sent a duel request to §c" + target.getName() + (((targetParty2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId())) != null) ? ("§f's party §9(" + (targetParty2.getMembers().size() + 1) + ")") : "") + "§7" + " with the kit " + "§c" + kitName2 + "§7" + "!");
                    }
                    else {
                        final Party targetParty2;
                        player.sendMessage("§fEnviar una solicitud de duelo ha §9" + target.getName() + (((targetParty2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId())) != null) ? ("§f's party §9(" + (targetParty2.getMembers().size() + 1) + ")") : "") + "§f" + " en " + "§9" + kitName2 + "§f" + "!");
                    }
                    this.plugin.getManagerHandler().getRequestManager().addDuelRequest(target, player, new DuelRequest(kitName2));
                    final UtilActionMessage actionMessage = new UtilActionMessage();
                    if (settings2.isPublicChat()) {
                        target.sendMessage("§c" + player.getName() + ((party3 != null) ? ("§c's party §9(" + (party3.getMembers().size() + 1) + ")") : "§c") + "§7" + " wants to have a " + "§a" + kitName2 + "§7" + " duel.");
                        actionMessage.addText(ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/accept " + player.getName());
                        actionMessage.sendToPlayer(target);
                    }
                    else {
                        target.sendMessage("§f" + player.getName() + ((party3 != null) ? ("§f's party §9(" + (party3.getMembers().size() + 1) + ")") : "") + "§c" + " le ha enviado un duel a " + "§9" + kitName2 + "§f" + ".");
                        actionMessage.addText(ChatColor.RED + "[Click aqui para aceptar]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/accept " + player.getName());
                        actionMessage.sendToPlayer(target);
                    }
                }
            }
            else if (invTitle.contains("split fights")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final Party party4 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    player.closeInventory();
                    final ArrayList<UUID> firstTeam3 = new ArrayList<UUID>();
                    final ArrayList<UUID> secondTeam3 = new ArrayList<UUID>();
                    firstTeam3.add(party4.getLeader());
                    for (final UUID member : party4.getMembers()) {
                        if (firstTeam3.size() == secondTeam3.size()) {
                            firstTeam3.add(member);
                        }
                        else {
                            if (firstTeam3.size() <= secondTeam3.size()) {
                                continue;
                            }
                            secondTeam3.add(member);
                        }
                    }
                    this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party4.getLeader(), party4.getLeader(), firstTeam3, secondTeam3, false));
                }
            }
            else if (invTitle.contains("tournament size")) {
                e.setCancelled(true);
                if (itemClicked.getType() != Material.NETHER_STAR) {
                    player.closeInventory();
                    return;
                }
                final boolean isPlayerTournament = ChatColor.stripColor(itemClicked.getItemMeta().getLore().get(0).split(": ")[1]).equalsIgnoreCase("Player");
                final Kit kit3 = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getLore().get(1).split(": ")[1]));
                final int size = Integer.parseInt(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName().split("v")[0]));
                if (!player.hasPermission("practice.commands.host")) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? ("You don't have permissions to host (" + size + "v" + size + ")") : "No tienes permisos para hostear ("));
                    player.closeInventory();
                    return;
                }
                if (isPlayerTournament && Tournament.getTournaments().size() >= 1) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There is a tournament currently running." : "Un torneo ya esta activo."));
                    player.closeInventory();
                    return;
                }
                if (!isPlayerTournament) {
                    Tournament.forceEndPlayerTournaments();
                }
                final Tournament tournament = new Tournament(size, kit3, isPlayerTournament);
                if (isPlayerTournament) {
                    practicePlayer.setHostCooldown(System.currentTimeMillis() + 3600000L);
                    HostCommand.getRunningTournaments().put(player.getUniqueId(), tournament);
                }
                final UtilActionMessage actionMessage2 = new UtilActionMessage();
                actionMessage2.addText("§7[§dEvent " + String.valueOf(tournament.getMaximumPerTeam()) + "v" + String.valueOf(tournament.getMaximumPerTeam()) + "§7]" + " §fJoin event with the command §f/join or §c[Click here]").addHoverText(ChatColor.GRAY + "Click to join the tournament").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/join " + Tournament.getTournaments().size());
                for (final Player online : PlayerUtility.getOnlinePlayers()) {
                    actionMessage2.sendToPlayer(online);
                }
                player.closeInventory();
            }
            else if (invTitle.contains("available tournaments")) {
                e.setCancelled(true);
                if (itemClicked.getType() != Material.IRON_SWORD) {
                    player.closeInventory();
                    return;
                }
                final Tournament tournament2 = Tournament.getTournaments().get(e.getSlot());
                if (tournament2 == null) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "That tournament doesn't exist." : "Este tournament no existe."));
                    player.closeInventory();
                    return;
                }
                for (final Tournament tourney : Tournament.getTournaments()) {
                    if (!tourney.isInTournament(player)) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are currently in another tournament." : "Ya estas anotado en el tournament."));
                    player.closeInventory();
                    return;
                }
                if (tournament2.isStarted()) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Sorry! The tournament already started." : "El tournament ya ha iniciado."));
                    return;
                }
                if (tournament2.getTotalPlayersInTournament() == tournament2.getPlayersLimit()) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Sorry! The tournament is already full." : "El tournament esta lleno."));
                    player.closeInventory();
                    return;
                }
                if (tournament2.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are already in the tournament." : "Ya estas en el tournament."));
                    player.closeInventory();
                    return;
                }
                if (tournament2.getMaximumPerTeam() == 1) {
                    final TournamentTeam tournamentTeam = new TournamentTeam();
                    tournamentTeam.setPlayers(Collections.singletonList(player.getUniqueId()));
                    tournament2.getTeams().add(tournamentTeam);
                    UtilPlayer.clear2(player);
                    player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getTourItems());
                    tournament2.sendMessage(ChatColor.GREEN + player.getName() + " has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
                    player.closeInventory();
                }
                else if (tournament2.getMaximumPerTeam() >= 2) {
                    final Party party5 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    if (party5 == null) {
                        player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You must be in a party to join this tournament." : "Debes estar en una party para entrar al tournament."));
                        player.closeInventory();
                        return;
                    }
                    if (party5.getLeader() != player.getUniqueId()) {
                        player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Only the leader can join the tournament." : "Solamente el jefe puede entrar al tournament."));
                        player.closeInventory();
                        return;
                    }
                    if (party5.getSize() != tournament2.getMaximumPerTeam()) {
                        if (settings.isPublicChat()) {
                            player.sendMessage(ChatColor.RED + "The party must have only " + tournament2.getMaximumPerTeam() + " players.");
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "La party debe tener " + tournament2.getMaximumPerTeam() + " players.");
                        }
                        player.closeInventory();
                        return;
                    }
                    final TournamentTeam tournamentTeam2 = new TournamentTeam();
                    tournamentTeam2.setPlayers(party5.getAllMembersOnline());
                    tournament2.getTeams().add(tournamentTeam2);
                    UtilPlayer.clear2(player);
                    player.getInventory().setContents(Practice.getInstance().getManagerHandler().getItemManager().getTourItems());
                    tournament2.sendMessage(ChatColor.YELLOW + player.getName() + "'s Party has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
                    player.closeInventory();
                }
            }
            else if (invTitle.contains("fight other parties") && itemClicked.getType() == Material.SKULL_ITEM) {
                e.setCancelled(true);
                final String[] itemName = UtilString.splitString(itemClicked.getItemMeta().getDisplayName());
                itemName[0] = ChatColor.stripColor(itemName[0]);
                player.closeInventory();
                this.plugin.getServer().dispatchCommand((CommandSender)player, "duel " + itemName[0]);
            }
            else if (invTitle.toLowerCase().contains("kit layout")) {
                final String kitName3 = this.plugin.getManagerHandler().getEditorManager().getPlayerEditingKit(player.getUniqueId());
                final Map<Integer, PlayerKit> kitMap = practicePlayer.getKitMap().get(kitName3);
                e.setCancelled(true);
                if (!itemClicked.hasItemMeta() || !itemClicked.getItemMeta().hasDisplayName()) {
                    return;
                }
                if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                    if (itemClicked.getType() == Material.CHEST) {
                        if (settings.isPublicChat()) {
                            final int kitIndex = e.getSlot();
                            final ItemStack loadedKit = UtilItem.createItem(Material.ENDER_CHEST, 1, (short)0, "§fKIT: §9" + kitName3 + " #" + kitIndex);
                            final ItemStack load = UtilItem.createItem(Material.BOOK, 1, (short)0, "§fLoad Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack save1 = UtilItem.createItem(Material.INK_SACK, 1, (short)10, "§fSave Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack rename = UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§fRename Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack delete = UtilItem.createItem(Material.FLINT, 1, (short)0, "§fDelete Kit §9" + kitName3 + " #" + kitIndex);
                            final PlayerKit playerKit = new PlayerKit(kitName3, kitIndex, "§fKit: " + kitName3 + " #" + kitIndex, new ItemStack[0], new ItemStack[0]);
                            playerKit.setMainContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                            playerKit.setArmorContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getArmorContents());
                            practicePlayer.addKit(kitName3, kitIndex, playerKit);
                            player.sendMessage("§fSuccessfully created Kit §9#" + kitIndex);
                            inventory.setItem(e.getSlot(), loadedKit);
                            inventory.setItem(e.getSlot() + 9, load);
                            inventory.setItem(e.getSlot() + 18, save1);
                            inventory.setItem(e.getSlot() + 27, rename);
                            inventory.setItem(e.getSlot() + 36, delete);
                            player.closeInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                            player.getInventory().setArmorContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getArmorContents());
                            player.updateInventory();
                        }
                        else {
                            final int kitIndex = e.getSlot();
                            final ItemStack loadedKit = UtilItem.createItem(Material.ENDER_CHEST, 1, (short)0, "§fKIT: §9" + kitName3 + " #" + kitIndex);
                            final ItemStack load = UtilItem.createItem(Material.BOOK, 1, (short)0, "§fCargar Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack save1 = UtilItem.createItem(Material.INK_SACK, 1, (short)10, "§fGuardar el Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack rename = UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§fRenombrar el Kit §9" + kitName3 + " #" + kitIndex);
                            final ItemStack delete = UtilItem.createItem(Material.FLINT, 1, (short)0, "§fElimitar el Kit §9" + kitName3 + " #" + kitIndex);
                            final PlayerKit playerKit = new PlayerKit(kitName3, kitIndex, "§fKit: " + kitName3 + " #" + kitIndex, new ItemStack[0], new ItemStack[0]);
                            playerKit.setMainContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                            playerKit.setArmorContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getArmorContents());
                            practicePlayer.addKit(kitName3, kitIndex, playerKit);
                            player.sendMessage("§fCreador correctamente el Kit §9#" + kitIndex);
                            inventory.setItem(e.getSlot(), loadedKit);
                            inventory.setItem(e.getSlot() + 9, load);
                            inventory.setItem(e.getSlot() + 18, save1);
                            inventory.setItem(e.getSlot() + 27, rename);
                            inventory.setItem(e.getSlot() + 36, delete);
                            player.closeInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                            player.getInventory().setArmorContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getArmorContents());
                            player.updateInventory();
                        }
                    }
                    else if (itemClicked.getType() == Material.BOOK) {
                        player.closeInventory();
                        final int kitIndex = e.getSlot() - 9;
                        if (kitMap != null && kitMap.containsKey(kitIndex)) {
                            UtilPlayer.clear(player);
                            if (kitMap.get(kitIndex).getMainContents().length > 0) {
                                player.getInventory().setContents(kitMap.get(kitIndex).getMainContents());
                                player.getInventory().setArmorContents(kitMap.get(kitIndex).getArmorContents());
                            }
                            else {
                                player.getInventory().setContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                                player.getInventory().setArmorContents(kitMap.get(kitIndex).getArmorContents());
                            }
                            player.updateInventory();
                            player.closeInventory();
                        }
                    }
                    else if (itemClicked.getType() == Material.INK_SACK) {
                        final int kitIndex = e.getSlot() - 18;
                        if (kitMap != null && kitMap.containsKey(kitIndex)) {
                            kitMap.get(kitIndex).setMainContents(e.getView().getBottomInventory().getContents());
                            if (settings.isPublicChat()) {
                                player.sendMessage("§fSuccessfully saved Kit §9#" + kitIndex);
                            }
                            else {
                                player.sendMessage("§fGuardado correctamente el Kit §9#" + kitIndex);
                            }
                            player.closeInventory();
                        }
                    }
                    else if (itemClicked.getType() == Material.NAME_TAG) {
                        final int kitIndex = e.getSlot() - 27;
                        if (kitMap != null && kitMap.containsKey(kitIndex)) {
                            this.plugin.getManagerHandler().getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
                            player.closeInventory();
                            player.sendMessage("§9" + String.valueOf(settings.isPublicChat() ? "Enter the name you want this kit to be." : "Inserta el nombre para este kit."));
                        }
                    }
                    else if (itemClicked.getType() == Material.FLINT) {
                        if (settings.isPublicChat()) {
                            final int kitIndex = e.getSlot() - 36;
                            if (kitMap != null && kitMap.containsKey(kitIndex)) {
                                this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(player.getUniqueId());
                                kitMap.remove(kitIndex);
                                final ItemStack save2 = UtilItem.createItem(Material.CHEST, 1, (short)0, "§fCreate Kit §9" + kitName3 + " #" + kitIndex);
                                inventory.setItem(e.getSlot(), null);
                                inventory.setItem(e.getSlot() - 9, null);
                                inventory.setItem(e.getSlot() - 18, null);
                                inventory.setItem(e.getSlot() - 27, null);
                                inventory.setItem(e.getSlot() - 36, null);
                                inventory.setItem(kitIndex, save2);
                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);
                                player.updateInventory();
                                player.closeInventory();
                            }
                            else {
                                this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(player.getUniqueId());
                                kitMap.remove(kitIndex);
                                final ItemStack save2 = UtilItem.createItem(Material.CHEST, 1, (short)0, "§fCrear kit §9" + kitName3 + " #" + kitIndex);
                                inventory.setItem(e.getSlot(), null);
                                inventory.setItem(e.getSlot() - 9, null);
                                inventory.setItem(e.getSlot() - 18, null);
                                inventory.setItem(e.getSlot() - 27, null);
                                inventory.setItem(e.getSlot() - 36, null);
                                inventory.setItem(kitIndex, save2);
                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);
                                player.updateInventory();
                                player.closeInventory();
                            }
                        }
                    }
                    else if (itemClicked.getType() == Material.STAINED_GLASS_PANE) {
                        e.setCancelled(true);
                    }
                }
            }
        }
        else if (practicePlayer.getCurrentState() == PlayerState.WAITING || practicePlayer.getCurrentState() == PlayerState.FIGHTING) {
            if (itemClicked != null && itemClicked.getType() == Material.BOOK) {
                e.setCancelled(true);
            }
            if (e.getClick() == ClickType.NUMBER_KEY && player.getInventory().getItem(e.getHotbarButton()) != null && player.getInventory().getItem(e.getHotbarButton()).getType() == Material.BOOK) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void InventoryDragEvent(final InventoryDragEvent event) {
        if (this.isTopInventory(event) && event.getView().getTopInventory().getTitle().toLowerCase().contains("editing kit")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void InventoryCloseEvent(final InventoryCloseEvent e) {
        if (e.getInventory().getTitle().toLowerCase().contains("send request")) {
            this.plugin.getManagerHandler().getInventoryManager().removeSelectingDuel(e.getPlayer().getUniqueId());
        }
        if (InventoryManager.guiUnranked.contains(e.getPlayer())) {
            InventoryManager.guiUnranked.remove(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("Settings")) {
            event.setCancelled(true);
            final String enabled = ChatColor.GRAY + " " + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
            final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(((Player)event.getWhoClicked()).getUniqueId()).getSettings();
            final int slot = event.getSlot();
            ArrayList<String> lore = Lists.newArrayList();
            if (slot == 0) {
                settings.setScoreboard(!settings.isScoreboard());
                ((Player)event.getWhoClicked()).performCommand("scoreboard");
                lore = Lists.newArrayList();
                if (settings.isScoreboard()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                final String name = "§9Scoreboard";
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(0, create(Material.PAINTING, name, lore));
            }
            else if (slot == 2) {
                settings.setDuelRequests(!settings.isDuelRequests());
                lore = Lists.newArrayList();
                if (settings.isDuelRequests()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                final String name = "§9" + String.valueOf(settings.isPublicChat() ? "Duel Requests" : "Recibo de duelos");
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(2, create(Material.DIAMOND_SWORD, (short)0, name, lore));
            }
            else if (slot == 4) {
                settings.setMessage(!settings.isMessage());
                ((Player)event.getWhoClicked()).performCommand("famousmod");
                lore = Lists.newArrayList();
                if (settings.isMessage()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                final String name = "§9" + String.valueOf(settings.isPublicChat() ? "Private Message" : "Mensajes Privados");
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(4, create(Material.FEATHER, name, lore));
            }
            else if (slot == 6) {
                settings.setPublicChat(!settings.isPublicChat());
                lore = Lists.newArrayList();
                if (settings.isPublicChat()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + "English");
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + "Español");
                }
                String name = "§9Language";
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(6, create(Material.ITEM_FRAME, name, lore));
                lore = Lists.newArrayList();
                if (settings.isScoreboard()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(0, create(Material.PAINTING, "§9Scoreboard", lore));
                lore = Lists.newArrayList();
                if (settings.isDuelRequests()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(2, create(Material.DIAMOND_SWORD, (short)0, "§9" + String.valueOf(settings.isPublicChat() ? "Duel Requests" : "Recibo de duelos"), lore));
                lore = Lists.newArrayList();
                if (settings.isMessage()) {
                    lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                }
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(4, create(Material.FEATHER, "§9" + String.valueOf(settings.isPublicChat() ? "Private Message" : "Mensajes Privados"), lore));
                lore = Lists.newArrayList();
                if (((Player)event.getWhoClicked()).hasPermission("practice.fly")) {
                    if (settings.isTime()) {
                        lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                    }
                    else {
                        lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                    }
                }
                else {
                    lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "/buy" : "/buy"));
                }
                name = "§9" + String.valueOf(settings.isPublicChat() ? "Fly" : "Volar");
                ((Player)event.getWhoClicked()).getOpenInventory().setItem(8, create(Material.NETHER_STAR, name, lore));
            }
            else if (slot == 8) {
                if (((Player)event.getWhoClicked()).hasPermission("practice.fly")) {
                    if (!settings.isTime()) {
                        ((Player)event.getWhoClicked()).setAllowFlight(true);
                    }
                    else {
                        ((Player)event.getWhoClicked()).setAllowFlight(false);
                        ((Player)event.getWhoClicked()).setFlying(false);
                    }
                    settings.setTime(!settings.isTime());
                    lore = Lists.newArrayList();
                    if (settings.isTime()) {
                        lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
                    }
                    else {
                        lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
                    }
                    final String name = "§9" + String.valueOf(settings.isPublicChat() ? "Fly" : "Volar");
                    ((Player)event.getWhoClicked()).getOpenInventory().setItem(8, create(Material.NETHER_STAR, name, lore));
                }
                else {
                    ((Player)event.getWhoClicked()).sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Only rank people can fly in the spawn." : "Solo la gente con rango puede volar en el spawn."));
                }
            }
        }
    }
    
    public static void open(final Player player) {
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, "Settings");
        final String enabled = ChatColor.GRAY + " " + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
        ArrayList<String> lore = Lists.newArrayList();
        if (settings.isScoreboard()) {
            lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
        }
        else {
            lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
        }
        String name = "§9Scoreboard";
        inventory.setItem(0, create(Material.PAINTING, name, lore));
        lore = Lists.newArrayList();
        if (settings.isDuelRequests()) {
            lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
        }
        else {
            lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
        }
        name = "§9" + String.valueOf(settings.isPublicChat() ? "Duel Requests" : "Recibo de duelos");
        inventory.setItem(2, create(Material.DIAMOND_SWORD, (short)0, name, lore));
        lore = Lists.newArrayList();
        if (settings.isMessage()) {
            lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
        }
        else {
            lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
        }
        name = "§9" + String.valueOf(settings.isPublicChat() ? "Private Message" : "Mensajes Privados");
        inventory.setItem(4, create(Material.FEATHER, name, lore));
        lore = Lists.newArrayList();
        if (settings.isPublicChat()) {
            lore.add(String.valueOf(enabled) + ChatColor.GREEN + "English");
        }
        else {
            lore.add(String.valueOf(enabled) + ChatColor.GREEN + "Español");
        }
        name = "§9Language";
        inventory.setItem(6, create(Material.ITEM_FRAME, name, lore));
        player.openInventory(inventory);
        lore = Lists.newArrayList();
        if (player.hasPermission("practice.fly")) {
            if (settings.isTime()) {
                lore.add(String.valueOf(enabled) + ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Enabled" : "Activado"));
            }
            else {
                lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Disabled" : "Desactivado"));
            }
        }
        else {
            lore.add(String.valueOf(enabled) + ChatColor.RED + String.valueOf(settings.isPublicChat() ? "/buy" : "/buy"));
        }
        name = "§9" + String.valueOf(settings.isPublicChat() ? "Fly" : "Volar");
        inventory.setItem(8, create(Material.NETHER_STAR, name, lore));
        player.openInventory(inventory);
    }
    
    public static List<String> translate(final List<String> input) {
        return (List<String>)Lists.newArrayList((Iterable<String>)Lists.transform((List<String>)input, (Function<String, String>)new Function<String, String>() {
            public String apply(@Nullable final String s) {
                return ChatColor.translateAlternateColorCodes('&', s);
            }
        }));
    }
    
    public static ItemStack create(final Material material, final String name, final List<String> lore) {
        return create(material, (short)0, name, lore);
    }
    
    public static ItemStack create(final Material material, final short data, final String name, final List<String> lore) {
        final ItemStack itemstack = new ItemStack(material, 1, data);
        final ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore((List<String>)translate(lore));
        itemstack.setItemMeta(meta);
        return itemstack;
    }
    
    public static ItemStack[] reverse(final ItemStack[] x) {
        final ItemStack[] d = new ItemStack[x.length];
        for (int i = x.length - 1; i >= 0; --i) {
            d[x.length - i - 1] = x[i];
        }
        return d;
    }
    
    private boolean isTopInventory(final InventoryDragEvent event) {
        @SuppressWarnings("unused")
		final InventoryView view = event.getView();
        if (event.getView().getTopInventory() == null) {
            return false;
        }
        final Set<Map.Entry<Integer, ItemStack>> items = event.getNewItems().entrySet();
        boolean isInventory = false;
        for (final Map.Entry<Integer, ItemStack> item : items) {
            if (item.getKey() < event.getView().getTopInventory().getSize()) {
                isInventory = true;
                break;
            }
        }
        return isInventory;
    }
}
