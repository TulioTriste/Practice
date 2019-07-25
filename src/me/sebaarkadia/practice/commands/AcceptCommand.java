package me.sebaarkadia.practice.commands;

import org.bukkit.event.Event;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.duel.DuelRequest;
import me.sebaarkadia.practice.events.DuelPreCreateEvent;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;

import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class AcceptCommand implements CommandExecutor
{
    private Practice plugin;
    
    public AcceptCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (settings.isMod()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Modmode" : "§cNo puedes ejecutar ese comando en Modmode"));
            return true;
        }
        if (settings.isSeeAll()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Seeall Mode" : "§cNo se puede ejecutar ese comando en SeeAll"));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
                return true;
            }
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(player)) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have request´s." : "§cNo tienes Request´s."));
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            return true;
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(player, target)) {
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is busy." : "§cEl jugador esta ocupado"));
            return true;
        }
        final DuelRequest request = this.plugin.getManagerHandler().getRequestManager().getDuelRequest(player, target);
        if (request == null) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cRequest not found." : "§cRequest no encontrado."));
            return true;
        }
        this.plugin.getManagerHandler().getRequestManager().removeDuelRequest(player, target);
        final String kitName = request.getKitName();
        final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
        final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
        final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
        firstTeam.add(player.getUniqueId());
        secondTeam.add(target.getUniqueId());
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null && targetParty != null) {
            for (final UUID member : party.getMembers()) {
                firstTeam.add(member);
            }
            for (final UUID member : targetParty.getMembers()) {
                secondTeam.add(member);
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party.getLeader(), targetParty.getLeader(), firstTeam, secondTeam, false));
        }
        else {
            if ((party != null && targetParty == null) || (targetParty != null && party == null)) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is in Party." : "§cEl jugador esta en Party."));
                return true;
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
        }
        return true;
    }
}
