package me.sebaarkadia.practice.manager.managers;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.hosts.EventState;
import me.sebaarkadia.practice.hosts.PracticeEvent;
import me.sebaarkadia.practice.hosts.oitc.OITCEvent;
import me.sebaarkadia.practice.hosts.parkour.ParkourEvent;
import me.sebaarkadia.practice.hosts.redrover.RedroverEvent;
import me.sebaarkadia.practice.hosts.sumo.SumoEvent;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.player.PracticePlayer;
import me.sebaarkadia.practice.util.CustomLocation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EventManager {
    private final Map<Class<? extends PracticeEvent>, PracticeEvent> events;
    private final Practice plugin;
    private final World eventWorld;
    private HashMap<UUID, PracticeEvent> spectators;
    private long cooldown;

    public EventManager() {
        this.events = new HashMap<>();
        this.plugin = Practice.getInstance();
        Arrays.asList(SumoEvent.class, OITCEvent.class, ParkourEvent.class, RedroverEvent.class).forEach(this::addEvent);
        boolean newWorld;
        if (this.plugin.getServer().getWorld("event") == null) {
            this.eventWorld = this.plugin.getServer().createWorld(new WorldCreator("event"));
            newWorld = true;
        } else {
            this.eventWorld = this.plugin.getServer().getWorld("event");
            newWorld = false;
        }
        this.spectators = new HashMap<UUID, PracticeEvent>();
        this.cooldown = 0L;
        if (this.eventWorld != null) {
            if (newWorld) {
                this.plugin.getServer().getWorlds().add(this.eventWorld);
            }
            this.eventWorld.setTime(2000L);
            this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
            this.eventWorld.setGameRuleValue("doMobSpawning", "false");
            this.eventWorld.setStorm(false);
            this.eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        }
    }

    public PracticeEvent getByName(final String name) {
        return this.events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
    }

    public void hostEvent(final PracticeEvent event, final Player host) {
        event.setState(EventState.WAITING);
        event.setHost(host);
        event.startCountdown();
    }

    private void addEvent(final Class<? extends PracticeEvent> clazz) {
        PracticeEvent event = null;
        try {
            event = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex2) {
            final ReflectiveOperationException e = ex2;
            e.printStackTrace();
        }
        this.events.put(clazz, event);
    }

    public void addSpectatorRedrover(final Player player, final PracticePlayer playerData, final RedroverEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorSumo(final Player player, final PracticePlayer playerData, final SumoEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorOITC(final Player player, final PracticePlayer playerData, final OITCEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorParkour(final Player player, final PracticePlayer playerData, final ParkourEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            final List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        for (final Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    private void addSpectator(final Player player, final PracticePlayer playerData, final PracticeEvent event) {
        playerData.setCurrentState(PlayerState.SPECTATING);
        this.spectators.put(player.getUniqueId(), event);
        player.updateInventory();
        this.plugin.getServer().getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.hidePlayer(online);
        });
    }

    public void removeSpectator(final Player player) {
        this.getSpectators().remove(player.getUniqueId());
        this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
    }

    public boolean isPlaying(final Player player, final PracticeEvent event) {
        return event.getPlayers().containsKey(player.getUniqueId());
    }

    public PracticeEvent getEventPlaying(final Player player) {
        return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
    }

    public Map<Class<? extends PracticeEvent>, PracticeEvent> getEvents() {
        return this.events;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public HashMap<UUID, PracticeEvent> getSpectators() {
        return this.spectators;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(final long cooldown) {
        this.cooldown = cooldown;
    }

    public World getEventWorld() {
        return this.eventWorld;
    }
}
