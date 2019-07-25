package me.sebaarkadia.practice.manager.managers;

import org.bukkit.entity.Player;

import me.sebaarkadia.practice.manager.Manager;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.party.Party;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class PartyManager extends Manager
{
    private Map<UUID, Party> leaderUUIDtoParty;
    private Map<UUID, UUID> playerUUIDtoLeaderUUID;
    
    public PartyManager(final ManagerHandler handler) {
        super(handler);
        this.leaderUUIDtoParty = new HashMap<UUID, Party>();
        this.playerUUIDtoLeaderUUID = new HashMap<UUID, UUID>();
    }
    
    public Party getParty(final UUID player) {
        if (this.leaderUUIDtoParty.containsKey(player)) {
            return this.leaderUUIDtoParty.get(player);
        }
        if (this.playerUUIDtoLeaderUUID.containsKey(player)) {
            final UUID leader = this.playerUUIDtoLeaderUUID.get(player);
            return this.leaderUUIDtoParty.get(leader);
        }
        return null;
    }
    
    public Map<UUID, Party> getPartyMap() {
        return this.leaderUUIDtoParty;
    }
    
    public Party createParty(final UUID leader, final String leadername) {
        final Party party = new Party(leader, leadername);
        this.leaderUUIDtoParty.put(leader, party);
        return party;
    }
    
    public void destroyParty(final UUID leader) {
        final Party party = this.leaderUUIDtoParty.get(leader);
        this.leaderUUIDtoParty.remove(leader);
        for (final UUID member : party.getMembers()) {
            this.playerUUIDtoLeaderUUID.remove(member);
        }
    }
    
    public void leaveParty(final UUID player) {
        final UUID leader = this.playerUUIDtoLeaderUUID.get(player);
        this.playerUUIDtoLeaderUUID.remove(player);
        final Party party = this.leaderUUIDtoParty.get(leader);
        party.removeMember(player);
    }
    
    public void joinParty(final UUID leader, final UUID player) {
        final Party party = this.leaderUUIDtoParty.get(leader);
        party.addMember(player);
        this.playerUUIDtoLeaderUUID.put(player, leader);
    }
    
    public void notifyParty(final Party party, final String message) {
        final Player leaderPlayer = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        leaderPlayer.sendMessage(message);
        for (final UUID uuid : party.getMembers()) {
            final Player memberPlayer = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (memberPlayer == null) {
                continue;
            }
            memberPlayer.sendMessage(message);
        }
    }
}
