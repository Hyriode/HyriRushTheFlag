package fr.hyriode.rtf.listener;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.HyriRTFConfig;
import fr.hyriode.rtf.game.HyriRTFGame;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
public class PlayerListener extends HyriListener<HyriRTF> {

    public PlayerListener(HyriRTF plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final HyriRTFGame game = this.plugin.getGame();
        final Player player = event.getPlayer();

        if (game.getState() == HyriGameState.PLAYING) {
            final Location location = event.getTo();
            final HyriRTFConfig config = this.plugin.getConfiguration();
            final HyriRTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if (!LocationUtil.isInArea(location, config.getGameAreaFirst(), config.getGameAreaSecond())) {
                if (location.getY() <= LocationUtil.getLocationWithLowestHeight(config.getGameAreaFirst(), config.getGameAreaSecond()).getY()) {

                    if (!game.getDeadPlayers().contains(gamePlayer)) {
                        gamePlayer.kill(true);
                    }
                }
            }

            if (gamePlayer.getTeam().isInBase(player)) {
                if (game.isHoldingFlag(player)) {
                    game.getHoldingFlag(player).broughtBack();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (this.plugin.getGame().getState() == HyriGameState.PLAYING) {
            if (event.getEntity() instanceof Player) {
                final Player target = (Player) event.getEntity();
                final HyriRTFGame game = this.plugin.getGame();
                final HyriRTFGamePlayer gamePlayer = game.getPlayer(target.getUniqueId());

                if (event.getDamager() instanceof Player) {
                    gamePlayer.setLastHitter((Player) event.getDamager());
                } else if (event.getDamager() instanceof Projectile) {
                    final Projectile projectile = (Projectile) event.getDamager();

                    if (projectile.getShooter() instanceof Player) {
                        gamePlayer.setLastHitter((Player) projectile.getShooter());
                    }
                }

                if(target.getHealth() - event.getFinalDamage() <= 0) {
                    target.setHealth(20);

                    if (!game.getDeadPlayers().contains(gamePlayer)) {
                        gamePlayer.kill(false);
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (this.plugin.getGame().getState() == HyriGameState.PLAYING) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                final HyriRTFGame game = this.plugin.getGame();
                final HyriRTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

                if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    event.setCancelled(true);
                }

                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    player.setHealth(20);

                    if (!game.getDeadPlayers().contains(gamePlayer)) {
                        gamePlayer.kill(false);
                    }
                }
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
        if(this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(this.plugin.getGame().getPlayer(event.getWhoClicked().getUniqueId()).hasFlag()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        if(this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            final Player player = event.getPlayer();
            final HyriRTFGame game = this.plugin.getGame();
            final HyriRTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            if(gamePlayer.hasFlag()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

}
