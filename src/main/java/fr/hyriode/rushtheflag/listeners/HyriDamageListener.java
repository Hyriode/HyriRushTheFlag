package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class HyriDamageListener implements Listener {

    private HyriRTF hyriRTF;

    public HyriDamageListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent event) {
        if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
            if(event.getEntity().getType().equals(EntityType.PLAYER)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
            if(event.getEntity().getType().equals(EntityType.PLAYER)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
            if(event.getEntity().getType().equals(EntityType.PLAYER)) {
                event.setCancelled(true);
            }
        }
    }
}
