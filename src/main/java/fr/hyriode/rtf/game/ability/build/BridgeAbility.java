package fr.hyriode.rtf.game.ability.build;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;

import static fr.hyriode.rtf.listener.RTFWorldListener.SANDSTONE_METADATA_KEY;

public class BridgeAbility extends RTFAbility {

    private final HyriRTF plugin;

    public BridgeAbility(HyriRTF plugin) {
        super(RTFAbilityModel.BRIDGE,
                "bridge",
                Material.SANDSTONE,
                RTFAbilityType.BUILD,
                22);

        this.plugin = plugin;
    }

    @Override
    public void use(Player player) {
        final BlockIterator iterator = new BlockIterator(player.getLocation(), 0.0f, 15);

        int i = 0;
        while (i < 2) {
            iterator.next();
            i++;
        }

        int j = 0;
        while (iterator.hasNext()) {
            //Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                for (Block block : this.getBlocksBelow(iterator.next())) {
                    block.setType(Material.SANDSTONE);
                    block.setMetadata(SANDSTONE_METADATA_KEY, new FixedMetadataValue(this.plugin, System.currentTimeMillis()));
                }
            //}, j * 6L);

            j++;
        }
    }

    private List<Block> getBlocksBelow(Block block) {
        final List<Block> blocks = new ArrayList<>();

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                final Block belowBlock = block.getWorld().getBlockAt(block.getX() + x, block.getY() - 1, block.getZ() + z);

                if (belowBlock.getType() != Material.AIR) {
                    continue;
                }

                if (!this.plugin.getConfiguration().getArea().asArea().isInArea(belowBlock.getLocation())) {
                    continue;
                }

                if (this.plugin.getConfiguration().getFirstTeam().getSpawnArea().asArea().isInArea(belowBlock.getLocation())) {
                    continue;
                }

                if (this.plugin.getConfiguration().getSecondTeam().getSpawnArea().asArea().isInArea(belowBlock.getLocation())) {
                    continue;
                }

                blocks.add(belowBlock);
            }
        }

        return blocks;
    }

}
