package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rushtheflag.game.HyriRTFTeams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import fr.hyriode.tools.item.ItemNBT;
import org.bukkit.Bukkit;
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

    public static final List<Player> DEAD_PLAYERS = new ArrayList<>();

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
        if(hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
            if (event.getPlayer().getItemInHand() != null) {
                if(event.getPlayer().getItemInHand().getType() != Material.AIR) {
                    ItemNBT itemNBT = new ItemNBT(event.getPlayer().getItemInHand());
                    if(itemNBT.hasTag("RTFPickaxe")) {
                        if(itemNBT.getBoolean("RTFPickaxe")) {
                            event.getPlayer().getItemInHand().setDurability(Material.IRON_PICKAXE.getMaxDurability());
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
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity() instanceof Player) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(event.getDamager() != null) {
                    if(event.getDamager() instanceof Player) {
                        hyriRTF.getGame().getPlayer(player.getUniqueId()).setLastDamagerExist((Player) event.getDamager());
                    }
                }
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!DEAD_PLAYERS.contains(player)) {
                        event.setCancelled(true);
                        hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        DEAD_PLAYERS.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity() instanceof Player) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!DEAD_PLAYERS.contains(player)) {
                        event.setCancelled(true);
                        hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        DEAD_PLAYERS.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTakeDamageByBlock(EntityDamageByBlockEvent event) {
        HyriRTF hyriRTF = this.plugin;

        if(event.getEntity() instanceof Player) {
            if(!hyriRTF.getGame().getState().equals(HyriGameState.PLAYING)) {
                event.setCancelled(true);
            }else {
                Player player = (Player) event.getEntity();
                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    if(!DEAD_PLAYERS.contains(player)) {
                        event.setCancelled(true);
                        hyriRTF.getGame().getPlayer(event.getEntity().getUniqueId()).kill();
                        DEAD_PLAYERS.add(player);
                    }
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

        Location location1 = hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.FLAG_PLACE_1_KEY);
        Location location2 = hyriRTF.getConfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.FLAG_PLACE_2_KEY);

        return isInArea(location,location1, location2);
    }

    private boolean locationIsAllow(Location location) {
        HyriRTF hyriRTF = this.plugin;

        ArrayList<Location> locations1 = new ArrayList<>();

        ArrayList<Location> locations2 = new ArrayList<>();

        List<String> teamNames = Arrays.asList(
                HyriRTFTeams.BLUE.getTeamName(),
                HyriRTFTeams.RED.getTeamName()
        );

        for(String teamName : teamNames) {
            locations1.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.SPAWN_PROTECT_1_KEY));
            locations1.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.FLAG_PROTECT_1_KEY));
            locations2.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.SPAWN_PROTECT_2_KEY));
            locations2.add(hyriRTF.getConfiguration().getLocation(teamName + HyriRTFConfiguration.FLAG_PROTECT_2_KEY));
        }

        locations1.add(hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.BORDER1));
        locations2.add(hyriRTF.getConfiguration().getLocation(HyriRTFConfiguration.BORDER2));

        for(Location location1 : locations1) {
            Location location2 = locations2.get(locations1.indexOf(location1));
            if(isInArea(location, location1, location2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInArea(Location location, Location location1, Location location2){
        return location.getX() >= Math.min(location1.getX(), location2.getX()) && location.getX() <= Math.max(location1.getX(), location2.getX()) &&
                location.getY() >= Math.min(location1.getY(), location2.getY()) && location.getY() <= Math.max(location1.getY(), location2.getY()) &&
                location.getZ() >= Math.min(location1.getZ(), location2.getZ()) && location.getZ() <= Math.max(location1.getZ(), location2.getZ());
    }
}
