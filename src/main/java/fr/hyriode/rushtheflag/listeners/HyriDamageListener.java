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

    private final HyriRTF hyriRTF;

    public HyriDamageListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    this.hyriRTF.getHyriRTFMethods().killPlayer((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    this.hyriRTF.getHyriRTFMethods().killPlayer((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    hyriRTF.getHyriRTFMethods().killPlayer((Player) event.getEntity());
                }
            }
        }
    }

}
