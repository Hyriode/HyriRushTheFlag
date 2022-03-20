package fr.hyriode.rtf.game.items;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.rtf.HyriRTF;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class AbilityChooseItem extends HyriItem<HyriRTF> {
    public AbilityChooseItem(HyriRTF plugin) {
        super(plugin, "ability_choose", () -> HyriRTF.getLanguageManager().getMessage("item.choose.name"), Material.NAME_TAG);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        event.setCancelled(true);
        new AbilityChooseGui(this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId())).open();
    }
}
