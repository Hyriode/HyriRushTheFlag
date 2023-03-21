package fr.hyriode.rtf.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

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

    @HyriEventHandler
    public void onDeath(HyriGameDeathEvent event) {
        final RTFGamePlayer gamePlayer = (RTFGamePlayer) event.getGamePlayer();

        if (!((RTFGameTeam) gamePlayer.getTeam()).hasLife()) {
            event.addMessage(HyriLanguageMessage.get("message.eliminated"));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final RTFGame game = this.plugin.getGame();
        final Player player = event.getPlayer();

        if (game.getState() == HyriGameState.PLAYING) {
            final RTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (gamePlayer == null) {
                return;
            }

            if (((RTFGameTeam) gamePlayer.getTeam()).isInBase(player) && game.isHoldingFlag(player)) {
                game.getHoldingFlag(player).broughtBack();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        final HyriGame<?> game = this.plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() == HyriGameState.PLAYING) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        final RTFGame game = this.plugin.getGame();

        if (game == null) {
            return;
        }

        if (event.getEntity() instanceof Fireball) {
            event.getEntity().remove();
            event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 3);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();

        if (event.getSlotType() == InventoryType.SlotType.ARMOR || (currentItem != null && currentItem.getType().equals(Material.WOOL))) {
            event.setCancelled(true);
        }

        if (this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            final RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getWhoClicked().getUniqueId());

            if (gamePlayer != null && gamePlayer.hasFlag()) {
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

            if (gamePlayer != null && gamePlayer.hasFlag()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
