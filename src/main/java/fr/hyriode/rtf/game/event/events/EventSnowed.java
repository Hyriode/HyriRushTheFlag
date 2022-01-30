package fr.hyriode.rtf.game.event.events;

import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.List;

public class EventSnowed extends Event {

    public EventSnowed(HyriRTF plugin, String eventName) {
        super(plugin, eventName);
        this.isRunning = false;
    }

    @Override
    public void execute(Collection<HyriRTFGamePlayer> players) {
        this.isRunning = true;

        List<HyriRTFGamePlayer> alivePlayers1 = plugin.getGame().getPlayers();
        alivePlayers1.removeAll(plugin.getGame().getDeadPlayers());
        for(HyriRTFGamePlayer player : alivePlayers1) {
            if(!player.hasFlag()) {
                this.send(player);
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            isRunning = false;
            List<HyriRTFGamePlayer> alivePlayers11 = plugin.getGame().getPlayers();
            alivePlayers11.removeAll(plugin.getGame().getDeadPlayers());
            for(HyriRTFGamePlayer player : alivePlayers11) {
                if(!player.hasFlag()) {
                    player.spawn();
                }
            }
        }, 60*20L);
    }

    @Override
    public void send(Object o) {
        HyriRTFGamePlayer hyriRTFGamePlayer = (HyriRTFGamePlayer) o;
        hyriRTFGamePlayer.getPlayer().getInventory().clear();
        this.giveHotBar(hyriRTFGamePlayer);
        hyriRTFGamePlayer.giveArmor();
    }

    private void giveHotBar(HyriRTFGamePlayer hyriRTFGamePlayer) {
        final PlayerInventory inventory =   hyriRTFGamePlayer.getPlayer().getInventory();

        inventory.addItem(new ItemBuilder(Material.SNOW_BLOCK, 64 * 9, 2).build());
        inventory.setItem(hyriRTFGamePlayer.getAccount().getHotBar().getSlot(HyriRTFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, 16).build());
        inventory.setItem(hyriRTFGamePlayer.getAccount().getHotBar().getSlot(HyriRTFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(hyriRTFGamePlayer.getAccount().getHotBar().getSlot(HyriRTFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_SPADE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());
    }
}
