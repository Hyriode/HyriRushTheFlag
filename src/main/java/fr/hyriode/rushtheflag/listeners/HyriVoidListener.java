package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class HyriVoidListener implements Listener {

    HyriRTF hyriRTF;

    public HyriVoidListener(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @EventHandler
    public void onPlayerVoid(EntityDamageEvent event) {
        if(event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            if(event.getEntityType().equals(EntityType.PLAYER)) {
                event.setCancelled(true);
                Player player = (Player) event.getEntity();
                if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                    player.teleport(player.getWorld().getSpawnLocation());
                }else {
                    hyriRTF.getHyriRTFMethods().killPlayer(player);
                }
            }
        }
    }



}
