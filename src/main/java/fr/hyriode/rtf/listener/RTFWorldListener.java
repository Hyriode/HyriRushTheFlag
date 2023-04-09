package fr.hyriode.rtf.listener;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.RTFConfig;
import fr.hyriode.rtf.game.RTFFlag;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import fr.hyriode.rtf.util.RTFMessage;
import fr.hyriode.rtf.util.RTFValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 14:45
 */
public class RTFWorldListener extends HyriListener<HyriRTF> {

    public static final String SANDSTONE_METADATA_KEY = "RTFSandstone";

    public RTFWorldListener(HyriRTF plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockPlaced();

        event.setCancelled(!this.canPlaceBlock(block, event.getPlayer()));

        if (event.isCancelled()) {
            player.sendMessage(RTFMessage.ERROR_PLACE_BLOCK_MESSAGE.asString(player));
        }

        if (!RTFValues.REMOVE_BLOCKS.get()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (block.getType().equals(Material.SANDSTONE)) {
                block.setType(Material.STAINED_GLASS);
                block.setData((byte) 14);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    if (block.getType().equals(Material.STAINED_GLASS)) {
                        block.breakNaturally();
                    }
                }, 3 * 20L);
            }
        }, 177 * 20L);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.setCancelled(true);

        for (Block block : event.blockList()) {
            if (block.hasMetadata(SANDSTONE_METADATA_KEY)) {
                block.breakNaturally();
            }
        }

        event.blockList().clear();
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        final ItemStack itemStack = event.getEntity().getItemStack();

        if (itemStack.getType() == Material.SANDSTONE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.hasMetadata(RTFFlag.METADATA_KEY)) {
            final List<MetadataValue> values = block.getMetadata(RTFFlag.METADATA_KEY);

            if (values != null) {
                final MetadataValue value = values.get(0);

                if (value != null) {
                    final RTFGameTeam team = (RTFGameTeam) this.plugin.getGame().getTeam(value.asString());

                    if (!team.contains(player.getUniqueId())) {
                        team.getFlag().capture(player);
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(RTFMessage.ERROR_BREAK_FLAG_MESSAGE.asString(player));
                    }
                }
            }
        } else if (!block.hasMetadata(SANDSTONE_METADATA_KEY)) {
            event.setCancelled(true);
        } else {
            block.removeMetadata(SANDSTONE_METADATA_KEY, this.plugin);
        }

        if (!RTFValues.SPLEEF.get()){
            final RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId());

            if (gamePlayer == null) {
                return;
            }

            boolean playerIsOnTheBlock = false;

            List<Location> locations = Arrays.asList(
                    event.getBlock().getLocation(),
                    event.getBlock().getLocation().add(1, 0, 0),
                    event.getBlock().getLocation().add(0, 0, 1),
                    event.getBlock().getLocation().add(-1, 0, 0),
                    event.getBlock().getLocation().add(0, 0, -1)
            );

            for (HyriGamePlayer teammate : gamePlayer.getTeam().getPlayers()) {
                if (!teammate.equals(gamePlayer)) {
                    for (Location location : locations) {
                        if (LocationUtil.roundLocation(teammate.getPlayer().getLocation().subtract(0, 1, 0), 0).equals(location)) {
                            playerIsOnTheBlock = true;
                        }
                    }
                }
            }

            if (playerIsOnTheBlock) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(RTFMessage.ERROR_SPLEEF_MESSAGE.asString(player));
            }
        }
    }

    private boolean canPlaceBlock(Block block, Player player) {
        final Location location = block.getLocation();
        final RTFConfig config = this.plugin.getConfiguration();
        final RTFConfig.Team firstTeamConfig = config.getFirstTeam();
        final RTFConfig.Team secondTeamConfig = config.getSecondTeam();

        if (block.getType() == Material.SANDSTONE) {
            block.setMetadata(SANDSTONE_METADATA_KEY, new FixedMetadataValue(this.plugin, true));

            player.getItemInHand().setAmount(64);
        } else {
            return false;
        }

        return !firstTeamConfig.getSpawnArea().asArea().isInArea(location) &&
                !secondTeamConfig.getSpawnArea().asArea().isInArea(location) &&
                config.getArea().asArea().isInArea(location);
    }

}
