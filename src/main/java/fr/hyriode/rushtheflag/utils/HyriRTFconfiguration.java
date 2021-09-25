package fr.hyriode.rushtheflag.utils;

import fr.hyriode.hyrame.configuration.HyriConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class HyriRTFconfiguration extends HyriConfiguration {

    private final JavaPlugin javaPlugin;
    private final Map<String, Location> teamsLocations;

    public HyriRTFconfiguration(JavaPlugin plugin) {
        super(plugin);
        this.javaPlugin = plugin;
        this.teamsLocations = this.teamsLocations();
    }

    private Map<String, Location> teamsLocations() {
        final Map<String, Location> teamsLocations = new HashMap<>();

        for (Field field : HyriRTFconfiguration.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                this.setDefaultTeamLocation(field.getName());
                this.teamLocation(teamsLocations, field.getName());
            }
        }

        return teamsLocations;
    }

    private void setDefaultTeamLocation(String name) {
        this.set(name, "x", 0.0d);
        this.set(name, "y", 0.0d);
        this.set(name, "z", 0.0d);
    }

    private void teamLocation(Map<String, Location> teamsLocations, String name) {
        teamsLocations.put(name, new Location(Bukkit.getWorld("world"), this.getDouble(name, "x"), this.getDouble(name, "y"), this.getDouble(name, "z")));
    }

    public Location getTeamLocation(String name) {
        return this.teamsLocations().get(name);
    }

    public Map<String, Location> getTeamsLocations() {
        return this.teamsLocations();
    }
}
