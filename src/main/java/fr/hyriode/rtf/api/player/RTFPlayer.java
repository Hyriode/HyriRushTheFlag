package fr.hyriode.rtf.api.player;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.rtf.api.ability.RTFAbilityModel;
import fr.hyriode.rtf.api.hotbar.RTFHotBar;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class RTFPlayer extends HyriPlayerData {

    private final UUID uniqueId;
    private RTFHotBar hotBar;

    private RTFAbilityModel lastAbility;

    public RTFPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.hotBar = new RTFHotBar();
        this.lastAbility = RTFAbilityModel.RUNNER;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public RTFHotBar getHotBar() {
        if (this.hotBar == null) {
            return this.hotBar = new RTFHotBar();
        }
        return this.hotBar;
    }

    public RTFAbilityModel getLastAbility() {
        if (this.lastAbility == null) {
            return this.lastAbility = RTFAbilityModel.RUNNER;
        }
        return this.lastAbility;
    }

    public void setLastAbility(RTFAbilityModel lastAbility) {
        this.lastAbility = lastAbility;
    }

    public static RTFPlayer getPlayer(UUID uuid) {
        final RTFPlayer rtfPlayer = IHyriPlayer.get(uuid).getData("rushtheflag", RTFPlayer.class);

        return rtfPlayer == null ? new RTFPlayer(uuid) : rtfPlayer;
    }

    public static void updatePlayer(RTFPlayer player) {
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());

        account.addData("rushtheflag", player);
        account.update();
    }

}
