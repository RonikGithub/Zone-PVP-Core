package ronik.ffacore;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

public class Zone {
    private final String name;
    private ArrayList<Coords> corners;
    private Coords zoneProtectorCoords;
    private String teamThatCaptured;
    private int health;
    private final BossBar bossBar;
    private final KothBox kothBox;
    private String status;
    private ArrayList<String> teamNamesInKothBox;
    private int captureScore;
    private ArrayList<String> playerNamesInKothBox;

    public Zone(String name, ArrayList<Coords> corners, Coords zoneProtectorCoords, KothBox kothBox) {
        this.name = name;
        this.corners = corners;
        this.zoneProtectorCoords = zoneProtectorCoords;
        this.health = 100;
        this.status = "Unclaimed";
        this.bossBar = Bukkit.createBossBar(name + ChatColor.RED + " " + status, BarColor.RED, BarStyle.SOLID);
        this.bossBar.setProgress(1);
        this.bossBar.setVisible(true);
        this.kothBox = kothBox;
        this.teamNamesInKothBox = new ArrayList<>();
        this.playerNamesInKothBox = new ArrayList<>();
        this.captureScore = 0;
    }

    public boolean isPlayerInZone(Coords playerCoords) {
        int i, j;
        boolean c = false;
        for (i = 0, j = corners.size() - 1; i < corners.size(); j = i++) {
            if (((corners.get(i).getZ() > playerCoords.getZ()) != (corners.get(j).getZ() > playerCoords.getZ())) &&
                    (playerCoords.getX() < (corners.get(j).getX() - corners.get(i).getX()) * (playerCoords.getZ() - corners.get(i).getZ()) / (corners.get(j).getZ() - corners.get(i).getZ()) + corners.get(i).getX())) {
                c = !c;
            }
        }
        return c;
    }

    public void updateBossBar() {
        if (status.equals("Unclaimed")) {
            bossBar.setColor(BarColor.RED);
            bossBar.setProgress((double) captureScore / 100);
            bossBar.setTitle(ChatColor.BLUE + name + ChatColor.GREEN + " " + captureScore);
        } else if (status.equals("Captured")) {
            bossBar.setColor(BarColor.BLUE);
            bossBar.setProgress((double) health / 100);
            bossBar.setTitle(ChatColor.BLUE + name + ChatColor.WHITE + " Captured by " + ChatColor.BLUE + teamThatCaptured + ChatColor.GREEN + " " + health);
        }
    }

    public BossBar getBossBar() { return bossBar; }

    public String getName() { return name; }

    public int getHealth() { return health; }

    public Coords getZoneProtectorCoords() { return zoneProtectorCoords; }

    public void dealDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void onZoneProtectorBreak() {
        if (health >= 1) {
            dealDamage(1);
            updateBossBar();
        }
        if (health <= 0) {
            status = "Unclaimed";
            teamThatCaptured = null;
            health = 100;
            captureScore = 0;
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + name + ChatColor.WHITE + " has been " + ChatColor.RED + "unclaimed" + ChatColor.WHITE + "!");
            updateBossBar();
        }
    }

    public void onPlayerInKoth(Player player) {
        String teamName = "%betterteams_name%";
        teamName = PlaceholderAPI.setPlaceholders(player, teamName);
        if (!playerNamesInKothBox.contains(player.getName())) {
            playerNamesInKothBox.add(player.getName());
        }
        if (!teamNamesInKothBox.contains(teamName)) {
            teamNamesInKothBox.add(teamName);
        }
    }

    public void onPlayerNotInKoth(Player player) {
        String teamName = "%betterteams_name%";
        teamName = PlaceholderAPI.setPlaceholders(player, teamName);
        if (playerNamesInKothBox.contains(player.getName())) {
            playerNamesInKothBox.remove(player.getName());
        }
        for (String playerName : playerNamesInKothBox) {
            Player playerInKoth = Bukkit.getPlayer(playerName);
            if (playerInKoth != null) {
                if (teamName.equals(PlaceholderAPI.setPlaceholders(playerInKoth, "%betterteams_name%"))) {
                    return;
                }
            }
        }
        if (teamNamesInKothBox.contains(teamName)) {
            teamNamesInKothBox.remove(teamName);
        }
    }

    public boolean isPlayerInKoth(Player player) {
        return kothBox.contains(Tools.locationToCoords(player.getLocation()));
    }

    public boolean isCoordsInKoth(Coords coords) {
        return kothBox.contains(coords);
    }

    public void addKothCaptureScore() {
        if (teamNamesInKothBox.size() != 1 || status.equals("Captured")) {
            captureScore = 0;
            return;
        }
        if (captureScore < 100) {
            captureScore++;
        }
        if (captureScore == 100) {
            status = "Captured";
            teamThatCaptured = teamNamesInKothBox.get(0);
            health = 100;
            captureScore = 0;
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + teamThatCaptured + ChatColor.WHITE + " has captured " + ChatColor.BLUE + name + ChatColor.WHITE + "!");
        }
        updateBossBar();
    }

    public String getStatus() { return status; }

    public ArrayList<String> getTeamNamesInKothBox() { return teamNamesInKothBox; }

    public int getRealHealth() {
        switch (status) {
            case "Unclaimed":
                return captureScore;
            case "Captured":
                return health;
        }
        return 0;
    }

    public String getStatusPrefix() {
        switch (status) {
            case "Unclaimed":
                return ChatColor.DARK_GRAY + "■";
            case "Captured":
                return ChatColor.GREEN + "■";
        }
        return "";
    }

    public String getAllZoneInfo() {
        String zoneInfo = "";
        zoneInfo += "Name: " + name + "\n";
        zoneInfo += "Corners: " + corners.toString() + "\n";
        zoneInfo += "Zone Protector: " + zoneProtectorCoords.toString() + "\n";
        zoneInfo += "Koth Box: " + kothBox.toString() + "\n";
        zoneInfo += "Status: " + status + "\n";
        zoneInfo += "Team that captured: " + teamThatCaptured + "\n";
        zoneInfo += "Health: " + health + "\n";
        zoneInfo += "Capture Score: " + captureScore + "\n";
        zoneInfo += "Players in Koth Box: " + playerNamesInKothBox.toString() + "\n";
        zoneInfo += "Teams in Koth Box: " + teamNamesInKothBox.toString() + "\n\n\n";
        return zoneInfo;
    }

    public String getTeamThatCaptured() {
        if (status.equals("Unclaimed")) {
            return ChatColor.DARK_GRAY + "Unclaimed" + ChatColor.RESET;
        } else if (teamThatCaptured == null) {
            return ChatColor.DARK_GRAY + "Unclaimed" + ChatColor.RESET;
        }
        return teamThatCaptured;
    }

    public void saveInfoToDatabase() {
        DatabaseHandler.saveZoneInfo(name, teamThatCaptured, health, status, captureScore);
    }

    public void loadInfoFromDatabase() {

        String[] info = DatabaseHandler.getZoneInfo(name);
        if (info == null) {
            return;
        }
        this.teamThatCaptured = info[0];
        this.health = Integer.parseInt(info[1]);
        this.status = info[2];
        this.captureScore = Integer.parseInt(info[3]);
        updateBossBar();
    }
}
