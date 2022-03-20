package fr.hyriode.rtf.game.abilities;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 20/03/2022 at 10:32
 */
public class BuilderAbility {

    private final HyriRTF plugin;

    public BuilderAbility(HyriRTF pl) {
      /*  super(HyriRTFAbilityModel.BUILDER,
                "ability.builder.name",
                new String[]{"ability.builder.lore.1", "ability.builder.lore.2"},
                Material.SANDSTONE,
                25);

       abilityMap.put(BuilderAbility.class, this);
       */
        this.plugin = pl;
    }

  /*  @Override
    public void use(Player player) {
        final World world = player.getWorld();
        final Vector location = player.getLocation().toVector();
        final Vector direction = player.getEyeLocation().getDirection();
        final double yOffset = 2;
        final int maxDistance = 18;

        BlockIterator iterator = new BlockIterator(world, location, direction, yOffset, maxDistance);
        while(iterator.hasNext()) {
            if ((this.plugin.getGame().getFirstTeam().isInBase(iterator.next().getLocation())) || (this.plugin.getGame().getSecondTeam().isInBase(iterator.next().getLocation()))) {
                continue;
            }
            if(iterator.next().getType().equals(Material.AIR)) {
                iterator.forEachRemaining(b -> {
                    Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            b.setType(Material.SANDSTONE);
                            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 3f, 3f);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> b.setType(Material.AIR), 20 * 5);
                        }
                    }, 10);
                });
            }
        }

    }

   */
}
