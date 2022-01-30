package fr.hyriode.rtf.game.event.events;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.TNTPrimed;

import java.util.Collection;
import java.util.List;

public class EventTNTRain extends Event {

    public EventTNTRain(HyriRTF plugin, String eventName) {
        super(plugin, eventName);
    }



    @Override
    public void execute(Collection<HyriRTFGamePlayer> players) {
        this.isRunning = true;
        List<HyriRTFGamePlayer> alivePlayers = this.plugin.getGame().getPlayers();
        alivePlayers.removeAll(this.plugin.getGame().getDeadPlayers());

        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            for(HyriRTFGamePlayer player : alivePlayers) {
                TNTPrimed tnt = player.getPlayer().getLocation().getWorld().spawn(player.getPlayer().getLocation().add(0, 8, 0), TNTPrimed.class);
                tnt.setFuseTicks(20 * 4);
            }
        }, 0L, 2 * 20L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Bukkit.getScheduler().cancelTask(task);
            this.isRunning = false;
        }, 10 * 20L);
    }
}
