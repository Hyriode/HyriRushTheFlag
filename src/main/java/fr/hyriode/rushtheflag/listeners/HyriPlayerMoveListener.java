package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.Bukkit;
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
        if(hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
            if(hyriRTF.getBlueFlag().flagIsTaken) {
                if(hyriRTF.getBlueFlag().playerWhoTookFlag.equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }

            if(hyriRTF.getRedFlag().flagIsTaken) {
                if(hyriRTF.getRedFlag().playerWhoTookFlag.equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }
        }
    }

    private boolean locationIsCaptured(Location location, HyriGamePlayer hyriGamePlayer) {

        Location startLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + ".startFlagPlace");
        Location endLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + ".endFlagPlace");

        if(location.getX() >= startLocation.getX() && location.getY() >= startLocation.getY() && location.getZ() >= startLocation.getZ()) {
            if(location.getX() <= endLocation.getX() && location.getY() <= endLocation.getY() && location.getZ() <= endLocation.getZ()) {
                return true;
            }
        }
        return false;
    }
}
