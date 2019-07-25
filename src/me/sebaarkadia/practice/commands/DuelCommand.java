package me.sebaarkadia.practice.commands;

import java.util.Collections;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.party.PartyState;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class DuelCommand implements CommandExecutor, TabCompleter
{
    private Practice plugin;
    
    public DuelCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (settings.isMod()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Modmode" : "§cNo puedes ejecutar ese comando en Modmode"));
            return true;
        }
        if (settings.isSeeAll()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in Seeall Mode" : "§cNo puedes ejecutar ese comando en seeall"));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar ese comando en tu estado"));
            return false;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                return true;
            }
        }
        final Player target;
        if ((target = this.plugin.getServer().getPlayer(args[0])) == null) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
            return true;
        }
        if (target.getName().equals(player.getName())) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cIncorrect usage." : "§cUso incorrecto."));
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY || !practiceTarget.getSettings().isDuelRequests()) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is busy." : "§cEl jugador esta ocupado."));
            return true;
        }
        if (this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(target) && this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(target, player)) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou already sent a duel." : "§cYa le has enviado duel."));
            return true;
        }
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null) {
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou are not leader of the party." : "§cNo eres el jefe de la party."));
                return true;
            }
            if (targetParty == null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe party is bussy." : "§cLa party esta ocupada."));
                return true;
            }
            if (!targetParty.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe target is not leader of the party." : "§cEste jugador no es el jefe de la party."));
                return true;
            }
            if (targetParty.getPartyState() == PartyState.DUELING) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe party is busy." : "§cLa party esta ocupada."));
                return true;
            }
        }
        else if (targetParty != null) {
            return true;
        }
        final UUID uuid = player.getUniqueId();
        this.plugin.getManagerHandler().getInventoryManager().setSelectingDuel(uuid, target.getUniqueId());
        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRequestInventory());
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("duel") && args.length == 1) {
            final ArrayList<String> playerName = new ArrayList<String>();
            if (!args[0].equals("")) {
                for (final Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        playerName.add(pl.getName());
                    }
                }
            }
            else {
                for (final Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        playerName.add(pl.getName());
                    }
                }
            }
            Collections.sort(playerName);
            return playerName;
        }
        return null;
    }
}
