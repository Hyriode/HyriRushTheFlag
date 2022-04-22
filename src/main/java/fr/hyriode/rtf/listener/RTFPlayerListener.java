package fr.hyriode.rtf.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.RTFGamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 19:36
 */
public class RTFPlayerListener extends HyriListener<HyriRTF> {

    public RTFPlayerListener(HyriRTF plugin) {
        super(plugin);
        HyriAPI.get().getEventBus().register(this);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final RTFGame game = this.plugin.getGame();
        final Player player = event.getPlayer();

        if (game.getState() == HyriGameState.PLAYING) {
            final RTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (gamePlayer.getTeam().isInBase(player)) {
                if (game.isHoldingFlag(player)) {
                    game.getHoldingFlag(player).broughtBack();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (this.plugin.getGame().getState() == HyriGameState.PLAYING) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.getCurrentItem().getType().equals(Material.WOOL)) {
            event.setCancelled(true);
        }
        if (this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            if (this.plugin.getGame().getPlayer(event.getWhoClicked().getUniqueId()).hasFlag()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            final Player player = event.getPlayer();
            final RTFGame game = this.plugin.getGame();
            final RTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (gamePlayer.hasFlag()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

}
