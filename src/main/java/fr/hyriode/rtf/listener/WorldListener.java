package fr.hyriode.rtf.listener;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.HyriRTFConfig;
import fr.hyriode.rtf.game.HyriRTFFlag;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.HyriRTFGameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 14:45
 */
public class WorldListener extends HyriListener<HyriRTF> {

    public static final String SANDSTONE_METADATA_KEY = "RTFSandstone";

    public WorldListener(HyriRTF plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        event.setCancelled(!this.canPlaceBlock(event.getBlockPlaced(), event.getPlayer()));

        if (event.isCancelled()) {
            player.sendMessage(ChatColor.RED + HyriRTF.getLanguageManager().getValue(player, "error.place-block"));
        }

        new BukkitRunnable() {
            private int index = 150;

            @Override
            public void run() {
                if(index == 120) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if(event.getBlockPlaced().getType().equals(Material.SANDSTONE)) {
                                event.getBlockPlaced().setType(Material.STAINED_GLASS);
                                event.getBlockPlaced().setData((byte) 14);
                                event.getBlockPlaced().setMetadata(SANDSTONE_METADATA_KEY, new FixedMetadataValue(plugin, true));
                            }
                        }
                    });
                }
                if(index == 0) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if(event.getBlockPlaced().getType().equals(Material.STAINED_GLASS)) {
                                event.getBlockPlaced().breakNaturally();
                            }
                        }
                    });
                    this.cancel();
                }
                index--;
            }
        }.runTaskTimerAsynchronously(this.plugin, 0, 20);
    }
    
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
        for (Block block : event.blockList()) {
            if(block.hasMetadata(SANDSTONE_METADATA_KEY)) {
                block.breakNaturally();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.hasMetadata(HyriRTFFlag.METADATA_KEY)) {
            final List<MetadataValue> values = block.getMetadata(HyriRTFFlag.METADATA_KEY);

            if (values != null) {
                final MetadataValue value = values.get(0);

                if (value != null) {
                    final HyriRTFGameTeam team = (HyriRTFGameTeam) this.plugin.getGame().getTeam(value.asString());

                    if (!team.contains(player.getUniqueId())) {
                        team.getFlag().capture(player);
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + HyriRTF.getLanguageManager().getValue(player, "error.break-block.flag"));
                    }
                }
            }
        } else if (!block.hasMetadata(SANDSTONE_METADATA_KEY)) {
            event.setCancelled(true);
        }else {
            final HyriRTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId());
            boolean playerIsOnTheBlock = false;

            List<Location> locations = Arrays.asList(
                    event.getBlock().getLocation(),
                    event.getBlock().getLocation().add(1, 0, 0),
                    event.getBlock().getLocation().add(0, 0, 1),
                    event.getBlock().getLocation().add(-1, 0, 0),
                    event.getBlock().getLocation().add(0, 0, -1)
            );

            for(HyriGamePlayer gamePlayer1 : gamePlayer.getTeam().getPlayers()) {
                if(!gamePlayer1.equals(gamePlayer)) {
                    for(Location location : locations) {
                        if(LocationUtil.roundLocation(gamePlayer1.getPlayer().getLocation().subtract(0, 1, 0), 0).equals(location)) {
                            playerIsOnTheBlock = true;
                        }
                    }
                }
            }
            if(playerIsOnTheBlock) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + HyriRTF.getLanguageManager().getValue(player, "error.break-block.spleef"));
            }
        }
    }

    private boolean canPlaceBlock(Block block, Player player) {
        final Location location = block.getLocation();
        final HyriRTFConfig config = this.plugin.getConfiguration();
        final HyriRTFConfig.Team firstTeamConfig = config.getFirstTeam();
        final HyriRTFConfig.Team secondTeamConfig = config.getSecondTeam();

        if (block.getType() == Material.SANDSTONE) {
            block.setMetadata(SANDSTONE_METADATA_KEY, new FixedMetadataValue(this.plugin, true));

            player.getItemInHand().setAmount(64);
        }else {
            return false;
        }

        return !LocationUtil.isInArea(location, firstTeamConfig.getSpawnAreaFirst(), firstTeamConfig.getSpawnAreaSecond()) &&
                !LocationUtil.isInArea(location, secondTeamConfig.getSpawnAreaFirst(), secondTeamConfig.getSpawnAreaSecond()) &&
                LocationUtil.isInArea(location, config.getGameAreaFirst(), config.getGameAreaSecond());
    }

}
