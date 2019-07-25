package me.sebaarkadia.practice.player;

import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractMap;
import com.mongodb.client.model.UpdateOptions;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.util.UtilItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Set;

public class PracticePlayer
{
    private static Set<PracticePlayer> profiles;
    public static Practice main;
    private Settings settings;
    private UUID uuid;
    private boolean npc;
    private String name;
    private PlayerState currentState;
    private int teamNumber;
    private int unrankedWins;
    private int globalPersonalElo;
    private int credits;
    private boolean scoreboard;
    private long hostCooldown;
    private int potionMiss;
    private int potion;
    private int lastMissedPotions;
    private int totalHit;
    private int lastTotalHit;
    private int longestCombo;
    private int bestLongestCombo;
    private int lastBestLongestCombo;
    private int oitcEventKills;
    private int oitcEventDeaths;
    private int oitcEventWins;
    private int oitcEventLosses;
    private int sumoEventWins;
    private int sumoEventLosses;
    private int parkourEventWins;
    private int parkourEventLosses;
    private int redroverEventWins;
    private int redroverEventLosses;
    private transient boolean showRematchItemFlag;
    private transient String lastDuelPlayer;
    private List<Match> matches;
    private Map<String, Map<Integer, PlayerKit>> playerKitMap;
    private Map<String, Integer> playerEloMap;
    
    static {
        PracticePlayer.profiles = new HashSet<PracticePlayer>();
        PracticePlayer.main = Practice.getInstance();
    }
    
    public PracticePlayer(final UUID uuid, final boolean cache) {
        this.playerEloMap = new HashMap<String, Integer>();
        this.playerKitMap = new HashMap<String, Map<Integer, PlayerKit>>();
        this.matches = new ArrayList<Match>();
        this.uuid = uuid;
        this.currentState = PlayerState.LOBBY;
        this.npc = false;
        this.settings = new Settings();
        this.unrankedWins = 0;
        this.credits = 0;
        this.globalPersonalElo = 0;
        this.hostCooldown = 0L;
        this.potionMiss = 0;
        this.potion = 0;
        this.lastMissedPotions = 0;
        this.totalHit = 0;
        this.lastTotalHit = 0;
        this.longestCombo = 0;
        this.bestLongestCombo = 0;
        this.lastBestLongestCombo = 0;
        this.oitcEventKills = 0;
        this.oitcEventDeaths = 0;
        this.oitcEventWins = 0;
        this.oitcEventLosses = 0;
        this.sumoEventWins = 0;
        this.sumoEventLosses = 0;
        this.parkourEventWins = 0;
        this.parkourEventLosses = 0;
        this.redroverEventWins = 0;
        this.redroverEventLosses = 0;
        this.scoreboard = true;
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.name = player.getName();
        }
        else {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null) {
                this.name = offlinePlayer.getName();
            }
        }
        this.load();
        if (cache) {
            PracticePlayer.profiles.add(this);
        }
    }
    
    public void giveLobbyItems(final Player player) {
        final boolean inEvent = this.main.getManagerHandler().getEventManager().getEventPlaying(player) != null;
        ItemStack[] items = this.main.getManagerHandler().getItemManager().getSpawnItems();
        if (inEvent) {
            items = this.main.getManagerHandler().getItemManager().getEventItems();
        }
        player.getInventory().setContents(items);
        if (!inEvent) {
            player.getInventory().setItem(3, UtilItem.createItem(Material.EMERALD, ChatColor.BLUE.toString() + ChatColor.BOLD + "Rematch"));
        }
        player.updateInventory();
    }
    
    public int getCredits() {
        return this.credits;
    }
    
    public long getHostCooldown() {
        return this.hostCooldown;
    }
    
    public String getLastDuelPlayer() {
        return this.lastDuelPlayer;
    }
    
    public void setLastDuelPlayer(final String lastDuelPlayer) {
        this.lastDuelPlayer = lastDuelPlayer;
    }
    
    public void setHostCooldown(final long hostCooldown) {
        this.hostCooldown = hostCooldown;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public boolean isShowRematchItemFlag() {
        return this.showRematchItemFlag;
    }
    
    public void setShowRematchItemFlag(final boolean showRematchItemFlag) {
        this.showRematchItemFlag = showRematchItemFlag;
    }
    
    public void setCreateNcp(final boolean npc) {
        this.npc = npc;
    }
    
    public boolean isCreateNpc() {
        return this.npc;
    }
    
    public void setCredits(final int credits) {
        this.credits = credits;
    }
    
    public int getTeamNumber() {
        return this.teamNumber;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PlayerState getCurrentState() {
        return this.currentState;
    }
    
    public Settings getSettings() {
        return this.settings;
    }
    
    public void setSettings(final Settings settings) {
        this.settings = settings;
    }
    
    public void setTeamNumber(final int i) {
        this.teamNumber = i;
    }
    
    public void addElo(final String kitName, final int elo) {
        this.playerEloMap.put(kitName, elo);
    }
    
    public void addKit(final String kitName, final Integer kitIndex, final PlayerKit playerKit) {
        if (!this.playerKitMap.containsKey(kitName)) {
            this.playerKitMap.put(kitName, new HashMap<Integer, PlayerKit>());
        }
        this.playerKitMap.get(kitName).put(kitIndex, playerKit);
    }
    
    public void setCurrentState(final PlayerState playerState) {
        this.currentState = playerState;
    }
    
    public Map<String, Integer> getEloMap() {
        return this.playerEloMap;
    }
    
    public List<Match> getMatches() {
        return this.matches;
    }
    
    public Map<String, Map<Integer, PlayerKit>> getKitMap() {
        return this.playerKitMap;
    }
    
    public void load() {
        if (Practice.getInstance().getServer().getPluginManager().getPlugin("cLib") != null) {
            final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("uuid", this.uuid.toString())).first();
            if (document != null) {
                for (final JsonElement element : new JsonParser().parse(document.getString("player_elo")).getAsJsonArray()) {
                    final JsonObject practiceDocument = element.getAsJsonObject();
                    if (practiceDocument.has("kit_personal_name")) {
                        this.addElo(practiceDocument.get("kit_personal_name").getAsString(), practiceDocument.get("kit_personal_elo").getAsInt());
                    }
                }
                if (document.containsKey("unrankedWins")) {
                    this.unrankedWins = document.getInteger("unrankedWins");
                }
                if (document.containsKey("hostCooldown")) {
                    this.hostCooldown = document.getLong("hostCooldown");
                }
                if (document.containsKey("globalPersonalElo")) {
                    this.globalPersonalElo = document.getInteger("globalPersonalElo");
                }
                if (document.containsKey("credits")) {
                    this.credits = document.getInteger("credits");
                }
                if (document.containsKey("recentName")) {
                    this.name = document.getString("recentName");
                }
            }
        }
    }
    
    public void save() {
        final Document document = new Document();
        final JsonArray eloDocument = new JsonArray();
        for (final Map.Entry<String, Integer> entry : this.playerEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_personal_name", (String)entry.getKey());
            practiceDocument.addProperty("kit_personal_elo", (Number)entry.getValue());
            eloDocument.add((JsonElement)practiceDocument);
        }
        document.put("uuid", this.uuid.toString());
        document.put("credits", this.credits);
        document.put("unrankedWins", this.unrankedWins);
        document.put("globalPersonalElo", this.globalPersonalElo);
        if (this.name != null) {
            document.put("recentName", this.name);
            document.put("recentNameLowercase", this.name.toLowerCase());
            document.put("recentNameLength", this.name.length());
        }
        document.put("player_elo", eloDocument.toString());
        PracticePlayer.main.getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new UpdateOptions().upsert(true));
    }
    
    public static PracticePlayer getByUuid(final UUID uuid) {
        for (final PracticePlayer profile : PracticePlayer.profiles) {
            if (profile.getUUID().equals(uuid)) {
                return profile;
            }
        }
        return getExternalByUuid(uuid);
    }
    
    private static PracticePlayer getExternalByUuid(final UUID uuid) {
        final PracticePlayer profile = new PracticePlayer(uuid, false);
        return profile;
    }
    
    public static Map.Entry<UUID, String> getExternalPlayerInformation(String name) throws IOException, ParseException {
        final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("recentName", name)).first();
        if (document != null && document.containsKey("recentName")) {
            return new AbstractMap.SimpleEntry<UUID, String>(UUID.fromString(document.getString("uuid")), document.getString("recentName"));
        }
        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final JSONParser parser = new JSONParser();
        final JSONObject obj = (JSONObject)parser.parse(reader.readLine());
        final UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        name = String.valueOf(obj.get("name"));
        reader.close();
        return new AbstractMap.SimpleEntry<UUID, String>(uuid, name);
    }
    
    public static Set<PracticePlayer> getProfiles() {
        return PracticePlayer.profiles;
    }
    
    public int getUnrankedWins() {
        return this.unrankedWins;
    }
    
    public void setUnrankedWins(final int unrankedWins) {
        this.unrankedWins = unrankedWins;
    }
    
    public int getGlobalPersonalElo() {
        return this.globalPersonalElo;
    }
    
    public void setGlobalPersonalElo(final int globalPersonalElo) {
        this.globalPersonalElo = globalPersonalElo;
    }
    
    public int getPotionMiss() {
        return this.potionMiss;
    }
    
    public void setPotionMiss(final int potionMiss) {
        this.potionMiss = potionMiss;
    }
    
    public int getPotion() {
        return this.potion;
    }
    
    public void setPotion(final int potion) {
        this.potion = potion;
    }
    
    public int getLastMissedPotions() {
        return this.lastMissedPotions;
    }
    
    public void setLastMissedPotions(final int lastMissedPotions) {
        this.lastMissedPotions = lastMissedPotions;
    }
    
    public int getTotalHit() {
        return this.totalHit;
    }
    
    public void setTotalHit(final int totalHit) {
        this.totalHit = totalHit;
    }
    
    public int getLastTotalHit() {
        return this.lastTotalHit;
    }
    
    public void setLastTotalHit(final int lastTotalHit) {
        this.lastTotalHit = lastTotalHit;
    }
    
    public int getLongestCombo() {
        return this.longestCombo;
    }
    
    public void setLongestCombo(final int longestCombo) {
        this.longestCombo = longestCombo;
    }
    
    public int getBestLongestCombo() {
        return this.bestLongestCombo;
    }
    
    public void setBestLongestCombo(final int bestLongestCombo) {
        this.bestLongestCombo = bestLongestCombo;
    }
    
    public int getLastBestLongestCombo() {
        return this.lastBestLongestCombo;
    }
    
    public void setLastBestLongestCombo(final int lastBestLongestCombo) {
        this.lastBestLongestCombo = lastBestLongestCombo;
    }

    public int getOitcEventKills() {
        return this.oitcEventKills;
    }

    public void setOitcEventKills(final int oitcEventKills) {
        this.oitcEventKills = oitcEventKills;
    }

    public int getOitcEventDeaths() {
        return this.oitcEventDeaths;
    }

    public void setOitcEventDeaths(final int oitcEventDeaths) {
        this.oitcEventDeaths = oitcEventDeaths;
    }

    public int getOitcEventWins() {
        return this.oitcEventWins;
    }

    public void setOitcEventWins(final int oitcEventWins) {
        this.oitcEventWins = oitcEventWins;
    }

    public int getOitcEventLosses() {
        return this.oitcEventLosses;
    }

    public void setOitcEventLosses(final int oitcEventLosses) {
        this.oitcEventLosses = oitcEventLosses;
    }

    public int getSumoEventWins() {
        return this.sumoEventWins;
    }

    public void setSumoEventWins(final int sumoEventWins) {
        this.sumoEventWins = sumoEventWins;
    }

    public int getSumoEventLosses() {
        return this.sumoEventLosses;
    }

    public void setSumoEventLosses(final int sumoEventLosses) {
        this.sumoEventLosses = sumoEventLosses;
    }

    public int getParkourEventWins() {
        return this.parkourEventWins;
    }

    public void setParkourEventWins(final int parkourEventWins) {
        this.parkourEventWins = parkourEventWins;
    }

    public int getParkourEventLosses() {
        return this.parkourEventLosses;
    }

    public void setParkourEventLosses(final int parkourEventLosses) {
        this.parkourEventLosses = parkourEventLosses;
    }

    public int getRedroverEventWins() {
        return this.redroverEventWins;
    }

    public void setRedroverEventWins(final int redroverEventWins) {
        this.redroverEventWins = redroverEventWins;
    }

    public int getRedroverEventLosses() {
        return this.redroverEventLosses;
    }

    public void setRedroverEventLosses(final int redroverEventLosses) {
        this.redroverEventLosses = redroverEventLosses;
    }
}
