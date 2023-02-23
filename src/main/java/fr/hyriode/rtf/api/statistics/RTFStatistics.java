package fr.hyriode.rtf.api.statistics;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.rtf.game.RTFGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class RTFStatistics implements IHyriStatistics {

    private final Map<RTFGameType, Data> data;

    public RTFStatistics() {
        this.data = new HashMap<>();
    }

    public Map<RTFGameType, Data> getData() {
        return this.data;
    }

    public Data getData(RTFGameType gameType) {
        Data data = this.data.get(gameType);

        if (data == null) {
            data = new Data();
            this.data.put(gameType, data);
        }

        return data;
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add("rushtheflag", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player));
    }

    public static RTFStatistics get(IHyriPlayer account) {
        RTFStatistics statistics = account.getStatistics().get("rushtheflag");

        if (statistics == null) {
            statistics = new RTFStatistics();
            statistics.update(account);
        }

        return statistics;
    }

    public static RTFStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<RTFGameType, Data> entry : this.data.entrySet()) {
            final Document dataDocument = new Document();

            entry.getValue().save(MongoDocument.of(dataDocument));

            document.append(entry.getKey().name(), dataDocument);
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final Data data = new Data();

            data.load(MongoDocument.of((Document) entry.getValue()));

            this.data.put(RTFGameType.valueOf(entry.getKey()), data);
        }
    }


    public static class Data implements MongoSerializable {
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

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("final_kills", this.finalKills);
            document.append("deaths", this.deaths);
            document.append("victories", this.victories);
            document.append("captured_flags", this.capturedFlags);
            document.append("flags_brought_back", this.flagsBroughtBack);
            document.append("games_played", this.gamesPlayed);
            document.append("played_time", this.playedTime);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.finalKills = document.getLong("final_kills");
            this.deaths = document.getLong("deaths");
            this.victories = document.getLong("victories");
            this.capturedFlags = document.getLong("captured_flags");
            this.flagsBroughtBack = document.getLong("flags_brought_back");
            this.gamesPlayed = document.getLong("games_played");
            this.playedTime = document.getLong("played_time");
        }
    }

}
