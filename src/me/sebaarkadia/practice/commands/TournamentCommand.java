package me.sebaarkadia.practice.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.kit.Kit;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.tournament.Tournament;

public class TournamentCommand implements CommandExecutor
{
    private Practice plugin;
    
    public TournamentCommand(final Practice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.donator.tournament")) {
            player.sendMessage("§cNo Permission!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("§7§m--------------------------------------------");
            player.sendMessage("§9§lTournament Command");
            player.sendMessage("§cUsage: /tournament start <kit>");
            player.sendMessage("§cUsage: /tournament stop <id>");
            player.sendMessage("§cUsage: /tournament forcestart <id>");
            player.sendMessage("§cUsage: /tournament active");
            player.sendMessage("§7§m--------------------------------------------");
            return true;
        }
        if (args[0].toLowerCase().equalsIgnoreCase("start")) {
            if (!sender.hasPermission("practice.donator.tournament")) {
                sender.sendMessage("§cNo Permission!");
                return true;
            }
            if (Tournament.getTournaments().size() == 0) {
                final String kitName = args[1];
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
                if (kit == null) {
                    sender.sendMessage(ChatColor.RED + "This kit doesn't exist.");
                    return true;
                }
                this.plugin.getManagerHandler().getInventoryManager().setTournamentInventory(kit, false);
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getTournamentInventory());
            }
            else {
                sender.sendMessage(ChatColor.RED + "One tournament is already start !");
            }
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("practice.commands.tournament.stop")) {
                ((Player)sender).sendMessage(ChatColor.RED + "No permissions!");
                return true;
            }
            final int id = Integer.parseInt(args[1]);
            if (Tournament.getTournaments().size() > id) {
                sender.sendMessage(ChatColor.RED + "Available tournaments to stop:");
                int count = 1;
                for (final Tournament tournament : Tournament.getTournaments()) {
                    sender.sendMessage(ChatColor.RED + "ID: " + count + ChatColor.GRAY + " (" + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam() + ")");
                    ++count;
                }
                return true;
            }
            final Tournament tournament2 = Tournament.getTournaments().get(id - 1);
            if (tournament2 == null) {
                sender.sendMessage(ChatColor.RED + "That tournament id doesn't exist.");
                return true;
            }
            tournament2.stopTournament();
            PracticePlayer.main.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("forcestart")) {
            if (!sender.hasPermission("practice.commands.tournament.forcestart")) {
                sender.sendMessage("§cNo Permission!");
                return true;
            }
            final int id = Integer.parseInt(args[1]);
            final Tournament tournament2 = Tournament.getTournaments().get(id - 1);
            if (tournament2 == null) {
                player.sendMessage("§cYou don't.");
                return true;
            }
            tournament2.setForceStarted(true);
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("active") || args[0].toLowerCase().equalsIgnoreCase("list")) {
            if (!sender.hasPermission("practice.commands.tournament.active")) {
                sender.sendMessage("§cNo Permission!");
                return true;
            }
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage("§cYou don't.");
                return true;
            }
            for (final Tournament tournament3 : Tournament.getTournaments()) {
                player.sendMessage("§7§m--------------------------------------------");
                player.sendMessage(ChatColor.GOLD + "(*) Tournament (" + tournament3.getMaximumPerTeam() + "v" + tournament3.getMaximumPerTeam() + ")");
                player.sendMessage(ChatColor.GRAY + "-> Kit: " + tournament3.getDefaultKit().getName());
                player.sendMessage(ChatColor.GRAY + "-> Stage: " + ((tournament3.getTournamentStage() == null) ? "Waiting for players" : StringUtils.capitalize(tournament3.getTournamentStage().name().replace("_", " "))));
                player.sendMessage(ChatColor.GRAY + "-> Current Matches: " + tournament3.getCurrentMatches().size());
                player.sendMessage(ChatColor.GRAY + "-> Total Teams: " + tournament3.getTeams().size());
                player.sendMessage(ChatColor.GRAY + "-> Players Limit: " + tournament3.getPlayersLimit());
                player.sendMessage("§7§m--------------------------------------------");
            }
        }
        return true;
    }
}
