package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.game.HyriRTFTeams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import fr.hyriode.tools.item.ItemNBT;
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

public class HyriRTFListener extends HyriListener<HyriRTF> {

    public HyriRTFListener(HyriRTF plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerVoid(EntityDamageEvent event) {
        HyriRTF hyriRTF = this.plugin;

        if(event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            if(event.getEntityType().equals(EntityType.PLAYER)) {
                event.setCancelled(true);
                Player player = (Player) event.getEntity();
                if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                    player.teleport(player.getWorld().getSpawnLocation());
                }else {
                    hyriRTF.getGame().getPlayer(player.getUniqueId()).kill();
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
        HyriRTF hyriRTF = this.plugin;

        if(hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(hyriRTF.getBlueFlag().isFlagTaken()) {
                if(hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getGame().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                        hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).setWoolsBroughtBack(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getWoolsBroughtBack() + 1);
                    }
                }
            }

            if(hyriRTF.getRedFlag().isFlagTaken()) {
                if(hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        hyriRTF.getGame().captureFlag(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                        hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).setWoolsBroughtBack(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getWoolsBroughtBack() + 1);
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
        HyriRTF hyriRTF = this.plugin;

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
            if(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(HyriRTFTeams.BLUE.getTeamName())) {
                if(hyriRTF.getRedFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                    hyriRTF.getRedFlag().playerTakeFlag(event.getPlayer());
                }
            }else if(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(HyriRTFTeams.RED.getTeamName())) {
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
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(event.getDamager().getType().equals(EntityType.PLAYER)) {
                    hyriRTF.getGame().getPlayer(player.getUniqueId()).setLastDamagerExist((Player) event.getDamager());
                }
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HyriRTF hyriRTF = this.plugin;

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
        HyriRTF hyriRTF = this.plugin;

        Location startLocation = hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.S_FLAG_PLACE_KEY);
        Location endLocation = hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.E_FLAG_PLACE_KEY);

        if(location.getX() >= startLocation.getX() && location.getY() >= startLocation.getY() && location.getZ() >= startLocation.getZ()) {
            if(location.getX() <= endLocation.getX() && location.getY() <= endLocation.getY() && location.getZ() <= endLocation.getZ()) {
                return true;
            }
        }
        return false;
    }

    private boolean locationIsAllow(Location location) {
        HyriRTF hyriRTF = this.plugin;

        ArrayList<Location> startLocations = new ArrayList<>();

        ArrayList<Location> endLocations = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                HyriRTFTeams.BLUE.getTeamName(),
                HyriRTFTeams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            startLocations.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.S_SPAWN_PROTECT_KEY));
            startLocations.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.S_FLAG_PROTECT_KEY));
            endLocations.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.E_SPAWN_PROTECT_KEY));
            endLocations.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.E_FLAG_PROTECT_KEY));
        }

        Location s_border = hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.S_BORDER);
        Location e_border = hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.E_BORDER);

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
