package fr.hyriode.rushtheflag.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.stream.IntStream;

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
                if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
                    player.teleport(player.getWorld().getSpawnLocation());
                }else {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(player.getWorld().getSpawnLocation().add(0, 20, 0));
                }
            }
        }
    }



}
