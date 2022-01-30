package fr.hyriode.rtf.game.event.events;

import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.List;

public class EventFlagged extends Event {
    public EventFlagged(HyriRTF plugin, String eventName) {
        super(plugin, eventName);
    }

    @Override
    public void execute(Collection<HyriRTFGamePlayer> players) {
        this.isRunning = true;
        List<HyriRTFGamePlayer> alivePlayers = this.plugin.getGame().getPlayers();
        alivePlayers.removeAll(this.plugin.getGame().getDeadPlayers());

        byte data;

        for(HyriRTFGamePlayer player : alivePlayers) {
            PlayerInventory inventory = player.getPlayer().getInventory();
            if(player.getTeam().equals(this.plugin.getGame().getSecondTeam())) {
                data = this.plugin.getGame().getFirstTeam().getColor().getDyeColor().getData();
            }else {
                data = this.plugin.getGame().getSecondTeam().getColor().getDyeColor().getData();
            }

            player.getPlayer().setGameMode(GameMode.ADVENTURE);

            inventory.clear();

            for (int i = 0; i < 9; i++) {
                inventory.setItem(i, new ItemBuilder(Material.WOOL, 1, data).build());
            }

            inventory.setHelmet(new ItemBuilder(Material.WOOL, 1, data).build());
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            List<HyriRTFGamePlayer> alivePlayers1 = this.plugin.getGame().getPlayers();
            alivePlayers1.removeAll(this.plugin.getGame().getDeadPlayers());
            for(HyriRTFGamePlayer player : alivePlayers1) {
                if(!player.hasFlag()) {
                    player.spawn();
                }
            }
            isRunning = false;
        }, 30 * 20L);
    }
}
