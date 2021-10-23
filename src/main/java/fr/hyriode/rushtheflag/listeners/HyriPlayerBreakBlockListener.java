package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.game.Teams;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class HyriPlayerBreakBlockListener implements Listener {

    private final HyriRTF hyriRTF;

    public HyriPlayerBreakBlockListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        event.setCancelled(true);
        if(event.getBlock().getType().equals(Material.SANDSTONE)) {
            if(!hyriRTF.getBlueFlag().flagIsTaken && !hyriRTF.getRedFlag().flagIsTaken) {
                event.getBlock().setType(Material.AIR);
            }else {
                if(!event.getPlayer().equals(hyriRTF.getBlueFlag().playerWhoTookFlag) || !event.getPlayer().equals(hyriRTF.getRedFlag().playerWhoTookFlag)) {
                    event.getBlock().setType(Material.AIR);
                }
            }
        }else if(event.getBlock().getType().equals(Material.WOOL)) {
            if(hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(Teams.BLUE.getTeamName())) {
                if(hyriRTF.getRedFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                    hyriRTF.getRedFlag().playerTakeFlag(event.getPlayer());
                }
            }else if(hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(Teams.RED.getTeamName())) {
                if(hyriRTF.getBlueFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                    hyriRTF.getBlueFlag().playerTakeFlag(event.getPlayer());
                }
            }
        }
    }
}
