package fr.hyriode.rushtheflag.utils;

import fr.hyriode.hyrame.configuration.HyriConfiguration;
import fr.hyriode.rushtheflag.game.Teams;
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

public class HyriRTFconfiguration extends HyriConfiguration {

    private final JavaPlugin javaPlugin;

    public HyriRTFconfiguration(JavaPlugin plugin) {
        super(plugin);
        this.javaPlugin = plugin;
    }

    private Map<String, Location> teamsLocations() {

        final Map<String, Location> teamsLocations = new HashMap<>();

        String[] teamsNames = new String[] {
                Teams.BLUE.getTeamName(),
                Teams.RED.getTeamName()
        };

        String[] varNames = new String[] {
                "startFlagProtect",
                "endFlagProtect",
                "startSpawnProtect",
                "endSpawnProtect",
                "startFlagPlace",
                "endFlagPlace",
                "spawnLocation",
                "flagLocation"
        };

        for(String teamNames1 : teamsNames) {
            for(String varNames1 : varNames) {
                this.setDefaultLocation(teamNames1 + "." + varNames1);
                this.Location(teamsLocations, teamNames1 + "." + varNames1);
            }
        }

        return teamsLocations;

    }

    private void setDefaultLocation(String name) {
        this.set(name, "x", 0.0d);
        this.set(name, "y", 0.0d);
        this.set(name, "z", 0.0d);
    }

    private void Location(Map<String, Location> teamsLocations, String name) {
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
