package fr.hyriode.rtf.game.items;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.abilities.RTFAbility;

import java.util.Optional;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class RTFChooseAbilityGUI extends HyriInventory {

    public RTFChooseAbilityGUI(RTFGamePlayer owner) {

        super(owner.getPlayer(), HyriRTF.getLanguageManager().getValue(owner.getPlayer(), "ability.gui.name")
                .replace("%ability%", HyriRTF.getLanguageManager().getValue(owner.getPlayer(), owner.getAbility().getNameKey()))
                ,27);

        this.fillInventory(owner);
    }

    private void fillInventory(RTFGamePlayer gamePlayer) {
        for (HyriRTFAbilityModel value : HyriRTFAbilityModel.values()) {
            Optional<RTFAbility> oAbility = RTFAbility.getWithModel(value);

            oAbility.ifPresent(ability -> {
                if(gamePlayer.getAbility().equals(ability)) {
                    this.addItem(new ItemBuilder(ability.getIcon())
                            .withName(HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getNameKey()))
                            .withLore(
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getLoreKey()[0]),
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getLoreKey()[1]),
                                    " ",
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), "ability.gui.lore-chosen")
                            )
                            .withGlow()
                            .withAllItemFlags()
                            .build(), event -> event.setCancelled(true));
                } else {
                    this.addItem(new ItemBuilder(ability.getIcon())
                            .withName(HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getNameKey()))
                            .withLore(
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getLoreKey()[0]),
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), ability.getLoreKey()[1]),
                                    " ",
                                    HyriRTF.getLanguageManager().getValue(owner.getPlayer(), "ability.gui.lore-choose")
                            )
                            .withAllItemFlags()
                            .build(), event -> {
                        event.setCancelled(true);
                        gamePlayer.setAbility(ability);
                        gamePlayer.getAccount().setLastAbility(ability.getModel());
                        new RTFChooseAbilityGUI(gamePlayer).open();
                    });
                }
            });
        }
    }

}
