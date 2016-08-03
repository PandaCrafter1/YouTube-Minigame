package pw.betanyan.minigame;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pw.betanyan.minigame.commands.ArenaCommand;
import pw.betanyan.minigame.game.Arena;
import pw.betanyan.minigame.game.ArenaManager;
import pw.betanyan.minigame.listeners.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minigame extends JavaPlugin {

    private static Minigame instance;

    private ArenaManager arenaManager;

    private File configFile;
    private FileConfiguration config;

    @Override
    public void onEnable() {

        instance = this;

        arenaManager = new ArenaManager();

        loadConfig();

        registerListeners(new PlayerInteract(this), new SignChange(), new PlayerDeath(), new PlayerDamage(), new PlayerFood(), new PlayerMove());

        registerCommands();

    }

    private void loadConfig() {

        if (!getDataFolder().exists()) {

            getDataFolder().mkdir();

        }

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.contains("arenas")) {
            config.set("arenas", "");
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadArenas();

    }

    private void loadArenas() {

        if (!config.getString("arenas").equals("")) {

            for (String arena : config.getConfigurationSection("arenas").getKeys(false)) {

                new Arena(arena, unserializeLocation(config.getString("arenas." + arena + ".lobbyspawn")),
                        listStrToLocs(config.getStringList("arenas." + arena + ".spawns")),
                        config.getInt("arenas." + arena + ".maxplayers"),
                        config.getString("arenas." + arena + ".sign").equals("NOEXIST") ? null :
                                (Sign) unserializeLocation(config.getString("arenas." + arena + ".sign")).getWorld()
                                        .getBlockAt(unserializeLocation(config.getString("arenas." + arena + ".sign"))).getState());

            }

        }

    }

    private void registerCommands() {
        getCommand("arena").setExecutor(new ArenaCommand());
    }

    private void registerListeners(Listener... listeners) {

        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "=" + loc.getX() + "=" + loc.getY() + "=" + loc.getZ() + "=" + loc.getPitch() + "=" + loc.getYaw();
    }

    public Location unserializeLocation(String loc) {

        if (loc.equals("NOEXIST")) {
            return null;
        }

        String[] split = loc.split("=");
        return new Location(getServer().getWorld(split[0]),
                Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Double.parseDouble(split[3]), Float.parseFloat(split[4]),
                Float.parseFloat(split[5]));
    }

    public List<Location> listStrToLocs(List<String> locs) {

        List<Location> toReturn = new ArrayList<>();

        locs.stream().forEach(loc -> toReturn.add(unserializeLocation(loc)));

        return toReturn;

    }

    public String chatColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Minigame getInstance() {
        return instance;
    }


}
