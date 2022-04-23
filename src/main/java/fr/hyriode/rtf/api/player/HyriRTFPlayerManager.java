package fr.hyriode.rtf.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.rtf.api.HyriRTFAPI;

import java.util.UUID;
import java.util.function.Function;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:27
 */
public class HyriRTFPlayerManager {

    private final HyriRTFAPI api;

    public HyriRTFPlayerManager(HyriRTFAPI api) {
        this.api = api;
    }

    public HyriRTFPlayer getPlayer(UUID uuid) {
        IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        return hyriPlayer.getData("rushtheflag", HyriRTFPlayer.class);
    }

    public void sendPlayer(HyriRTFPlayer player) {
        final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

        hyriPlayer.addData("rushtheflag", player);
        HyriAPI.get().getPlayerManager().sendPlayer(hyriPlayer);
    }

}
