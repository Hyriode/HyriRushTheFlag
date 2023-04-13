package fr.hyriode.rtf.game.ability.buildAbility;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class BridgeAbility extends RTFAbility {

    private final HyriRTF plugin;

    public BridgeAbility(HyriRTF plugin) {
        super(RTFAbilityModel.BRIDGE,
                "bridge",
                Material.GLASS,
                RTFAbilityType.BUILD,
                3000,
                15);

        this.plugin = plugin;
    }

    @Override
    public void use(Player player) {
        final BlockIterator iterator = new BlockIterator(player, 15);

        int i = 0;
        while (i < 2) {
            iterator.next();
            i++;
        }

        int j = 0;
        while (iterator.hasNext()) {
            final Block blockBelow = this.getBlockBelow(iterator.next());

            if (blockBelow.getType() != Material.AIR) {
                continue;
            }

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                blockBelow.setType(Material.GLASS);
            }, j * 6L);

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                blockBelow.setType(Material.AIR);
            }, (j+20) * 6L);

            j++;
        }
    }

    private Block getBlockBelow(Block block) {
        return block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
    }
}
