package me.sebaarkadia.practice.manager.managers;

import org.bukkit.inventory.meta.ItemMeta;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.player.PlayerKit;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.util.UtilItem;

import java.util.Arrays;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.bukkit.inventory.Inventory;

public class InventoryManager extends Manager
{
    private Inventory unrankedInventory;
    private Inventory editorInventory;
    private Inventory requestInventory;
    private Inventory splitFightInventory;
    private Inventory partiesInventory;
    private Inventory tournamentInventory;
    private Inventory joinTournamentInventory;
    private Map<UUID, UUID> selectingDuel;
    private Map<UUID, Inventory> editingKitItems;
    private Map<UUID, Inventory> editingKitKits;
    public static List<Player> guiUnranked;
    
    static {
        InventoryManager.guiUnranked = new ArrayList<Player>();
    }
    
    public InventoryManager(final ManagerHandler handler) {
        super(handler);
        this.selectingDuel = new HashMap<UUID, UUID>();
        this.editingKitItems = new HashMap<UUID, Inventory>();
        this.editingKitKits = new HashMap<UUID, Inventory>();
        this.unrankedInventory = handler.getPlugin().getServer().createInventory(null, 9, "§7» §e§lUnranked Queue §7«");
        this.editorInventory = handler.getPlugin().getServer().createInventory(null, 9, "§7» §e§lKit Editor §7«");
        this.requestInventory = handler.getPlugin().getServer().createInventory(null, 9, "§7» §e§lSend Request §7«");
        this.splitFightInventory = handler.getPlugin().getServer().createInventory(null, 9, "§7» §e§lSplit Fights §7«");
        this.partiesInventory = handler.getPlugin().getServer().createInventory(null, 54, "§7» §b§lFight Other Party §7«");
        this.tournamentInventory = handler.getPlugin().getServer().createInventory(null, 9, "§8Tournament Size");
        this.joinTournamentInventory = handler.getPlugin().getServer().createInventory(null, 9, "§7» §e§lAvailable Tournaments §7«");
        this.setUnrankedPartyInventory();
        this.setEditorInventory();
        this.setRequestInventory();
        this.setPartyEventsInventory();
        this.setSplitFightInventory();
        this.setJoinTournamentInventory();
    }
    
    public static void openUnrankedGUI(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 9, "§8Unranked Queue");
        InventoryManager.guiUnranked.add(player);
        final int NoDebuff1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("NoDebuff") != null) ? 1 : 0;
        final int Debuff1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Debuff") != null) ? 1 : 0;
        final int Soup1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Soup") != null) ? 1 : 0;
        final int anvil1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Sumo") != null) ? 1 : 0;
        final int Archer1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Archer") != null) ? 1 : 0;
        final int builduhc1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("BuildUHC") != null) ? 1 : 0;
        final int combo1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Combo") != null) ? 1 : 0;
        final int axe1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Axe") != null) ? 1 : 0;
        final int NoDebuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("NoDebuff");
        final int Debuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Debuff");
        final int soup2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Soup");
        final int anvil2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Sumo");
        final int Archer2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Archer");
        final int builduhc2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("BuildUHC");
        final int combo2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Combo");
        final int axe2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Axe");
        final ItemStack item5 = new ItemStack(Material.POTION, (NoDebuff2 > 64) ? 64 : ((NoDebuff2 == 0) ? 1 : NoDebuff2), (short)8229);
        final ItemMeta itemm5 = item5.getItemMeta();
        itemm5.setDisplayName("§6NoDebuff");
        itemm5.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + NoDebuff2, "§eIn Queue§7: §f" + ChatColor.WHITE + NoDebuff1, ChatColor.RESET + ""));
        item5.setItemMeta(itemm5);
        final ItemStack debuff = new ItemStack(Material.POTION, (Debuff2 > 64) ? 64 : ((Debuff2 == 0) ? 1 : Debuff2), (short)8228);
        final ItemMeta debuffd = debuff.getItemMeta();
        debuffd.setDisplayName("§6Debuff");
        debuffd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + Debuff2, "§eIn Queue§7: §f" + ChatColor.WHITE + Debuff1, ChatColor.RESET + ""));
        debuff.setItemMeta(debuffd);
        final ItemStack soup3 = new ItemStack(Material.MUSHROOM_SOUP, (soup2 > 64) ? 64 : ((soup2 == 0) ? 1 : soup2));
        final ItemMeta soupd = soup3.getItemMeta();
        soupd.setDisplayName("§6Soup");
        soupd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + soup2, "§eIn Queue§7: §f" + ChatColor.WHITE + Soup1, ChatColor.RESET + ""));
        soup3.setItemMeta(soupd);
        final ItemStack anvil3 = new ItemStack(Material.ANVIL, (anvil2 > 64) ? 64 : ((anvil2 == 0) ? 1 : anvil2));
        final ItemMeta anvild = anvil3.getItemMeta();
        anvild.setDisplayName("§6Sumo");
        anvild.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + anvil2, "§eIn Queue§7: §f" + ChatColor.WHITE + anvil1, ChatColor.RESET + ""));
        anvil3.setItemMeta(anvild);
        final ItemStack archer = new ItemStack(Material.BOW, (Archer2 > 64) ? 64 : ((Archer2 == 0) ? 1 : Archer2));
        final ItemMeta archerd = archer.getItemMeta();
        archerd.setDisplayName("§6Archer");
        archerd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + Archer2, "§eIn Queue§7: §f" + ChatColor.WHITE + Archer1, ChatColor.RESET + ""));
        archer.setItemMeta(archerd);
        final ItemStack builduhc3 = new ItemStack(Material.LAVA_BUCKET, (builduhc2 > 64) ? 64 : ((builduhc2 == 0) ? 1 : builduhc2));
        final ItemMeta builduhcd = builduhc3.getItemMeta();
        builduhcd.setDisplayName("§6BuildUHC");
        builduhcd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + builduhc2, "§eIn Queue§7: §f" + ChatColor.WHITE + builduhc1, ChatColor.RESET + ""));
        builduhc3.setItemMeta(builduhcd);
        final ItemStack combo3 = new ItemStack(Material.RAW_FISH, (combo2 > 64) ? 64 : ((combo2 == 0) ? 1 : combo2), (short)3);
        final ItemMeta combod = combo3.getItemMeta();
        combod.setDisplayName("§6Combo");
        combod.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + combo2, "§eIn Queue§7: §f" + ChatColor.WHITE + combo1, ChatColor.RESET + ""));
        combo3.setItemMeta(combod);
        final ItemStack axe3 = new ItemStack(Material.IRON_AXE, (axe2 > 64) ? 64 : ((axe2 == 0) ? 1 : axe2));
        final ItemMeta axed = axe3.getItemMeta();
        axed.setDisplayName("§6Axe");
        axed.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Fight§7: §f" + ChatColor.WHITE + axe2, "§eIn Queue§7: §f" + ChatColor.WHITE + axe1, ChatColor.RESET + ""));
        axe3.setItemMeta(axed);
        inv.setItem(0, item5);
        inv.setItem(1, debuff);
        inv.setItem(2, soup3);
        inv.setItem(3, anvil3);
        inv.setItem(4, archer);
        inv.setItem(5, combo3);
        inv.setItem(6, axe3);
        player.openInventory(inv);
    }
    
    public static void openRankedGUI(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 9, "§8Ranked Queue");
        final int NoDebuff1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("NoDebuff") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("NoDebuff").size() : 0;
        final int Debuff1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Debuff") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Debuff").size() : 0;
        final int Soup1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Soup") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Soup").size() : 0;
        final int anvil1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Sumo") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Sumo").size() : 0;
        final int Archer1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Archer") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Archer").size() : 0;
        final int builduhc1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("BuildUHC") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("BuildUHC").size() : 0;
        final int combo1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Combo") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Combo").size() : 0;
        final int axe1 = Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().containsKey("Axe") ? Practice.getInstance().getManagerHandler().getQueueManager().getRankedKitQueueMap().get("Axe").size() : 0;
        final int NoDebuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("NoDebuff");
        final int Debuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Debuff");
        final int soup2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Soup");
        final int anvil2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Sumo");
        final int Archer2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Archer");
        final int builduhc2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("BuildUHC");
        final int combo2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Combo");
        final int axe2 = Practice.getInstance().getManagerHandler().getDuelManager().getRankedDuelsFromKit("Axe");
        final ItemStack item5 = new ItemStack(Material.POTION, (NoDebuff2 > 64) ? 64 : ((NoDebuff2 == 0) ? 1 : NoDebuff2), (short)8229);
        final ItemMeta itemm5 = item5.getItemMeta();
        itemm5.setDisplayName("§6NoDebuff");
        itemm5.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Game §7» " + ChatColor.WHITE + NoDebuff2, "§eIn Queue §7» " + ChatColor.WHITE + NoDebuff1, ChatColor.RESET + ""));
        item5.setItemMeta(itemm5);
        final ItemStack debuff = new ItemStack(Material.POTION, (Debuff2 > 64) ? 64 : ((Debuff2 == 0) ? 1 : Debuff2), (short)8228);
        final ItemMeta debuffd = debuff.getItemMeta();
        debuffd.setDisplayName("§6Debuff");
        debuffd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + Debuff2, "§eIn Queue §7» " + ChatColor.WHITE + Debuff1, ChatColor.RESET + ""));
        debuff.setItemMeta(debuffd);
        final ItemStack soup3 = new ItemStack(Material.MUSHROOM_SOUP, (soup2 > 64) ? 64 : ((soup2 == 0) ? 1 : soup2));
        final ItemMeta soupd = soup3.getItemMeta();
        soupd.setDisplayName("§6Soup");
        soupd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + soup2, "§eIn Queue §7» " + ChatColor.WHITE + Soup1, ChatColor.RESET + ""));
        soup3.setItemMeta(soupd);
        final ItemStack anvil3 = new ItemStack(Material.ANVIL, (anvil2 > 64) ? 64 : ((anvil2 == 0) ? 1 : anvil2));
        final ItemMeta anvild = anvil3.getItemMeta();
        anvild.setDisplayName("§6Sumo");
        anvild.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + anvil2, "§eIn Queue §7» " + ChatColor.WHITE + anvil1, ChatColor.RESET + ""));
        anvil3.setItemMeta(anvild);
        final ItemStack archer = new ItemStack(Material.BOW, (Archer2 > 64) ? 64 : ((Archer2 == 0) ? 1 : Archer2));
        final ItemMeta archerd = archer.getItemMeta();
        archerd.setDisplayName("§6Archer");
        archerd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + Archer2, "§eIn Queue §7» " + ChatColor.WHITE + Archer1, ChatColor.RESET + ""));
        archer.setItemMeta(archerd);
        final ItemStack builduhc3 = new ItemStack(Material.LAVA_BUCKET, (builduhc2 > 64) ? 64 : ((builduhc2 == 0) ? 1 : builduhc2));
        final ItemMeta builduhcd = builduhc3.getItemMeta();
        builduhcd.setDisplayName("§6BuildUHC");
        builduhcd.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + builduhc2, "§eIn Queue §7» " + ChatColor.WHITE + builduhc1, ChatColor.RESET + ""));
        builduhc3.setItemMeta(builduhcd);
        final ItemStack combo3 = new ItemStack(Material.RAW_FISH, (combo2 > 64) ? 64 : ((combo2 == 0) ? 1 : combo2), (short)3);
        final ItemMeta combod = combo3.getItemMeta();
        combod.setDisplayName("§6Combo");
        combod.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + combo2, "§eIn Queue §7» " + ChatColor.WHITE + combo1, ChatColor.RESET + ""));
        combo3.setItemMeta(combod);
        final ItemStack axe3 = new ItemStack(Material.IRON_AXE, (axe2 > 64) ? 64 : ((axe2 == 0) ? 1 : axe2));
        final ItemMeta axed = axe3.getItemMeta();
        axed.setDisplayName("§6Axe");
        axed.setLore(Arrays.asList(ChatColor.RESET + "","§eIn Game §7» " + ChatColor.WHITE + axe2, "§eIn Queue §7» " + ChatColor.WHITE + axe1, ChatColor.RESET + ""));
        axe3.setItemMeta(axed);
        inv.setItem(0, item5);
        inv.setItem(1, debuff);
        inv.setItem(2, soup3);
        inv.setItem(3, anvil3);
        inv.setItem(4, archer);
        inv.setItem(5, combo3);
        inv.setItem(6, axe3);
        player.openInventory(inv);
    }
    
    public void setUnrankedPartyInventory() {
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final int inGame = this.handler.getDuelManager().getUnRankedPartyDuelsFromKit(kitName);
            final int inQueue = (this.handler.getQueueManager().getQueuedForPartyUnrankedQueue(kitName) != null) ? 1 : 0;
            final List<String> lore = Arrays.asList("§fIn Game §7» " + ChatColor.WHITE + inGame, "§fIn Queue §7» " + ChatColor.WHITE + inQueue);
            final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), (inGame > 64) ? 64 : ((inGame == 0) ? 1 : inGame), kit.getIcon().getDurability(), "§9" + kitName, lore);
            this.unrankedInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setEditorInventory() {
        this.editorInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (kit.isEnabled()) {
                if (!kit.isEditable()) {
                    continue;
                }
                final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), 1, kit.getIcon().getDurability(), "§9" + kitName);
                this.editorInventory.setItem(count, kitIcon);
                ++count;
            }
        }
    }
    
    public void setRequestInventory() {
        this.requestInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§6" + kitName);
            this.requestInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setJoinTournamentInventory() {
        this.joinTournamentInventory.clear();
        if (Tournament.getTournaments().size() == 0) {
            return;
        }
        int count = 1;
        for (final Tournament tournament : Tournament.getTournaments()) {
            final ItemStack item = UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, "§9" + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam(), Arrays.asList("§fPlayers §7» §f" + tournament.getTeams().size() + "/" + tournament.getPlayersLimit(), ChatColor.WHITE + "Stage §7»§f " + ((tournament.getTournamentStage() == null) ? "Waiting for players" : tournament.getTournamentStage().name().replace("_", " ")), ChatColor.GRAY + "Click here to join the tournament"));
            this.joinTournamentInventory.addItem(new ItemStack[] { item });
            ++count;
        }
    }
    
    public void setTournamentInventory(final Kit kit, final boolean isPlayer) {
        this.tournamentInventory.clear();
        for (int i = 1; i <= 5; ++i) {
            final String[] arrstring = { ChatColor.GRAY + "Type: " + (isPlayer ? "Player" : "System"), ChatColor.GRAY + "Kit: " + kit.getName() };
            final ItemStack item = UtilItem.createItem(Material.NETHER_STAR, 1, (short)0, String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + i + "v" + i, Arrays.asList(ChatColor.GRAY + "Type: " + (isPlayer ? "Player" : "System"), ChatColor.GRAY + "Kit: " + kit.getName()));
            this.tournamentInventory.addItem(new ItemStack[] { item });
        }
    }
    
    private void setPartyEventsInventory() {
        this.splitFightInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§9" + kitName);
            this.splitFightInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setSplitFightInventory() {
        this.splitFightInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§9" + kitName);
            this.splitFightInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void addParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final ItemStack skull = UtilItem.createItem(Material.SKULL_ITEM, 1, (short)3, ChatColor.GOLD + player.getName());
        this.partiesInventory.addItem(new ItemStack[] { skull });
    }
    
    public void delParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final String leaderName = (player == null) ? party.getLeaderName() : player.getName();
        ItemStack[] contents;
        for (int length = (contents = this.partiesInventory.getContents()).length, i = 0; i < length; ++i) {
            final ItemStack itemStack = contents[i];
            if (itemStack != null && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(leaderName)) {
                this.partiesInventory.remove(itemStack);
                break;
            }
        }
    }
    
    public void updateParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final String leaderName = (player == null) ? party.getLeaderName() : player.getName();
        final ArrayList<String> lores = new ArrayList<String>();
        for (final UUID uuid : party.getMembers()) {
            final Player memberPlayer = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (memberPlayer == null) {
                continue;
            }
            lores.add("§9" + memberPlayer.getName());
        }
        ItemStack[] contents;
        for (int length = (contents = this.partiesInventory.getContents()).length, i = 0; i < length; ++i) {
            final ItemStack itemStack = contents[i];
            if (itemStack != null && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(leaderName)) {
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(lores);
                itemMeta.setDisplayName(ChatColor.GOLD + leaderName + "§9" + " (" + (party.getMembers().size() + 1) + ")");
                itemStack.setItemMeta(itemMeta);
                break;
            }
        }
    }
    
    public void setSelectingDuel(final UUID uuid, final UUID uuid1) {
        this.selectingDuel.put(uuid, uuid1);
    }
    
    public UUID getSelectingDuelPlayerUUID(final UUID uuid) {
        return this.selectingDuel.get(uuid);
    }
    
    public void removeSelectingDuel(final UUID uuid) {
        this.selectingDuel.remove(uuid);
    }
    
    public Inventory getEditKitItemsInventory(final UUID uuid) {
        return this.editingKitItems.get(uuid);
    }
    
    public void addEditKitItemsInventory(final UUID uuid, final Kit kit) {
        final Inventory inventory = this.handler.getPlugin().getServer().createInventory(null, 54, kit.getName());
        ItemStack[] mainContents;
        for (int length = (mainContents = kit.getMainContents()).length, i = 0; i < length; ++i) {
            final ItemStack itemStack = mainContents[i];
            if (itemStack != null) {
                inventory.addItem(new ItemStack[] { itemStack });
            }
        }
        ItemStack[] armorContents;
        for (int length2 = (armorContents = kit.getArmorContents()).length, j = 0; j < length2; ++j) {
            final ItemStack itemStack = armorContents[j];
            if (itemStack != null) {
                inventory.addItem(new ItemStack[] { itemStack });
            }
        }
        inventory.addItem(new ItemStack[] { new ItemStack(Material.COOKED_BEEF, 64) });
        this.editingKitItems.put(uuid, inventory);
    }
    
    public void destroyEditKitItemsInventory(final UUID uuid) {
        this.editingKitItems.remove(uuid);
    }
    
    public void addEditKitKitsInventory(final UUID uuid, final Kit kit) {
        final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(uuid);
        final Map<Integer, PlayerKit> kitMap = practicePlayer.getKitMap().get(kit.getName());
        final Inventory inventory = this.handler.getPlugin().getServer().createInventory(null, 45, "§8Kit Layout");
        for (int i = 1; i <= 7; ++i) {
            final ItemStack save = UtilItem.createItem(Material.CHEST, 1, (short)0, "§fCreate Kit §9" + kit.getName() + " #" + i);
            inventory.setItem(i, save);
            if (kitMap != null && kitMap.containsKey(i)) {
                final ItemStack loadedKit = UtilItem.createItem(Material.ENDER_CHEST, 1, (short)0, "§fKIT: §9" + kit.getName() + " #" + i);
                final ItemStack load = UtilItem.createItem(Material.BOOK, 1, (short)0, "§fLoad Kit §9" + kit.getName() + " #" + i);
                final ItemStack save2 = UtilItem.createItem(Material.INK_SACK, 1, (short)10, "§fSave Kit §9" + kit.getName() + " #" + i);
                final ItemStack rename = UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§fRename Kit §9" + kit.getName() + " #" + i);
                final ItemStack delete = UtilItem.createItem(Material.FLINT, 1, (short)0, "§fDelete Kit §9" + kit.getName() + " #" + i);
                inventory.setItem(i, loadedKit);
                inventory.setItem(i + 9, load);
                inventory.setItem(i + 18, save2);
                inventory.setItem(i + 27, rename);
                inventory.setItem(i + 36, delete);
            }
        }
        final ItemStack back = UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)14, "§7§oKit Editor");
        inventory.setItem(0, back);
        inventory.setItem(9, back);
        inventory.setItem(18, back);
        inventory.setItem(27, back);
        inventory.setItem(8, back);
        inventory.setItem(17, back);
        inventory.setItem(26, back);
        inventory.setItem(35, back);
        inventory.setItem(36, back);
        inventory.setItem(44, back);
        this.editingKitKits.put(uuid, inventory);
    }
    
    public Inventory getEditKitKitsInventory(final UUID uuid) {
        return this.editingKitKits.get(uuid);
    }
    
    public void destroyEditKitKitsInventory(final UUID uuid) {
        this.editingKitKits.remove(uuid);
    }
    
    public Inventory getUnrankedInventory() {
        return this.unrankedInventory;
    }
    
    public Inventory getEditorInventory() {
        return this.editorInventory;
    }
    
    public Inventory getRequestInventory() {
        return this.requestInventory;
    }
    
    public Inventory getSplitFightInventory() {
        return this.splitFightInventory;
    }
    
    public Inventory getPartiesInventory() {
        return this.partiesInventory;
    }
    
    public Inventory getTournamentInventory() {
        return this.tournamentInventory;
    }
    
    public Inventory getJoinTournamentInventory() {
        return this.joinTournamentInventory;
    }
}
