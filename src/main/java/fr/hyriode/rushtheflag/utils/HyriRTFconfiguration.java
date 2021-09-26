package fr.hyriode.rushtheflag.utils;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.configuration.HyriConfiguration;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.game.Teams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HyriRTFconfiguration extends HyriConfiguration {

    private final JavaPlugin javaPlugin;
    private final HyriRTF hyriRTF;
    private final Map<String, Location> teamsLocations;

    public HyriRTFconfiguration(JavaPlugin plugin) {
        super(plugin);
        this.javaPlugin = plugin;
        this.teamsLocations = this.teamsLocations();
        this.hyriRTF = (HyriRTF)javaPlugin;
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
}
