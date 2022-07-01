package fr.hyriode.rtf.api.player;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.rtf.api.ability.HyriRTFAbilityModel;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class HyriRTFPlayer extends HyriPlayerData {

    private final UUID uniqueId;
    private HyriRTFHotBar hotBar;

    private HyriRTFAbilityModel lastAbility;

    public HyriRTFPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.hotBar = new HyriRTFHotBar();
        this.lastAbility = HyriRTFAbilityModel.RUNNER;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public HyriRTFHotBar getHotBar() {
        if (this.hotBar == null) {
            return this.hotBar = new HyriRTFHotBar();
        }
        return this.hotBar;
    }

    public HyriRTFAbilityModel getLastAbility() {
        if (this.lastAbility == null) {
            return this.lastAbility = HyriRTFAbilityModel.RUNNER;
        }
        return this.lastAbility;
    }

    public void setLastAbility(HyriRTFAbilityModel lastAbility) {
        this.lastAbility = lastAbility;
    }

}
