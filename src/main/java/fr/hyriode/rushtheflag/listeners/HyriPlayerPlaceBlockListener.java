package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.game.HyriRTFFlag;
import fr.hyriode.rushtheflag.game.Teams;
import fr.hyriode.rushtheflag.utils.HyriRTFconfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyriPlayerPlaceBlockListener implements Listener {

    private final HyriRTF hyriRTF;
    public HyriPlayerPlaceBlockListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }



    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(!locationIsAllow(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }else {
            event.getPlayer().getInventory().addItem(new ItemStack(Material.SANDSTONE, 1));
        }
    }

    private boolean locationIsAllow(Location location) {

        ArrayList<Location> startLocations = new ArrayList<>();

        ArrayList<Location> endLocations = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                Teams.BLUE.getTeamName(),
                Teams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            startLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + ".startSpawnProtect"));
            startLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + ".startFlagProtect"));
            endLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + ".endSpawnProtect"));
            endLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + ".endFlagProtect"));
        }


        for(Location location1 : startLocations) {
            if(location.getX() >= location1.getX() && location.getY() >= location1.getY() && location.getZ() >= location1.getZ()) {
                if(location.getX() <= endLocations.get(startLocations.indexOf(location1)) .getX() && location.getY() <= endLocations.get(startLocations.indexOf(location1)).getY() && location.getZ() <= endLocations.get(startLocations.indexOf(location1)).getZ()) {
                    return false;
                }
            }
        }
        return true;
    }
}
