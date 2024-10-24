package ronik.ffacore;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public final class FFACore extends JavaPlugin implements Listener {

    private GameMap map;
    private List<LivePlayer> players;
    private final Map<UUID, BukkitRunnable> activeTasks = new HashMap<>();
    private Scoreboard mainScoreboard;
    Objective objective;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("FFACore plugin has been enabled! Woohoo");
        getServer().getPluginManager().registerEvents(this, this);

        /////////////////////////////////
//        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
//            Bukkit.getPluginManager().registerEvents(this, this);
//        } else {
//            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
//            Bukkit.getPluginManager().disablePlugin(this);
//        }
        /////////////////////////////////

        players = new ArrayList<>();

        mainScoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        if (mainScoreboard.getObjective("ZONE PVP") != null) {
            Objects.requireNonNull(mainScoreboard.getObjective("ZONE PVP")).unregister();
        }
        objective = mainScoreboard.registerNewObjective("ZONE PVP", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String title = "§c§l  ZONE PVP BETA  "; // §c represents the color code for red
        objective.setDisplayName(title);


        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfPlayersSwitchedZones();
            }
        }.runTaskTimer(this, 1, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                manageBossBars();
            }
        }.runTaskTimer(this, 1, 40);

        new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboard();
            }
        }.runTaskTimer(this, 0, 100);

        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfPlayerInKothBox();
            }
        }.runTaskTimer(this, 3, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                addKothCaptureScore();
            }
        }.runTaskTimer(this, 5, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                saveZoneInfo();
            }
        }.runTaskTimer(this, 0, (60 * 20));

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                outputZone1Info();
//            }
//        }.runTaskTimer(this, 7, 20);


        //getServer().getScheduler().runTaskTimer(this, this::checkIfPlayersSwitchedZones, 0L, 20L); // Update every second (20 ticks)
        //getServer().getScheduler().runTaskTimer(this, this::everyFiveSeconds, 0L, 100L); // Update every 5 seconds (100 ticks)

        ArrayList<Coords> zone1Coords = new ArrayList<Coords>() {{
            add(new Coords(0, 0, 0));
            add(new Coords(100, 0, 0));
            add(new Coords(100, 0, 100));
            add(new Coords(0, 0, 100));
        }};
        KothBox zone1koth = new KothBox(new Coords(32 + 4, 0, 31 + 4), new Coords(32 - 4, 0, 31 - 4));
        Zone zone1 = new Zone(ChatColor.GRAY + "Mountains", zone1Coords, new Coords(32, 65, 31), zone1koth);

        ArrayList<Coords> zone2Coords = new ArrayList<Coords>() {{
            add(new Coords(0, 0, 0));
            add(new Coords(-100, 0, 0));
            add(new Coords(-100, 0, 100));
            add(new Coords(0, 0, 100));
        }};
        KothBox zone2koth = new KothBox(new Coords(-70 + 4, 0, 43 + 4), new Coords(-70 - 4, 0, 43 - 4));
        Zone zone2 = new Zone(ChatColor.GOLD + "Taiga", zone2Coords, new Coords(-70, 66, 43), zone2koth);

        ArrayList<Coords> zone3Coords = new ArrayList<Coords>() {{
            add(new Coords(0, 0, 0));
            add(new Coords(-100, 0, 0));
            add(new Coords(-100, 0, -100));
            add(new Coords(0, 0, -100));
        }};
        KothBox zone3koth = new KothBox(new Coords(-74 + 4, 0, -50 + 4), new Coords(-74 - 4, 0, -50 - 4));
        Zone zone3 = new Zone(ChatColor.DARK_GREEN + "Forest", zone3Coords, new Coords(-74, 66, -50), zone3koth);

        ArrayList<Coords> zone4Coords = new ArrayList<Coords>() {{
            add(new Coords(0, 0, 0));
            add(new Coords(100, 0, 0));
            add(new Coords(100, 0, -100));
            add(new Coords(0, 0, -100));
        }};
        KothBox zone4koth = new KothBox(new Coords(28 + 4, 0, -29 + 4), new Coords(28 - 4, 0, -29 - 4));
        Zone zone4 = new Zone(ChatColor.BLUE + "City", zone4Coords, new Coords(28, 65, -29), zone4koth);

        Zone[] zones = {zone1, zone2, zone3, zone4};
        map = new GameMap(zones);
        loadZoneInfo();
        updateScoreboard();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveZoneInfo();
        getLogger().info("FFACore plugin has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!DatabaseHandler.isPlayerInDatabase(player.getUniqueId().toString())) {
            DatabaseHandler.addPlayerToDatabase(player.getUniqueId().toString());

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.GREEN + "You have been added to the database!");
                }
            }.runTaskLater(this, 60);
        }

        LivePlayer joinedPlayer = new LivePlayer(player);
        Location playerLocation = player.getLocation();
        joinedPlayer.setCurrentZone(map.getZoneName(new Coords(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ())));
        players.add(joinedPlayer);
        getLogger().info("PLAYER " + joinedPlayer.getPlayer().getName() + " HAS BEEN ADDED TO THE PLAYER LIST!");
        Bukkit.getServer().broadcastMessage("PLAYER " + joinedPlayer.getPlayer().getName() + " HAS BEEN ADDED TO THE PLAYER LIST!");
        // log player list
        for (LivePlayer livePlayer : players) {
            getLogger().info("PLAYER " + livePlayer.getPlayer().getName() + " IS IN THE PLAYER LIST!");
        }
        for (LivePlayer livePlayer : players) {
            Bukkit.getServer().broadcastMessage("PLAYER " + livePlayer.getPlayer().getName() + " IS IN THE PLAYER LIST!");
        }

        player.setScoreboard(mainScoreboard);
        map.getZone(new Coords(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ())).getBossBar().addPlayer(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (LivePlayer livePlayer : players) {
            if (livePlayer.getPlayer() == player) {
                if (map.getZoneFromZoneName(livePlayer.getCurrentZoneName()) != null) {
                    map.getZoneFromZoneName(livePlayer.getCurrentZoneName()).onPlayerNotInKoth(player);
                }
                players.remove(livePlayer);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // if player was killed by another player
        String playerUUID = event.getEntity().getUniqueId().toString();
        DatabaseHandler.addDeath(playerUUID);
        if (event.getEntity().getKiller() != null) {
            String killerUUID = Objects.requireNonNull(event.getEntity().getKiller()).getUniqueId().toString();
            DatabaseHandler.addKill(killerUUID);
        }

        // get the location of the player's death
        Location deathLocation = event.getEntity().getLocation();
        Coords deathCoords = new Coords(deathLocation.getBlockX(), deathLocation.getBlockY(), deathLocation.getBlockZ());
        // get the zone the player died in
        Zone deathZone = map.getZone(deathCoords);
        // if the player died in a zone
        if (deathZone != null) {
            if (deathZone.isCoordsInKoth(deathCoords)) {
                deathZone.onPlayerNotInKoth(event.getEntity());
            }
        }
    }

    public void checkIfPlayersSwitchedZones() {
        for (LivePlayer player : players) {
            Location playerLocation = player.getPlayer().getLocation();
            Coords playerCoords = new Coords(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
            String currentZoneName = player.getCurrentZoneName();
            String newZoneName = map.getZoneName(playerCoords);
            if (!Objects.equals(currentZoneName, newZoneName)) {
                player.setCurrentZone(newZoneName);
                // remove player from old zone's boss bar
                if (currentZoneName != null && map.getZoneFromZoneName(currentZoneName) != null) {
                    map.getZoneFromZoneName(currentZoneName).getBossBar().removePlayer(player.getPlayer());
                }
                if (map.getZone(playerCoords) != null) {
                    map.getZone(playerCoords).getBossBar().addPlayer(player.getPlayer());
                }
                if (newZoneName != null) {
                    if (map.getZoneFromZoneName(newZoneName) != null){
                        player.getPlayer().sendTitle(map.getZoneFromZoneName(newZoneName).getTeamThatCaptured(), ChatColor.BLUE + "Entering " + ChatColor.DARK_BLUE + newZoneName, 10, 70, 20);
                    }
                    Zone a = map.getZoneFromZoneName(currentZoneName);
                    if (a != null) {
                        a.onPlayerNotInKoth(player.getPlayer());
                    }
                } else {
                    player.getPlayer().sendTitle("", ChatColor.RED + "You are now in the wilderness", 10, 70, 20);
                }
            }


            Player pl = player.getPlayer();
            String teamName = "%betterteams_name%";
            teamName = PlaceholderAPI.setPlaceholders(pl, teamName);
            if (teamName.equals("")) {
                if (!Tools.coordsInPolygon(playerCoords, new Coords[]{new Coords(-21, 0, -25),
                        new Coords(20, 0, -25),
                        new Coords(20, 0, 16),
                        new Coords(-21, 0, 16)})) {
                    // teleport to spawn
                    pl.teleport(new Location(pl.getWorld(), 0, 69, -5));
                    // send message
                    pl.sendMessage(ChatColor.RED + "You are not in a team! Please join or create a team to play!");
                }
            }
        }
    }

    private void checkZoneProtectorBreaks(BlockBreakEvent event) {
        for (Zone zone : map.getZones()) {
            if (Tools.locationToCoords(event.getBlock().getLocation()).equals(zone.getZoneProtectorCoords())) {
                event.setCancelled(true);
                if (zone.getStatus().equals("Captured")) {
                    if (PlaceholderAPI.setPlaceholders(event.getPlayer(), "%betterteams_name%").equals(zone.getTeamThatCaptured())) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own Zone Protector!");
                        continue;
                    }
                    zone.onZoneProtectorBreak();
                    if (zone.getHealth() >= 1) {
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have damaged the Zone Protector"));
                    } else {
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You have destroyed the Zone Protector"));
                    }
                }
                break;
            }
        }
    }

    private void checkIfPlayerInKothBox() {
        for (LivePlayer player : players) {
            String currentZoneName = player.getCurrentZoneName();
            if (currentZoneName != null && map.getZoneFromZoneName(currentZoneName) != null) {
                Zone currentZone = map.getZoneFromZoneName(currentZoneName);
                if (currentZone.isPlayerInKoth(player.getPlayer())) {
                    currentZone.onPlayerInKoth(player.getPlayer());
                } else {
                    currentZone.onPlayerNotInKoth(player.getPlayer());
                }
            }
        }
    }

    private void addKothCaptureScore() {
        // for every zone in the map
        for (Zone zone : map.getZones()) {
            zone.addKothCaptureScore();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkZoneProtectorBreaks(event);
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
            Inventory potionChest = Bukkit.createInventory(null, 54, "HEALING STATION");

            ItemStack upgradedPotion = new ItemStack(Material.SPLASH_POTION);
            PotionMeta potionMeta = (PotionMeta) upgradedPotion.getItemMeta();

            assert potionMeta != null;
            potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));


            upgradedPotion.setItemMeta(potionMeta);



            for (int i = 0; i < 54; i++) {
                potionChest.setItem(i, upgradedPotion);
            }

            event.getPlayer().openInventory(potionChest);
        }
    }

    public void manageBossBars() {
        for (Zone zone : map.getZones()) {
            zone.updateBossBar();
        }
    }

    public void updateScoreboard() {
        // Clear old scores from the objective
        for (String entry : mainScoreboard.getEntries()) {
            mainScoreboard.resetScores(entry);
        }

        Zone[] allzones = map.getZones();
        for (int i = allzones.length; i >= 1; i--) {
            Score zoneScore = objective.getScore(allzones[i-1].getStatusPrefix() + " " + ChatColor.RED + allzones[i-1].getName() + ":   " + ChatColor.WHITE + allzones[i-1].getRealHealth());
            zoneScore.setScore(i);
        }
//        Score zone1Score = objective.getScore(ChatColor.RED + allzones[0].getName() + ":   " + ChatColor.WHITE + allzones[0].getHealth());
//        zone1Score.setScore(8);
//        Score unclaimed1 = objective.getScore(ChatColor.DARK_GRAY + allzones[0].get());
//        unclaimed1.setScore(7);
//        Score zone2Score = objective.getScore(ChatColor.RED + allzones[1].getName() + ":   " + ChatColor.WHITE + allzones[1].getHealth());
//        zone2Score.setScore(6);
//        Score unclaimed2 = objective.getScore(ChatColor.DARK_GRAY + "Unclaimed  ");
//        unclaimed2.setScore(5);
//        Score zone3Score = objective.getScore(ChatColor.RED + allzones[2].getName() + ":   " + ChatColor.WHITE + allzones[2].getHealth());
//        zone3Score.setScore(4);
//        Score unclaimed3 = objective.getScore(ChatColor.DARK_GRAY + "Unclaimed   ");
//        unclaimed3.setScore(3);
//        Score zone4Score = objective.getScore(ChatColor.RED + allzones[3].getName() + ":   " + ChatColor.WHITE + allzones[3].getHealth());
//        zone4Score.setScore(2);
//        Score unclaimed4 = objective.getScore(ChatColor.DARK_GRAY + "Unclaimed    ");
//        unclaimed4.setScore(1);

//        for (int x = 1; x <= 8; x++) {
//
//            if (x % 2 == 0) {
//                Score zoneScore = objective.getScore(ChatColor.RED + allzones[(x/2)-1].getName() + ":   " + ChatColor.WHITE + allzones[(x/2)-1].getHealth());
//                zoneScore.setScore(x);
//            } else {
//                Score teamOwned = objective.getScore(ChatColor.DARK_GRAY + "Unclaimed");
//                teamOwned.setScore(x);
//            }
//        }

        Score top = objective.getScore(ChatColor.GRAY + "---------------- ");
        top.setScore(allzones.length + 1);

        Score bottom = objective.getScore(ChatColor.GRAY + "----------------");
        bottom.setScore(0);
    }

    @EventHandler
    public void onEnderPearlThrow(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            outputZone1Info();
        }
    }

    public void displayTitle(Player player, String title) {
        player.sendTitle(title, "", 10, 70, 20);
    }

    public void outputZone1Info() {
        Bukkit.getServer().broadcastMessage(map.getZoneFromZoneName("Forest").getAllZoneInfo());
        for (LivePlayer player : players) {
            Bukkit.getServer().broadcastMessage(player.getPlayer().getName());
        }
    }

    public void saveZoneInfo() {
        for (Zone zone : map.getZones()) {
            zone.saveInfoToDatabase();
        }
    }

    public void loadZoneInfo() {
        for (Zone zone : map.getZones()) {
            zone.loadInfoFromDatabase();
        }
    }

}
