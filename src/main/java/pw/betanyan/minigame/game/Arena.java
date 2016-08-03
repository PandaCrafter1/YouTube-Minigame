package pw.betanyan.minigame.game;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pw.betanyan.minigame.Minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {

    private Timer timer;

    private String name;

    private GameState state;

    private List<String> ingame;

    private List<Location> spawns;

    private Map<String, Integer> kills;
    private Map<String, Scoreboard> scoreboards;

    private int maxPlayers;

    private Location lobbySpawn;

    private Sign sign;

    public Arena(String name, Location lobbySpawn, List<Location> spawns, int maxPlayers, Sign sign) {
        this.name = name;
        this.maxPlayers = maxPlayers;

        this.spawns = spawns;

        this.lobbySpawn = lobbySpawn;

        this.state = GameState.LOBBY;

        this.kills = new HashMap<>();
        this.scoreboards = new HashMap<>();

        this.sign = sign;

        this.timer = new Timer(this);
        this.ingame = new ArrayList<>();

        Minigame.getInstance().getArenaManager().addArena(this);
    }

    public Timer getTimer() {
        return timer;
    }

    public void updateSign() {

        getSign().setLine(0, ChatColor.AQUA + getName());
        getSign().setLine(1, ChatColor.GREEN + getState().toString());
        getSign().setLine(2, String.valueOf(ChatColor.YELLOW) + getIngame().size() + "/" + getMaxPlayers());

        getSign().update(true);

    }
    
    public void joinPlayer(Player player) {

        if (getIngame().size() < maxPlayers) {

            if (!getIngame().contains(player.getName())) {

                if (getState() == GameState.LOBBY) {

                    getIngame().add(player.getName());
                    broadcast("&a" + player.getName() + " has joined the game! " + getIngame().size() + "/" + getMaxPlayers());

                    player.teleport(getLobbySpawn());

                    if (getIngame().size() == getMaxPlayers() / 2) {

                        getTimer().setPaused(false);

                    }

                    kills.put(player.getName(), 0);

                    updateSign();

                    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

                    Objective obj = board.registerNewObjective("stats", "dummy");
                    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                    obj.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Minigame "
                            + ChatColor.YELLOW + getTimer().formatTime());

                    Team name = board.registerNewTeam("name");

                    name.addEntry(ChatColor.RED.toString());
                    name.setPrefix(ChatColor.GREEN + "Name: ");
                    name.setSuffix(player.getName());

                    obj.getScore(ChatColor.RED.toString()).setScore(3);

                    Team blank1 = board.registerNewTeam("blank1");
                    blank1.addEntry(ChatColor.BLUE.toString());

                    obj.getScore(ChatColor.BLUE.toString()).setScore(2);

                    Team kills = board.registerNewTeam("kills");

                    kills.addEntry(ChatColor.GREEN.toString());
                    kills.setPrefix(ChatColor.GREEN + "Kills: ");
                    kills.setSuffix(ChatColor.RED + this.kills.get(player.getName()).toString());

                    obj.getScore(ChatColor.GREEN.toString()).setScore(1);

                    Team state = board.registerNewTeam("state");

                    state.addEntry(ChatColor.YELLOW.toString());
                    state.setPrefix(ChatColor.GREEN + "GameState: ");
                    state.setSuffix(ChatColor.RED + Character.toString(this.getState().toString().charAt(0))
                            + this.getState().toString().toLowerCase().substring(1));

                    obj.getScore(ChatColor.YELLOW.toString()).setScore(0);

                    player.setScoreboard(board);


                } else {

                    player.sendMessage(ChatColor.RED + "The game already started!");

                }

            } else {

                player.sendMessage(ChatColor.RED + "You are already in this game!");

            }

        } else {

            player.sendMessage(ChatColor.RED + "There is already enough people!");

        }
        
    }

    public String getName() {
        return name;
    }

    public List<String> getIngame() {
        return ingame;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void broadcast(String msg) {
        for (String player : ingame) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Minigame] &r" + msg));
        }
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(List<Location> spawns) {
        this.spawns = spawns;
    }

    public void addSpawn(Location location) {
        spawns.add(location);
    }

    public Sign getSign() {
        return sign;
    }

    public void updateScoreboards() {

        for (String user : scoreboards.keySet()) {

            Scoreboard board = scoreboards.get(user);

            board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Minigame "
                    + ChatColor.YELLOW + getTimer().formatTime());

            board.getTeam("kills").setSuffix(kills.get(user).toString());

        }

    }

    public Map<String, Integer> getKills() {
        return kills;
    }
}
