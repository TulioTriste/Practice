package me.sebaarkadia.practice.commands;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Comparator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.function.Consumer;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bson.Document;
import java.util.ArrayList;
import com.mongodb.BasicDBObject;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PracticePlayer;

import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class TopEloCommand implements TabExecutor
{
    private Practice plugin;
    
    public TopEloCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (args.length <= 0) {
            new BukkitRunnable() {
                public void run() {
                    @SuppressWarnings("unchecked")
					final List<Document> documents = (List<Document>)PracticePlayer.main.getPracticeDatabase().getProfiles().find().limit(5).sort(new BasicDBObject("globalPersonalElo", (-1))).into(new ArrayList<Object>());
                    sender.sendMessage("§7§M----------------------------------------------");
                    sender.sendMessage("§9§lTopElo Global");
                    int index = 1;
                    for (final Document document : documents) {
                        final UUID uuid = UUID.fromString(document.getString("uuid"));
                        final String name = Bukkit.getOfflinePlayer(uuid).getName();
                        final int premiumElo = document.getInteger("globalPersonalElo");
                        sender.sendMessage("  §7- §9" + index++ + " §f" + name + " §9(" + premiumElo + ")");
                    }
                    sender.sendMessage("§7§M----------------------------------------------");
                }
            }.runTaskAsynchronously(this.plugin);
        }
        else {
            new Gson();
            new BukkitRunnable() {
                @SuppressWarnings("unchecked")
				public void run() {
                    final ArrayList<UUIDToElo> everything = Lists.newArrayList();
                    PracticePlayer.main.getPracticeDatabase().getProfiles().find().forEach((Consumer<?>)new Consumer<Object>() {
                        @Override
                        public void accept(final Object o) {
                            final Document document = (Document)o;
                            final UUID uuid = UUID.fromString(document.getString("uuid"));
                            for (final JsonElement element : new JsonParser().parse(document.getString("player_elo")).getAsJsonArray()) {
                                final JsonObject practiceDocument = element.getAsJsonObject();
                                if (practiceDocument.has("kit_personal_name")) {
                                    if (!(practiceDocument.get("kit_personal_name").getAsString()).equalsIgnoreCase(args[0])) {
                                        continue;
                                    }
                                    final int elo = practiceDocument.get("kit_personal_elo").getAsInt();
                                    final UUIDToElo data = new UUIDToElo(uuid, elo);
                                    everything.add(data);
                                }
                            }
                            Collections.sort(everything, new Comparator<UUIDToElo>() {
                                @Override
                                public int compare(final UUIDToElo o1, final UUIDToElo o2) {
                                    return o2.elo - o1.elo;
                                }
                            });
                        }
                    });
                    sender.sendMessage("§7§M----------------------------------------------");
                    sender.sendMessage("§9§lTopElo for " + args[0] + ":");
                    for (int i = 0; i < Math.min(3, everything.size()); ++i) {
                        final UUIDToElo elo = everything.get(i);
                        final UUID uuid = elo.getUuid();
                        final String name = Bukkit.getOfflinePlayer(uuid).getName();
                        final int premiumElo = elo.getElo();
                        sender.sendMessage("  §7- §9" + (i + 1) + "§f " + name + " §9(" + premiumElo + ")");
                    }
                    sender.sendMessage("§7§M----------------------------------------------");
                }
            }.runTaskAsynchronously(this.plugin);
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        return (List<String>)Lists.newArrayList(Practice.getInstance().getManagerHandler().getKitManager().getKitMap().keySet());
    }
    
    public class UUIDToElo
    {
        private UUID uuid;
        private int elo;
        
        public UUIDToElo(final UUID uuid, final int elo) {
            this.uuid = uuid;
            this.elo = elo;
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
        
        public int getElo() {
            return this.elo;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UUIDToElo)) {
                return false;
            }
            final UUIDToElo other = (UUIDToElo)o;
            if (!other.canEqual(this)) {
                return false;
            }
            final UUID this$uuid = this.getUuid();
            final UUID other$uuid = other.getUuid();
            if (this$uuid == null) {
                if (other$uuid == null) {
                    return this.getElo() == other.getElo();
                }
            }
            else if (this$uuid.equals(other$uuid)) {
                return this.getElo() == other.getElo();
            }
            return false;
        }
        
        protected boolean canEqual(final Object other) {
            return other instanceof UUIDToElo;
        }
        
        @Override
        public int hashCode() {
            int result = 1;
            final UUID $uuid = this.getUuid();
            result = result * 59 + (($uuid == null) ? 0 : $uuid.hashCode());
            result = result * 59 + this.getElo();
            return result;
        }
        
        @Override
        public String toString() {
            return "SortPremiumEloCommand.UUIDToElo(uuid=" + this.getUuid() + ", elo=" + this.getElo() + ")";
        }
    }
}
