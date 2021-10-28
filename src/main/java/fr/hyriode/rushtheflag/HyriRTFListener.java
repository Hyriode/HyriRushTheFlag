package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.game.Teams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyriRTFListener extends HyriListener {

    private final HyriRTF hyriRTF;

    public HyriRTFListener(HyriRTF hyriRTF) {
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

    boolean giveB = false;

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(!locationIsAllow(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }else {
            if(giveB) {
                event.getPlayer().getInventory().addItem(new ItemStack(Material.SANDSTONE, 1));
            }else {
                giveB = true;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(hyriRTF.getBlueFlag().flagIsTaken) {
                if(hyriRTF.getBlueFlag().playerWhoTookFlag.equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                    }
                }
            }

            if(hyriRTF.getRedFlag().flagIsTaken) {
                if(hyriRTF.getRedFlag().playerWhoTookFlag.equals(event.getPlayer())) {
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
        event.setCancelled(true);

        if(event.getBlock().getType().equals(Material.SANDSTONE)) {
            if(!hyriRTF.getBlueFlag().flagIsTaken && !hyriRTF.getRedFlag().flagIsTaken) {
                event.getBlock().setType(Material.AIR);
            }else {
                if(!event.getPlayer().equals(hyriRTF.getBlueFlag().playerWhoTookFlag) || !event.getPlayer().equals(hyriRTF.getRedFlag().playerWhoTookFlag)) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
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

    private boolean locationIsCaptured(Location location, HyriGamePlayer hyriGamePlayer) {

        Location startLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.S_FLAG_PLACE_KEY);
        Location endLocation = this.hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.E_FLAG_PLACE_KEY);

        if(location.getX() >= startLocation.getX() && location.getY() >= startLocation.getY() && location.getZ() >= startLocation.getZ()) {
            if(location.getX() <= endLocation.getX() && location.getY() <= endLocation.getY() && location.getZ() <= endLocation.getZ()) {
                return true;
            }
        }
        return false;
    }

    private boolean locationIsAllow(Location location) {

        ArrayList<Location> startLocations = new ArrayList<>();

        ArrayList<Location> endLocations = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                Teams.BLUE.getTeamName(),
                Teams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            startLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.S_SPAWN_PROTECT_KEY));
            startLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.S_FLAG_PROTECT_KEY));
            endLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.E_SPAWN_PROTECT_KEY));
            endLocations.add(this.hyriRTF.getHyriRTFconfiguration().getLocation(teamName + HyriRTFConfiguration.E_FLAG_PROTECT_KEY));
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
