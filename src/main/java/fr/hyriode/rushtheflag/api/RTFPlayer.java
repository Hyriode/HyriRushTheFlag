package fr.hyriode.rushtheflag.api;

import java.util.UUID;

public class RTFPlayer {

    private UUID uuid;
    private long kills;
    private long finalKills;
    private long deaths;
    private long woolsCaptured;
    private long woolsBroughtBack;
    private long victories;
    private long gamesPlayed;
    private long playTime;

    public RTFPlayer(UUID uuid, long kills, long finalKills, long deaths, long finalDeaths, long woolsCaptured, long woolsBroughtBack, long victories, long gamesPlayed, long playTime) {
        this.uuid = uuid;
        this.kills = kills;
        this.finalKills = finalKills;
        this.deaths = deaths;
        this.woolsCaptured = woolsCaptured;
        this.woolsBroughtBack = woolsBroughtBack;
        this.victories = victories;
        this.gamesPlayed = gamesPlayed;
        this.playTime = playTime;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getKills() {
        return this.kills;
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public long getWoolsCaptured() {
        return this.woolsCaptured;
    }

    public void setWoolsCaptured(long woolsCaptured) {
        this.woolsCaptured = woolsCaptured;
    }

    public long getWoolsBroughtBack() {
        return this.woolsBroughtBack;
    }

    public void setWoolsBroughtBack(long woolsBroughtBack) {
        this.woolsBroughtBack = woolsBroughtBack;
    }

    public long getVictories() {
        return this.victories;
    }

    public void setVictories(long victories) {
        this.victories = victories;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public long getFinalKills() {
        return finalKills;
    }

    public void setFinalKills(long finalKills) {
        this.finalKills = finalKills;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
}
