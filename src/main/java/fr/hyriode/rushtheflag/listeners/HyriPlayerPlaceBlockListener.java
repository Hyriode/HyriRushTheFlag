package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.rushtheflag.utils.HyriRTFconfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        if(location.getX() > hyriRTFconfiguration.getTeamLocation( "blueStartFlagProtect").getX() && location.getY() > hyriRTFconfiguration.getTeamLocation( "blueStartFlagProtect").getY() && location.getZ() > hyriRTFconfiguration.getTeamLocation( "blueStartFlagProtect").getZ()) {
            if(location.getX() < hyriRTFconfiguration.getTeamLocation( "blueEndFlagProtect").getX() && location.getY() < hyriRTFconfiguration.getTeamLocation( "blueEndFlagProtect").getY() && location.getZ() < hyriRTFconfiguration.getTeamLocation( "blueEndFlagProtect").getZ()) {
                return false;
            }
        }
        if(location.getX() > hyriRTFconfiguration.getTeamLocation( "redStartFlagProtect").getX() && location.getY() > hyriRTFconfiguration.getTeamLocation( "redStartFlagProtect").getY() && location.getZ() > hyriRTFconfiguration.getTeamLocation( "redStartFlagProtect").getZ()) {
            if(location.getX() <  hyriRTFconfiguration.getTeamLocation( "redEndFlagProtect").getX() && location.getY() < hyriRTFconfiguration.getTeamLocation( "redEndFlagProtect").getY() && location.getZ() <  hyriRTFconfiguration.getTeamLocation( "redEndFlagProtect").getZ()) {
                return false;
            }
        }
        if(location.getX() >  hyriRTFconfiguration.getTeamLocation( "blueStartSpawnProtect").getX() && location.getY() > hyriRTFconfiguration.getTeamLocation( "blueStartSpawnProtect").getY() && location.getZ() > hyriRTFconfiguration.getTeamLocation( "blueStartSpawnProtect").getZ()) {
            if(location.getX() < hyriRTFconfiguration.getTeamLocation( "blueEndSpawnProtect").getX() && location.getY() < hyriRTFconfiguration.getTeamLocation( "blueEndSpawnProtect").getY() && location.getZ() < hyriRTFconfiguration.getTeamLocation( "blueEndSpawnProtect").getZ()) {
                return false;
            }
        }
        if(location.getX() > hyriRTFconfiguration.getTeamLocation( "redStartSpawnProtect").getX() && location.getY() > hyriRTFconfiguration.getTeamLocation( "redStartSpawnProtect").getY() && location.getZ() > hyriRTFconfiguration.getTeamLocation( "redStartSpawnProtect").getZ()) {
            if(location.getX() < hyriRTFconfiguration.getTeamLocation( "redEndSpawnProtect").getX() && location.getY() < hyriRTFconfiguration.getTeamLocation( "redEndSpawnProtect").getY() && location.getZ() < hyriRTFconfiguration.getTeamLocation( "redEndSpawnProtect").getZ()) {
                return false;
            }
        }
        return true;
    }
}
