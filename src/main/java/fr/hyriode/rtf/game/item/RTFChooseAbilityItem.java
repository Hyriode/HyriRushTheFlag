package fr.hyriode.rtf.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.gui.RTFChooseAbilityGUI;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class RTFChooseAbilityItem extends HyriItem<HyriRTF> {

    public RTFChooseAbilityItem(HyriRTF plugin) {
        super(plugin, "ability_choose", () -> HyriLanguageMessage.get("item.choose.name"), () -> HyriLanguageMessage.get("item.choose.lore"), Material.NETHER_STAR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());

        event.setCancelled(true);

        new RTFChooseAbilityGUI(gamePlayer, RTFAbility.getWithModel(gamePlayer.getData().getLastAbility()).get().getType()).open();
    }

}
