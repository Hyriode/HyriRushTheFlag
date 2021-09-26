package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.rushtheflag.utils.HyriRTFconfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class HyriPlayerPlaceBlockListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final HyriRTFconfiguration hyriRTFconfiguration;
    public HyriPlayerPlaceBlockListener(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.hyriRTFconfiguration = new HyriRTFconfiguration(javaPlugin);
    }



    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Bukkit.broadcastMessage("lessg");
        if(!locationIsAllow(event.getBlock().getLocation())) {
            Bukkit.broadcastMessage("less");
            event.getBlockPlaced().setType(event.getBlockAgainst().getType());
            event.setCancelled(true);
        }
        Bukkit.broadcastMessage("less");
    }

    private boolean locationIsAllow(Location location) {

        List<Location> startLocations = Arrays.asList(
            hyriRTFconfiguration.getLocation("blueStartFlagProtect"),
            hyriRTFconfiguration.getLocation("redStartFlagProtect"),
            hyriRTFconfiguration.getLocation("blueStartSpawnProtect"),
            hyriRTFconfiguration.getLocation("redStartSpawnProtect")
        );

        List<Location> endLocations = Arrays.asList(
                hyriRTFconfiguration.getLocation("blueEndFlagProtect"),
                hyriRTFconfiguration.getLocation("redEndFlagProtect"),
                hyriRTFconfiguration.getLocation("blueEndSpawnProtect"),
                hyriRTFconfiguration.getLocation("redEndSpawnProtect")
        );

        for(Location location1 : startLocations) {
            if(location.getX() > location1.getX() && location.getY() > location1.getY() && location.getZ() > location1.getZ()) {
                if(location.getX() < endLocations.get(startLocations.indexOf(location1)) .getX() && location.getY() < endLocations.get(startLocations.indexOf(location1)).getY() && location.getZ() < endLocations.get(startLocations.indexOf(location1)).getZ()) {
                    return false;
                }
            }
        }
        return true;
    }
}
