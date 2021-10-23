package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HyriClickInventoryListener implements Listener {

    private final HyriRTF hyriRTF;

    public HyriClickInventoryListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
            event.setCancelled(true);
        }else {
            if(hyriRTF.getBlueFlag().flagIsTaken) {
                if(hyriRTF.getBlueFlag().playerWhoTookFlag.equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
            if(hyriRTF.getRedFlag().flagIsTaken) {
                if(hyriRTF.getRedFlag().playerWhoTookFlag.equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
