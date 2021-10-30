package fr.hyriode.rushtheflag;

import fr.hyriode.common.item.ItemNBT;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.game.Teams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyriRTFListener extends HyriListener {

    @EventHandler
    public void onPlayerVoid(EntityDamageEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

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


    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(!locationIsAllow(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        if(hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(hyriRTF.getBlueFlag().isFlagTaken()) {
                if(hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }

            if(hyriRTF.getRedFlag().isFlagTaken()) {
                if(hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        event.setCancelled(true);

        ItemNBT itemNBT = new ItemNBT(event.getPlayer().getItemInHand());
        if(itemNBT.hasTag("RTFPickaxe")) {
            if(itemNBT.getBoolean("RTFPickaxe")) {
                event.getPlayer().getItemInHand().setDurability((short) (event.getPlayer().getItemInHand().getDurability() - Material.IRON_PICKAXE.getMaxDurability()));
            }
        }

        if(event.getBlock().getType().equals(Material.SANDSTONE)) {
            if(!hyriRTF.getBlueFlag().isFlagTaken() && !hyriRTF.getRedFlag().isFlagTaken()) {
                event.getBlock().setType(Material.AIR);
            }else {
                if(!event.getPlayer().equals(hyriRTF.getBlueFlag().getPlayerWhoTookFlag()) || !event.getPlayer().equals(hyriRTF.getRedFlag().getPlayerWhoTookFlag())) {
                    event.getBlock().setType(Material.AIR);
                }
            }
        }else if(event.getBlock().getType().equals(Material.WOOL)) {
            if(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(Teams.BLUE.getTeamName())) {
                if(hyriRTF.getRedFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                    hyriRTF.getRedFlag().playerTakeFlag(event.getPlayer());
                }
            }else if(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(Teams.RED.getTeamName())) {
                if(hyriRTF.getBlueFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                    hyriRTF.getBlueFlag().playerTakeFlag(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getHyrame().getGameManager().getCurrentGame().getState().equals(HyriGameState.PLAYING)) {
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

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

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

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            event.setCancelled(true);
        }else {
            if(hyriRTF.getBlueFlag().isFlagTaken()) {
                if(hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
            if(hyriRTF.getRedFlag().isFlagTaken()) {
                if(hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean locationIsCaptured(Location location, HyriGamePlayer hyriGamePlayer) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        Location startLocation = hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.S_FLAG_PLACE_KEY);
        Location endLocation = hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.E_FLAG_PLACE_KEY);

        if(location.getX() >= startLocation.getX() && location.getY() >= startLocation.getY() && location.getZ() >= startLocation.getZ()) {
            if(location.getX() <= endLocation.getX() && location.getY() <= endLocation.getY() && location.getZ() <= endLocation.getZ()) {
                return true;
            }
        }
        return false;
    }

    private boolean locationIsAllow(Location location) {
        HyriRTF hyriRTF = (HyriRTF) this.pluginSupplier.get();

        ArrayList<Location> startLocations = new ArrayList<>();

        ArrayList<Location> endLocations = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                Teams.BLUE.getTeamName(),
                Teams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            startLocations.add(hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.S_SPAWN_PROTECT_KEY));
            startLocations.add(hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.S_FLAG_PROTECT_KEY));
            endLocations.add(hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.E_SPAWN_PROTECT_KEY));
            endLocations.add(hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.E_FLAG_PROTECT_KEY));
        }

        Location s_border = hyriRTF.getHyriRTFconfiguration().getLocation(HyriRTFConfiguration.S_BORDER);
        Location e_border = hyriRTF.getHyriRTFconfiguration().getLocation(HyriRTFConfiguration.E_BORDER);

        if(location.getX() < s_border.getX() || location.getY() < s_border.getY() || location.getZ() < s_border.getZ()) {
            return false;
        }

        if(location.getX() > e_border.getX() || location.getY() > e_border.getY() || location.getZ() > e_border.getZ()) {
            return false;
        }

        for(Location location1 : startLocations) {
            if(location.getX() >= location1.getX() && location.getY() >= location1.getY() && location.getZ() >= location1.getZ()) {
                if(location.getX() <= endLocations.get(startLocations.indexOf(location1)) .getX() && location.getY() <= endLocations.get(startLocations.indexOf(location1)).getY() && location.getZ() <= endLocations.get(startLocations.indexOf(location1)).getZ()) {
                    return false;
                }
            }
        }
        return true;
    }
}
