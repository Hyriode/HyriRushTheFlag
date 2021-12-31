package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.game.HyriRTFTeams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import fr.hyriode.tools.LocationUtil;
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

import static fr.hyriode.tools.LocationUtil.isInArea;

public class HyriRTFListener extends HyriListener<HyriRTF> {

    public final List<Player> deadPlayers = new ArrayList<>();

    private final HyriRTF hyriRTF;

    public HyriRTFListener(HyriRTF plugin) {
        super(plugin);
        this.hyriRTF = this.plugin;
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
                    hyriRTF.getGame().getPlayer(player.getUniqueId()).kill();
                }
            }
        }
    }


    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(locationIsAllow(event.getBlockPlaced().getLocation())) {
                event.getPlayer().getItemInHand().setAmount(64);
            }else {
                event.setCancelled(true);
            }
        }else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if(this.hyriRTF.getBlueFlag().isFlagTaken()) {
                if(this.hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        this.hyriRTF.getGame().captureFlag(this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                        this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).setWoolsBroughtBack(hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getWoolsBroughtBack() + 1);
                    }
                }
            }

            if(this.hyriRTF.getRedFlag().isFlagTaken()) {
                if(this.hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(event.getPlayer())) {
                    if(locationIsCaptured(event.getTo(), this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()))) {
                        this.hyriRTF.getGame().captureFlag(this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam());
                        this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).setWoolsBroughtBack(this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getWoolsBroughtBack() + 1);
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
        if(this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if (event.getPlayer().getItemInHand() != null) {
                if(event.getPlayer().getItemInHand().getType() != Material.AIR) {
                    ItemNBT itemNBT = new ItemNBT(event.getPlayer().getItemInHand());
                    if(itemNBT.hasTag("RTFPickaxe")) {
                        if(itemNBT.getBoolean("RTFPickaxe")) {
                            event.getPlayer().getItemInHand().setDurability((short) 0);
                        }
                    }

                    if(event.getBlock().getType().equals(Material.SANDSTONE)) {
                        if(!this.hyriRTF.getBlueFlag().isFlagTaken() && !this.hyriRTF.getRedFlag().isFlagTaken()) {
                            event.getBlock().setType(Material.AIR);
                        }else {
                            if(!event.getPlayer().equals(this.hyriRTF.getBlueFlag().getPlayerWhoTookFlag()) || !event.getPlayer().equals(this.hyriRTF.getRedFlag().getPlayerWhoTookFlag())) {
                                event.getBlock().setType(Material.AIR);
                            }
                        }
                    }else if(event.getBlock().getType().equals(Material.WOOL)) {
                        if(this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(HyriRTFTeams.BLUE.getTeamName())) {
                            if(this.hyriRTF.getRedFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                                this.hyriRTF.getRedFlag().playerTakeFlag(event.getPlayer());
                            }
                        }else if(this.hyriRTF.getGame().getPlayer(event.getPlayer().getUniqueId()).getTeam().getName().equalsIgnoreCase(HyriRTFTeams.RED.getTeamName())) {
                            if(this.hyriRTF.getBlueFlag().getFlagLocation().equals(event.getBlock().getLocation())) {
                                this.hyriRTF.getBlueFlag().playerTakeFlag(event.getPlayer());
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTakeDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if(!this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(event.getDamager() != null) {
                    if(event.getDamager() instanceof Player) {
                        this.hyriRTF.getGame().getPlayer(player.getUniqueId()).setLastDamagerExist((Player) event.getDamager());
                    }
                }
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!deadPlayers.contains(player)) {
                        event.setCancelled(true);
                        this.hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        deadPlayers.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(!this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!deadPlayers.contains(player)) {
                        event.setCancelled(true);
                        this.hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        deadPlayers.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        if(event.getEntity() instanceof Player) {
            if(!this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!deadPlayers.contains(player)) {
                        event.setCancelled(true);
                        this.hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        deadPlayers.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!this.hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            event.setCancelled(true);
        }else {
            if(this.hyriRTF.getBlueFlag().isFlagTaken()) {
                if(this.hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
            if(this.hyriRTF.getRedFlag().isFlagTaken()) {
                if(this.hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(event.getWhoClicked())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean locationIsCaptured(Location location, HyriGamePlayer hyriGamePlayer) {
        Location location1 = this.hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.FLAG_PLACE_1_KEY);
        Location location2 = this.hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.FLAG_PLACE_2_KEY);

        return isInArea(location,location1, location2);
    }

    private boolean locationIsAllow(Location location) {
        ArrayList<Location> locations1 = new ArrayList<>();

        ArrayList<Location> locations2 = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                HyriRTFTeams.BLUE.getTeamName(),
                HyriRTFTeams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            locations1.add(this.hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.SPAWN_PROTECT_1_KEY));
            locations1.add(this.hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.FLAG_PROTECT_1_KEY));
            locations2.add(this.hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.SPAWN_PROTECT_2_KEY));
            locations2.add(this.hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.FLAG_PROTECT_2_KEY));
        }

        for(Location location1 : locations1) {
            Location location2 = locations2.get(locations1.indexOf(location1));
            if(LocationUtil.isInArea(location, location1, location2)) {
                return false;
            }else if(!LocationUtil.isInArea(location,this.hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.BORDER1),this.hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.BORDER2))) {
                return false;
            }
        }
        return true;
    }
}
