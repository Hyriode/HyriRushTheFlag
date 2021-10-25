package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HyriPlayerMoveListener implements Listener {

    private final HyriRTF hyriRTF;

    public HyriPlayerMoveListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(hyriRTF.getBlueFlag().flagIsTaken) {
                if(hyriRTF.getBlueFlag().playerWhoTookFlag.equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }

            if(hyriRTF.getRedFlag().flagIsTaken) {
                if(hyriRTF.getRedFlag().playerWhoTookFlag.equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }
        }
    }

    private boolean locationIsCaptured(Location location, HyriGamePlayer hyriGamePlayer) {

        Location startLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.S_FLAG_PLACE_KEY);
        Location endLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.E_FLAG_PLACE_KEY);

        if(location.getX() >= startLocation.getX() && location.getY() >= startLocation.getY() && location.getZ() >= startLocation.getZ()) {
            if(location.getX() <= endLocation.getX() && location.getY() <= endLocation.getY() && location.getZ() <= endLocation.getZ()) {
                return true;
            }
        }
        return false;
    }
}
