package fr.hyriode.rtf.game.items;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.HyriRTFAPI;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Optional;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class AbilityChooseGui extends HyriInventory {

    public AbilityChooseGui(HyriRTFGamePlayer owner) {

        super(owner.getPlayer(), HyriRTF.getLanguageManager().getValue(owner.getPlayer(), "ability.gui.name")
                .replace("%ability%", HyriRTF.getLanguageManager().getValue(owner.getPlayer(), owner.getAbility().getNameKey()))
                ,27);

        this.fillInventory(owner);
    }

    private void fillInventory(HyriRTFGamePlayer gamePlayer) {
        for (HyriRTFAbilityModel value : HyriRTFAbilityModel.values()) {
            Optional<Ability> oAbility = Ability.getWithModel(value);

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
                            .build(), event -> {
                        event.setCancelled(true);
                    });
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
                        gamePlayer.getPlayer().closeInventory();
                    });
                }
            });
        }
    }

}
