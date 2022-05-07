package fr.hyriode.rtf.game.items;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.rtf.HyriRTF;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class RTFChooseAbilityItem extends HyriItem<HyriRTF> {

    public RTFChooseAbilityItem(HyriRTF plugin) {
        super(plugin, "ability_choose", () -> HyriRTF.getLanguageManager().getMessage("item.choose.name"), Material.NAME_TAG);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        event.setCancelled(true);
        new RTFChooseAbilityGUI(this.plugin.getGame().getPlayer(event.getPlayer().getUniqueId())).open();
    }

}
