package me.sebaarkadia.practice.commands;

import java.util.HashMap;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.party.Party;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.settings.Settings;
import me.sebaarkadia.practice.tournament.Tournament;
import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.UtilActionMessage;

public class PartyCommand implements CommandExecutor
{
    private String[] HELP_COMMAND;
    private Practice plugin;
    private String[] HELP_COMMAND_FR;
    private HashMap<UUID, Long> delay;
    
    public PartyCommand(final Practice plugin) {
        this.HELP_COMMAND = new String[] { String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Commands:", ChatColor.GOLD + "(*) /party help " + ChatColor.GRAY + "- Displays the help menu", ChatColor.GOLD + "(*) /party create " + ChatColor.GRAY + "- Creates a party instance", ChatColor.GOLD + "(*) /party leave " + ChatColor.GRAY + "- Leave your current party", ChatColor.GOLD + "(*) /party info " + ChatColor.GRAY + "- Displays your party information", ChatColor.GOLD + "(*) /party join (player) " + ChatColor.GRAY + "- Join a party (invited or unlocked)", "", ChatColor.RED + "Leader Commands:", ChatColor.GOLD + "(*) /party open " + ChatColor.GRAY + "- Open your party for others to join", ChatColor.GOLD + "(*) /party lock " + ChatColor.GRAY + "- Lock your party for others to join", ChatColor.GOLD + "(*) /party invite (player) " + ChatColor.GRAY + "- Invites a player to your party", ChatColor.GOLD + "(*) /party kick (player) " + ChatColor.GRAY + "- Kicks a player from your party", String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
        this.HELP_COMMAND_FR = new String[] { String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Commands:", ChatColor.GOLD + "(*) /party help " + ChatColor.GRAY + "- Page d'aide", ChatColor.GOLD + "(*) /party create " + ChatColor.GRAY + "- Cr\u00e9e une party", ChatColor.GOLD + "(*) /party leave " + ChatColor.GRAY + "- Quitter votre party", ChatColor.GOLD + "(*) /party info " + ChatColor.GRAY + "- Page d'information party", ChatColor.GOLD + "(*) /party join (player) " + ChatColor.GRAY + "- Rejoindre une party", "", ChatColor.RED + "Commandes pour le chef:", ChatColor.GOLD + "(*) /party open " + ChatColor.GRAY + "- Ouvre la party", ChatColor.GOLD + "(*) /party lock " + ChatColor.GRAY + "- Ferme la party", ChatColor.GOLD + "(*) /party invite (player) " + ChatColor.GRAY + "- Inviter des joueurs", ChatColor.GOLD + "(*) /party kick (player) " + ChatColor.GRAY + "- Expluser des joueur de la party", String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
        this.delay = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final Settings settings = Practice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        if (args.length == 0) {
            if (settings.isPublicChat()) {
                player.sendMessage(this.HELP_COMMAND);
            }
            else {
                player.sendMessage(this.HELP_COMMAND_FR);
            }
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar este comando en tu estado"));
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar este comando en tu estado"));
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(this.HELP_COMMAND);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cCannot execute this command in your current state" : "§cNo puedes ejecutar este comando en tu estado"));
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().createParty(player.getUniqueId(), player.getName());
            sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§aYou have created a new party!" : "§aAcabas de crear una party!"));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
            this.plugin.getManagerHandler().getInventoryManager().addParty(party);
            Bukkit.broadcastMessage(Color.translate("&eEl Jugador &6&l" + player.getDisplayName() + "&e Ha creado una party, pudele una invitacion para jugar con el"));
        }
        else if (args[0].equalsIgnoreCase("info")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes party."));
                return true;
            }
            final Player leader = this.plugin.getServer().getPlayer(party.getLeader());
            final StringJoiner members = new StringJoiner(", ");
            for (final UUID memberUUID : party.getAllMembersOnline()) {
                final Player member = this.plugin.getServer().getPlayer(memberUUID);
                members.add(member.getName());
            }
            if (settings.isPublicChat()) {
                player.sendMessage("§7§M----------------------------------------------------");
                player.sendMessage("§6§lParty Information:");
                player.sendMessage("§eLeader §7» §e" + leader.getName());
                player.sendMessage("§eMembers§7(" + party.getAllMembersOnline().size() + ") §7» §e" + members.toString());
                if (!party.isOpen()) {
                    player.sendMessage("§eParty State §7» §cLocked");
                }
                else {
                    player.sendMessage("§eParty State §7» §aOpen");
                }
                player.sendMessage("§7§M----------------------------------------------------");
            }
            else {
                player.sendMessage("§7§M----------------------------------------------------");
                player.sendMessage("§6§lInformacion de la party:");
                player.sendMessage("§eJefe §7» §e" + leader.getName());
                player.sendMessage("§eMiembros§7(" + party.getAllMembersOnline().size() + ") §7» §e" + members.toString());
                if (!party.isOpen()) {
                    player.sendMessage("§eParty §7» §cLocked");
                }
                else {
                    player.sendMessage("§eParty §7» §aOpen");
                }
                player.sendMessage("§7§M----------------------------------------------------");
            }
        }
        else if (args[0].equalsIgnoreCase("leave")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes party."));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou leave the party." : "§cTe acabas de salir de la party."));
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
                for (final UUID member2 : party.getMembers()) {
                    final Player pLayer = this.plugin.getServer().getPlayer(member2);
                    pLayer.sendMessage("§cYou have left the party.");
                    final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                    if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(pLayer);
                }
                this.plugin.getManagerHandler().getInventoryManager().delParty(party);
            }
            else {
                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, "§cThe player " + player.getName() + " has left the party.");
                this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
                this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
            }
        }
        else if (args[0].equalsIgnoreCase("open")) {
            final Player p = (Player)sender;
            if (this.check(p.getPlayer().getUniqueId())) {
                sender.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "You will be able to use this command again in 2 minutes" : "Tienes que esperar 2 minutos antes de realizar este comando."));
                return true;
            }
            final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party2 == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes party."));
                return true;
            }
            party2.setOpen(false);
            if (!sender.hasPermission("practice.donator.party")) {
                sender.sendMessage(ChatColor.RED + String.valueOf(settings.isPublicChat() ? "Buy a rank from /buy to use this command." : "Para comprar rango usa el comando /buy."));
            }
            else {
                @SuppressWarnings("unused")
				final UtilActionMessage actionMessage = new UtilActionMessage();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&2" + sender.getName() + " &ahas just opened his party! Type &2/party join " + sender.getName()));
                this.delay.put(p.getPlayer().getUniqueId(), System.currentTimeMillis() + 120000L);
            }
            if (party2.getLeader().equals(player.getUniqueId())) {
                if (party2.isOpen()) {
                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYour party is already open." : "§cTu party ya esta abierta."));
                    return true;
                }
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§aYour party is now open." : "§aTu party acaba de abrirse para el resto de jugadores."));
                party2.setOpen(true);
            }
            else {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't are the leader of the party." : "§cNo eres el leader de la party."));
            }
        }
        else if (args[0].equalsIgnoreCase("lock")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes una party."));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (!party.isOpen()) {
                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYour party is already locked." : "§cTu party ya esta cerrada."));
                    return true;
                }
                party.setOpen(false);
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYour party is now locked." : "§cTu party acaba de cerrarse."));
            }
            else {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't are the leader of the party." : "§cNo eres el jefe de la party."));
            }
        }
        else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party join <player>");
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou are already in a party." : "§cYa estas en la party."));
                return true;
            }
            final Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
                return true;
            }
            final Party party3 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
            if (party3 == null || !party3.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe party does not exist." : "La party no existe."));
                return true;
            }
            if (!party3.isOpen()) {
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(player)) {
                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cRequest not found." : "§CRequest no encontrado"));
                    return true;
                }
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(player, target)) {
                    player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cRequest not found." : "§CRequest no encontrado"));
                    return true;
                }
                this.plugin.getManagerHandler().getRequestManager().removePartyRequest(player, target);
            }
            if (party3.getMembers().size() >= 20) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe party is full." : "§CLa party esta llena."));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().joinParty(party3.getLeader(), player.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party3, "§aThe player " + player.getName() + " has join the party.");
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§aYou have join the party." : "§CVous avez rejoins la party."));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party3);
        }
        else if (args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party kick <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes party."));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't are the leader of the party." : "§cNo eres el jefe de la party."));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
                return true;
            }
            if (party.getLeader() == target2.getUniqueId()) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't are the leader of the party." : "§cNo eres el jefe de la party."));
                return true;
            }
            if (!party.getMembers().contains(target2.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is not in your party." : "§CEste jugador no esta en tu party."));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().leaveParty(target2.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party, "§cThe player " + target2.getName() + " has been kicked from the party.");
            target2.sendMessage(ChatColor.YELLOW + "You were kicked from the party.");
            player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou have been kicked from the party." : "§CAcabas de ser kickeado de la party"));
            final PracticePlayer ptarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target2);
            if (ptarget.getCurrentState() == PlayerState.LOBBY) {
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(target2);
            }
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't have a party." : "§cNo tienes party."));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou don't are the leader of the party." : "§cNo eres el jefe de la party."));
                return true;
            }
            if (party.isOpen()) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYour party is open." : "§cTu party esta abierta."));
                return true;
            }
            if (party.getMembers().size() >= 15) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe party is full." : "§cLa party esta llena."));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cPlayer not found." : "§cJugador no encontrado."));
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(target2.getUniqueId()) != null) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cThe player is in party." : "§CEste jugador ya esta en la party."));
                return true;
            }
            if (this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(target2) && this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(target2, player)) {
                player.sendMessage(String.valueOf(settings.isPublicChat() ? "§cYou already sent a request." : "§cYa le has enviado una invitacion."));
                return true;
            }
            if (settings.isPublicChat()) {
                player.sendMessage("§fSent a party request to §9" + target2.getName() + "§f" + "!");
            }
            else {
                player.sendMessage("§fEnviar invitacion de la party ha §9" + target2.getName() + "§f" + "!");
            }
            this.plugin.getManagerHandler().getRequestManager().addPartyRequest(target2, player);
            final UtilActionMessage actionMessage2 = new UtilActionMessage();
            actionMessage2.addText("§9" + player.getName() + "§f" + " has invited you to their party! ");
            actionMessage2.addText("                                 " + ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/party join " + player.getName());
            actionMessage2.sendToPlayer(target2);
        }
        else {
            player.sendMessage(this.HELP_COMMAND);
        }
        return true;
    }
    
    private boolean check(final UUID uuid) {
        return this.delay.containsKey(uuid) && this.delay.get(uuid) >= System.currentTimeMillis();
    }
}
