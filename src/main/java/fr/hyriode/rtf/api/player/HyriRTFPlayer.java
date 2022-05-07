package fr.hyriode.rtf.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.api.statistics.HyriRTFStatistics;

import java.util.Arrays;
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
        this.lastAbility = HyriRTFAbilityModel.ASTRONAUT;
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
            return this.lastAbility = HyriRTFAbilityModel.ASTRONAUT;
        }
        return this.lastAbility;
    }

    public void setLastAbility(HyriRTFAbilityModel lastAbility) {
        this.lastAbility = lastAbility;
    }

}
