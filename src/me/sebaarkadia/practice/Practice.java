package me.sebaarkadia.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import lombok.Getter;
import me.sebaarkadia.practice.commands.AcceptCommand;
import me.sebaarkadia.practice.commands.ArenaCommand;
import me.sebaarkadia.practice.commands.BuilderCommand;
import me.sebaarkadia.practice.commands.CreditsCommand;
import me.sebaarkadia.practice.commands.DuelCommand;
import me.sebaarkadia.practice.commands.EventCommand;
import me.sebaarkadia.practice.commands.EventManagerCommand;
import me.sebaarkadia.practice.commands.GamemodeCommand;
import me.sebaarkadia.practice.commands.HostCommand;
import me.sebaarkadia.practice.commands.InventoryCommand;
import me.sebaarkadia.practice.commands.JoinCommand;
import me.sebaarkadia.practice.commands.KitCommand;
import me.sebaarkadia.practice.commands.LagCommand;
import me.sebaarkadia.practice.commands.LeaveCommand;
import me.sebaarkadia.practice.commands.ListCommand;
import me.sebaarkadia.practice.commands.MainCommand;
import me.sebaarkadia.practice.commands.MessageCommand;
import me.sebaarkadia.practice.commands.ModCommand;
import me.sebaarkadia.practice.commands.NewVideoCommand;
import me.sebaarkadia.practice.commands.PTeleportCommand;
import me.sebaarkadia.practice.commands.PartyCommand;
import me.sebaarkadia.practice.commands.PingCommand;
import me.sebaarkadia.practice.commands.RecordingCommand;
import me.sebaarkadia.practice.commands.RenameCommand;
import me.sebaarkadia.practice.commands.ResetEloCommand;
import me.sebaarkadia.practice.commands.ScoreboardCommand;
import me.sebaarkadia.practice.commands.SeeAllCommand;
import me.sebaarkadia.practice.commands.SetMaxPlayersCommand;
import me.sebaarkadia.practice.commands.SpectateCommand;
import me.sebaarkadia.practice.commands.TopEloCommand;
import me.sebaarkadia.practice.commands.TournamentCommand;
import me.sebaarkadia.practice.commands.WhoisCommand;
import me.sebaarkadia.practice.commands.chat.ChatCommand;
import me.sebaarkadia.practice.commands.chat.ChatControlHandler;
import me.sebaarkadia.practice.commands.essentials.CommandHandler;
import me.sebaarkadia.practice.listeners.BlockListener;
import me.sebaarkadia.practice.listeners.ChatListener;
import me.sebaarkadia.practice.listeners.DuelListener;
import me.sebaarkadia.practice.listeners.EntityListener;
import me.sebaarkadia.practice.listeners.InventoryListener;
import me.sebaarkadia.practice.listeners.PlayerListener;
import me.sebaarkadia.practice.listeners.StartListener;
import me.sebaarkadia.practice.listeners.XpListener;
import me.sebaarkadia.practice.manager.ManagerHandler;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.runnables.UpdateInventory;
import me.sebaarkadia.practice.tournament.TournamentListener;
import me.sebaarkadia.practice.util.Color;
import me.sebaarkadia.practice.util.ColorAPI;
import me.sebaarkadia.practice.util.ColorAPI2;
import me.sebaarkadia.practice.util.LocationSerializer;
import me.sebaarkadia.practice.util.cooldown.Cooldowns;
import me.sebaarkadia.practice.util.database.PracticeDatabase;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;

public class Practice extends JavaPlugin {

    public static Practice instance;
    private ManagerHandler managerHandler;
    PracticeDatabase practiceDatabase;
    private Location spawn;
    private Location editkit;
    private Location editkitnpc;
    public static Pattern splitPattern;
    public static Pattern UUID_PATTER;
    public static EntityPlayer npc;
    private Permission perms;
    public Chat chat;
    private ChatControlHandler chatcontrolHandler;
    private CommandHandler commandExecutor;
    private Map<String, String> lastMessage;
    public String wngnq;
    
    static {
        Practice.UUID_PATTER = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    }
    
    public Practice() {
    	this.wngnq = "Incorrect LICENSE! Disabling plugin. If you need an LICENSE, get one by contacting @JavaPinq_!";
    }
    
	private void wqminoiwn()
	{
		Bukkit.getPluginManager().disablePlugin(this);
	}

	public void getAPI()
	{
		try
		{
			final URL url = new URL("https://pastebin.com/raw/xwdM1z2B");
			final ArrayList<Object> lines = new ArrayList<Object>();
			final URLConnection connection = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				lines.add(line);
			}
			if (!lines.contains(this.getConfig().getString("LICENSE.ID")) && this.getConfig().getString("LICENSE.ID") != null)
			{
				this.getLogger().log(Level.SEVERE, this.wngnq);
				this.wqminoiwn();
			}
			else if (this.getConfig().getString("LICENSE.ID") == null)
			{
				this.getLogger().log(Level.SEVERE, "§4Add an LICENSE in the config!");
				this.wqminoiwn();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE, "Error! Disabling plugin.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
    
    public void onEnable(Command cmd, final String fallbackPrefix) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e");
        this.chatcontrolHandler = new ChatControlHandler(this);
        this.managerHandler = new ManagerHandler(this);
        this.practiceDatabase = new PracticeDatabase(this);
        if (this.getConfig().contains("spawn")) {
            this.spawn = LocationSerializer.deserializeLocation(this.getConfig().getString("spawn"));
        }
        if (this.getConfig().contains("editkit")) {
            this.editkit = LocationSerializer.deserializeLocation(this.getConfig().getString("editkit"));
        }
        if (this.getConfig().contains("editkitnpc")) {
            this.editkitnpc = LocationSerializer.deserializeLocation(this.getConfig().getString("editkitnpc"));
        }
        Practice.splitPattern = Pattern.compile("\\s");
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7---------------- ------------------------"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&6&ljPractice has been activated!"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&eAuthor&7: &6JavaPinq_"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&eVersion&7: &e1.0"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&eDiscord&7: &eJavaPinq_#7125"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&eTwitter&7: &b@ZukaLLC"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7----------------------------------------"));
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new EntityListener(this), this);
        pm.registerEvents(new DuelListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new TournamentListener(), this);
        pm.registerEvents(new XpListener(), this);
        pm.registerEvents(new StartListener(), this);
        this.commandExecutor = new CommandHandler(this);
        registerCooldowns();
        this.getCommand("arena").setExecutor(new ArenaCommand(this));
        this.getCommand("seeall").setExecutor(new SeeAllCommand(this));
        this.getCommand("mod").setExecutor(new ModCommand(this));
        this.getCommand("tp").setExecutor(new PTeleportCommand(this));
        this.getCommand("duel").setExecutor(new DuelCommand(this));
        this.getCommand("accept").setExecutor(new AcceptCommand(this));
        this.getCommand("kit").setExecutor(new KitCommand(this));
        this.getCommand("spectate").setExecutor(new SpectateCommand(this));
        this.getCommand("builder").setExecutor(new BuilderCommand(this));
        this.getCommand("inventory").setExecutor(new InventoryCommand(this));
        this.getCommand("party").setExecutor(new PartyCommand(this));
        this.getCommand("reset").setExecutor(new ResetEloCommand(this));
        this.getCommand("join").setExecutor(new JoinCommand(this));
        this.getCommand("leave").setExecutor(new LeaveCommand(this));
        this.getCommand("credits").setExecutor(new CreditsCommand(this));
        this.getCommand("tournament").setExecutor(new TournamentCommand(this));
        this.getCommand("scoreboard").setExecutor(new ScoreboardCommand(this));
        this.getCommand("host").setExecutor(new HostCommand(this));
        this.getCommand("ping").setExecutor(new PingCommand());
        this.getCommand("practice").setExecutor(new MainCommand(this));
        this.getCommand("topelo").setExecutor(new TopEloCommand(this));
        this.getCommand("chat").setExecutor(new ChatCommand());
        this.getCommand("msg").setExecutor(new MessageCommand());
        this.getCommand("whois").setExecutor(new WhoisCommand());
        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("lag").setExecutor(new LagCommand());
        this.getCommand("gamemode").setExecutor(new GamemodeCommand());
        this.getCommand("newvideo").setExecutor(new NewVideoCommand());
        this.getCommand("recording").setExecutor(new RecordingCommand());
        this.getCommand("rename").setExecutor(new RenameCommand());
        this.getCommand("setmaxplayers").setExecutor(new SetMaxPlayersCommand());
        this.registerCommands();
        this.setUpPermissions();
        this.setUpChat();
        new UpdateInventory().runTaskTimer(this, 0L, 40L);
        ColorAPI.Color();
        ColorAPI2.Color();
        this.saveConfig();
        this.getAPI();
    }
    
    public void registerCommand(Command cmd, final String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }

    private void registerCommand(final Command cmd) {
        this.registerCommand(cmd, this.getName());
    }

    private void registerCommands() {
        Arrays.asList(new EventCommand(),
        		new EventManagerCommand()).forEach(command -> this.registerCommand(command, this.getName()));
    }
    
	public static void registerCooldowns() {
        Cooldowns.createCooldown("recording_delay");
        Cooldowns.createCooldown("newvideo_delay");
    }
    
    public void npc(final Player p) {
        final PracticePlayer practicePlayer = this.getManagerHandler().getPracticePlayerManager().getPracticePlayer(p);
        final File file2 = new File(getInstance().getDataFolder(), "NpcLoc.yml");
        final YamlConfiguration c = YamlConfiguration.loadConfiguration(file2);
        final double x2 = c.getDouble("x");
        final double y = c.getDouble("y");
        final double z = c.getDouble("z");
        if (!practicePlayer.isCreateNpc()) {
            final MinecraftServer nmsServer = ((CraftServer)Bukkit.getServer()).getServer();
            final WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
            (Practice.npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.fromString("d58ef82d-16e9-45e0-b08a-fb73ab62feaf"), p.getName()), new PlayerInteractManager((World)nmsWorld))).setLocation(x2, y, z, 100.0f, 0.0f);
            final PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(Practice.npc));
            practicePlayer.setCreateNcp(true);
        }
    }
    
    public void onDisable() {
        this.saveConfig();
        this.reloadConfig();
        this.managerHandler.disable();
    }
    
    private boolean setUpPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration(Permission.class);
        this.perms = (Permission)rsp.getProvider();
        return this.perms != null;
    }
    
    private boolean setUpChat() {
        final RegisteredServiceProvider<Chat> rsp = (RegisteredServiceProvider<Chat>)this.getServer().getServicesManager().getRegistration(Chat.class);
        this.chat = (Chat)rsp.getProvider();
        return this.chat != null;
    }
    
    public ManagerHandler getManagerHandler() {
        return this.managerHandler;
    }
    
    public static Practice getInstance() {
    	return (Practice) JavaPlugin.getPlugin(Practice.class);
    }
    
    public double getTps() {
        return Bukkit.spigot().getTPS()[0];
    }
    
    public PracticeDatabase getPracticeDatabase() {
        return this.practiceDatabase;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public void setSpawn(final Location spawn) {
        this.spawn = spawn;
    }
    
    public Location geteditkit() {
        return this.editkit;
    }
    
    public Location geteditkitnpc() {
        return this.editkitnpc;
    }
    
    public void seteditkitnpc(final Location editkitnpc) {
        this.editkitnpc = editkitnpc;
    }
    
    public ChatControlHandler getChatControlHandler() {
        return this.chatcontrolHandler;
    }
    
    public CommandHandler getCommandHandlerE() {
        return this.commandExecutor;
    }
    
    public void seteditkit(final Location editkit) {
        this.editkit = editkit;
    }
    
    public Map<String, String> getLastMessage() {
        return this.lastMessage;
    }
}
