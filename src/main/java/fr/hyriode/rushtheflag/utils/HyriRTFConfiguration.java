package fr.hyriode.rushtheflag.utils;

import fr.hyriode.hyrame.configuration.HyriConfiguration;
import fr.hyriode.rushtheflag.game.HyriRTFTeams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HyriRTFConfiguration extends HyriConfiguration {

    public static String S_FLAG_PROTECT_KEY = ".startFlagProtect";
    public static String E_FLAG_PROTECT_KEY = ".endFlagProtect";
    public static String S_SPAWN_PROTECT_KEY = ".startSpawnProtect";
    public static String E_SPAWN_PROTECT_KEY = ".endSpawnProtect";
    public static String S_FLAG_PLACE_KEY = ".startFlagPlace";
    public static String E_FLAG_PLACE_KEY = ".endFlagPlace";
    public static String FLAG_LOCATION_KEY = ".flagLocation";
    public static String SPAWN_LOCATION_KEY = ".spawnLocation";
    public static String S_BORDER = "border.start";
    public static String E_BORDER = "border.end";

    private final JavaPlugin javaPlugin;

    public HyriRTFConfiguration(JavaPlugin plugin) {
        super(plugin);
        this.javaPlugin = plugin;
    }

    private Map<String, Location> teamsLocations() {

        final Map<String, Location> teamsLocations = new HashMap<>();

        String[] teamsNames = new String[] {
                HyriRTFTeams.BLUE.getTeamName(),
                HyriRTFTeams.RED.getTeamName()
        };

        String[] varNames = new String[] {
                HyriRTFConfiguration.S_FLAG_PROTECT_KEY,
                HyriRTFConfiguration.E_FLAG_PROTECT_KEY,
                HyriRTFConfiguration.S_SPAWN_PROTECT_KEY,
                HyriRTFConfiguration.E_SPAWN_PROTECT_KEY,
                HyriRTFConfiguration.S_FLAG_PLACE_KEY,
                HyriRTFConfiguration.E_FLAG_PLACE_KEY,
                HyriRTFConfiguration.FLAG_LOCATION_KEY,
                HyriRTFConfiguration.SPAWN_LOCATION_KEY
        };

        for(String teamNames1 : teamsNames) {
            for(String varNames1 : varNames) {
                this.setDefaultLocation(teamNames1 + varNames1);
                this.location(teamsLocations, teamNames1 + varNames1);
            }
        }

        this.setDefaultLocation(S_BORDER);
        this.location(teamsLocations, S_BORDER);

        this.setDefaultLocation(E_BORDER);
        this.location(teamsLocations, E_BORDER);

        return teamsLocations;

    }

    private void setDefaultLocation(String name) {
        this.set(name, "x", 0.0d);
        this.set(name, "y", 0.0d);
        this.set(name, "z", 0.0d);
    }

    private void location(Map<String, Location> teamsLocations, String name) {
        teamsLocations.put(name, new Location(Bukkit.getWorld("world"), this.getDouble(name, "x"), this.getDouble(name, "y"), this.getDouble(name, "z")));
    }

    public Location getLocation(String name) {
        return this.teamsLocations().get(name);
    }

    public Map<String, Location> getTeamsLocations() {
        return this.teamsLocations();
    }

    public void setHotbar(Player player, int swordSlot, int gappleSword, int pickaxeSlot) {
        final File file = new File(javaPlugin.getDataFolder() + "/hotbars.yml");
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        configuration.set("hotbars." + player.getUniqueId() + ".swordSlot", swordSlot);
        configuration.set("hotbars." + player.getUniqueId() + ".gappleSlot", gappleSword);
        configuration.set("hotbars." + player.getUniqueId() + ".pickaxeSlot", pickaxeSlot);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int swordSlot(Player player) {
        final File file = new File(javaPlugin.getDataFolder() + "/hotbars.yml");
        if(file.exists()) {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection configurationSection = configuration.getConfigurationSection("hotbars.");
            if(configurationSection.get(player.getUniqueId().toString()) != null) {
                return configurationSection.getInt(player.getUniqueId().toString() + ".swordSlot");
            }
        }
        return 0;
    }

    public int gappleSlot(Player player) {
        final File file = new File(javaPlugin.getDataFolder() + "/hotbars.yml");
        if(file.exists()) {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection configurationSection = configuration.getConfigurationSection("hotbars.");
            if(configurationSection.get(player.getUniqueId().toString()) != null) {
                return configurationSection.getInt(player.getUniqueId().toString() + ".gappleSlot");
            }
        }
        return 1;
    }

    public int pickaxeSlot(Player player) {
        final File file = new File(javaPlugin.getDataFolder() + "/hotbars.yml");
        if(file.exists()) {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection configurationSection = configuration.getConfigurationSection("hotbars.");
            if(configurationSection.get(player.getUniqueId().toString()) != null) {
                return configurationSection.getInt(player.getUniqueId().toString() + ".pickaxeSlot");
            }
        }
        return 2;
    }
}
