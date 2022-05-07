package fr.hyriode.rtf.api.statistics;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.rtf.api.HyriRTFAPI;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:27
 */
public class HyriRTFStatisticsManager {

    public HyriRTFStatistics getStatistics(UUID uuid) {
        return HyriAPI.get().getPlayerManager().getPlayer(uuid).getStatistics("rushtheflag", HyriRTFStatistics.class);
    }

    public void sensStatistics(UUID uuid, HyriRTFStatistics statistics) {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        account.addStatistics("rushtheflag", statistics);
        account.update();
    }

}
