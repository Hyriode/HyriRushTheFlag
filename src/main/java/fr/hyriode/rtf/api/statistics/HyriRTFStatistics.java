package fr.hyriode.rtf.api.statistics;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class HyriRTFStatistics {

    private long kills;
    private long finalKills;
    private long deaths;
    private long victories;
    private long capturedFlags;
    private long flagsBroughtBack;
    private long gamesPlayed;
    private long playedTime;

    public long getKills() {
        return this.kills;
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public void addKills(long kills) {
        this.kills += kills;
    }

    public void removeKills(int kills) {
        this.kills -= kills;
    }

    public long getFinalKills() {
        return this.finalKills;
    }

    public void setFinalKills(long finalKills) {
        this.finalKills = finalKills;
    }

    public void addFinalKills(long finalKills) {
        this.finalKills += finalKills;
    }

    public void removeFinalKills(long finalKills) {
        this.finalKills -= finalKills;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public void addDeaths(long deaths) {
        this.deaths += deaths;
    }

    public void removeDeaths(long deaths) {
        this.deaths -= deaths;
    }

    public long getVictories() {
        return this.victories;
    }

    public void setVictories(long victories) {
        this.victories = victories;
    }

    public void addVictories(long victories) {
        this.victories += victories;
    }

    public void removeVictories(long victories) {
        this.victories -= victories;
    }

    public long getDefeats() {
        return this.gamesPlayed - this.victories;
    }

    public long getCapturedFlags() {
        return this.capturedFlags;
    }

    public void setCapturedFlags(long capturedFlags) {
        this.capturedFlags = capturedFlags;
    }

    public void addCapturedFlags(long capturedFlags) {
        this.capturedFlags += capturedFlags;
    }

    public void removeCapturedFlags(long capturedFlags) {
        this.capturedFlags -= capturedFlags;
    }

    public long getFlagsBroughtBack() {
        return this.flagsBroughtBack;
    }

    public void setFlagsBroughtBack(long flagsBroughtBack) {
        this.flagsBroughtBack = flagsBroughtBack;
    }

    public void addFlagsBroughtBack(long flagsBroughtBack) {
        this.flagsBroughtBack += flagsBroughtBack;
    }

    public void removeFlagsBroughtBack(long flagsBroughtBack) {
        this.flagsBroughtBack -= flagsBroughtBack;
    }

    public long getGamesPlayed() {
        return this.gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void addGamesPlayed(int gamesPlayed) {
        this.gamesPlayed += gamesPlayed;
    }

    public void removeGamesPlayed(int gamesPlayed) {
        this.gamesPlayed -= gamesPlayed;
    }

    public long getPlayedTime() {
        return this.playedTime;
    }

    public void setPlayedTime(long playedTime) {
        this.playedTime = playedTime;
    }

}
