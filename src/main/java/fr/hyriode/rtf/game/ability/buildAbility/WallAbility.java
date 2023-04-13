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

public class WallAbility extends RTFAbility {

    private final HyriRTF plugin;

    public WallAbility(HyriRTF plugin) {
        super(RTFAbilityModel.WALL,
                "wall",
                Material.BRICK,
                RTFAbilityType.BUILD,
                3000,
                20);
        this.plugin = plugin;
    }

    @Override
    public void use(Player player) {
        final BlockIterator iterator = new BlockIterator(player, 4);

        int i = 0;
        while (i < 3) {
            iterator.next();
            i++;
        }

        this.createWallAround(iterator.next(), player);
    }

    private void createWallAround(Block block, Player player) {
        final int xSize = 9; //has to be an odd number
        final int ySize = 5; //has to be an odd number

        final double absoluteYaw = Math.sqrt(Math.pow(player.getLocation().getYaw(), 2)) % 180;

        boolean isZ = true;

        if (absoluteYaw < 45 || absoluteYaw > 135) {
            isZ  = false;
        }

        for (int y = -((ySize-1)/2); y <= ((ySize-1)/2); y++) {

            final int finalY = y;
            final boolean finalIsZ = isZ;

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (int x = -((xSize-1)/2); x < ((xSize-1)/2); x++) {

                    if (!finalIsZ) {
                        this.setWallBlock(block, x, finalY, 0);
                        continue;
                    }
                    this.setWallBlock(block, 0, finalY, x);
                }
            }, y  * 5L);

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (int x = -((xSize-1)/2); x < ((xSize-1)/2); x++) {

                    if (!finalIsZ) {
                        this.removeWallBlock(block, x, finalY, 0);
                        continue;
                    }

                    this.removeWallBlock(block, 0, finalY, x);
                }
            }, (y+60) * 5L);
        }
    }

    private void setWallBlock(Block baseBlock, int x, int y, int z) {
        final Block relativeBlock = this.getRelativeBlock(baseBlock, x, y, z);

        if (relativeBlock.getType() == Material.AIR) {
            relativeBlock.setType(Material.BRICK);
        }
    }

    private void removeWallBlock(Block baseBlock, int x, int y, int z) {
        final Block relativeBlock = this.getRelativeBlock(baseBlock, x, y, z);

        if (relativeBlock.getType() == Material.BRICK) {
            relativeBlock.setType(Material.AIR);
        }
    }

    private Block getRelativeBlock(Block block, int x, int y, int z) {
        return block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
    }
}
