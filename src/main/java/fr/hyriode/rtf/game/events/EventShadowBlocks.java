package fr.hyriode.rtf.game.events;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collection;

import static fr.hyriode.rtf.listener.WorldListener.SANDSTONE_METADATA_KEY;

public class EventShadowBlocks extends Event{

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
    }
}
