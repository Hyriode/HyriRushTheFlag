package fr.hyriode.rtf.game.event.events;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.Event;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.Collection;

import static fr.hyriode.rtf.listener.WorldListener.SANDSTONE_METADATA_KEY;

public class EventShadowBlocks extends Event {

    private int dimension;

    public EventShadowBlocks(HyriRTF plugin, String eventName) {
        super(plugin, eventName);
    }

    @Override
    public void execute(Collection<HyriRTFGamePlayer> players) {
        this.isRunning = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> isRunning = false, 30 * 20L);
        this.plugin.getGame().sendMessageToAll(target -> HyriRTF.getLanguageManager().getValue(target, "message.event-shadow-blocks"));
    }

    public void send(Object object) {
        Block block = (Block) object;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if(block.hasMetadata(SANDSTONE_METADATA_KEY)) {
                block.setType(Material.AIR);
            }
        }, 3*20L);

        for(int i=0;i < 10;i++) {
            int i1 = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                if(block.hasMetadata(SANDSTONE_METADATA_KEY)) {
                    this.sendBlockBreak(block,i1);
                }
            }, 6L*i);
        }
    }

    private void sendBlockBreak(Block block,int damage) {
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), damage);
        int dimension = ((CraftWorld) block.getWorld()).getHandle().dimension;
        ((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(block.getX(), block.getY(), block.getZ(), 120, dimension, packet);
    }
}
