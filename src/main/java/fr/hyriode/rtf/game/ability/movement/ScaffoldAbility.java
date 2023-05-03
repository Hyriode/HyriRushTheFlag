package fr.hyriode.rtf.game.ability.movement;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ScaffoldAbility extends RTFAbility implements Listener {

    private final static byte[] COLORS = {0, 4, 1, 14, 15};
    private final static double SECONDS = 2.0; //the double of this number must be an integer

    private final HyriRTF plugin;
    private final Map<UUID, Integer> states = new HashMap<>();

    public ScaffoldAbility(HyriRTF plugin) {
        super(RTFAbilityModel.SCAFFOLD, "scaffold", Material.CARPET, RTFAbilityType.MOVEMENT, 30);

        this.plugin = plugin;
        HyriRTF.get().getServer().getPluginManager().registerEvents(this, HyriRTF.get());
    }

    @Override
    public void use(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (20*SECONDS), 2, true, false));

        for (int i = 0; i < SECONDS *2 + 1; i++) {
            final int finalI = i;

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.states.put(player.getUniqueId(), finalI);
            }, finalI * 10L);
        }
    }

    private List<Location> getBlocksLocations(Location playerLocation) {
        final List<Location> carpetLocations = new ArrayList<>();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                final Location location = new Location(playerLocation.getWorld(),
                        playerLocation.clone().getX() + x,
                        playerLocation.clone().getY() - 1,
                        playerLocation.clone().getZ() + z);

                if (location.getBlock().getType() == Material.AIR) {
                    carpetLocations.add(location);
                }
            }
        }

        return carpetLocations;
    }

    private void spawnCarpets(List<Location> carpetsLoc, int state) {
        for (Location carpetLocation : carpetsLoc) {
            final Block block = carpetLocation.clone().getBlock();

            block.setType(Material.STAINED_GLASS);
            block.setData(COLORS[state]);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        if (!this.states.containsKey(uuid)) return;

        if (this.plugin.getGame().getPlayer(uuid).isDead() || this.plugin.getGame().getPlayer(uuid).isSpectator()) return;

        if (this.states.get(uuid) == SECONDS *2) {
            this.states.remove(uuid);
            return;
        }

        final List<Location> carpetsLoc = this.getBlocksLocations(event.getPlayer().getLocation());

        this.spawnCarpets(carpetsLoc, this.states.get(uuid));

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            for (Location carpetLocation : carpetsLoc) {
                final Block block = carpetLocation.clone().getBlock();
                block.setType(Material.AIR);
            }
        }, 20L);
    }
}
