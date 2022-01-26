package fr.hyriode.rtf.api.player;

import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.api.statistics.HyriRTFStatistics;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class HyriRTFPlayer {

    private final UUID uniqueId;
    private HyriRTFStatistics statistics;
    private HyriRTFHotBar hotBar;

    public HyriRTFPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.statistics = new HyriRTFStatistics();
        this.hotBar = new HyriRTFHotBar();
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public HyriRTFStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(HyriRTFStatistics statistics) {
        this.statistics = statistics;
    }

    public void setHotBar(HyriRTFHotBar hotBar) {
        this.hotBar = hotBar;
    }

    public HyriRTFHotBar getHotBar() {
        return this.hotBar;
    }

}