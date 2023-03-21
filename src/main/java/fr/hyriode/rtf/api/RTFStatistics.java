package fr.hyriode.rtf.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
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

    private final Map<RTFGameType, Data> dataMap = new HashMap<>();

    public Map<RTFGameType, Data> getData() {
        return this.dataMap;
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<RTFGameType, Data> entry : this.dataMap.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.dataMap.put(RTFGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(RTFGameType gameType) {
        return this.dataMap.merge(gameType, new Data(), (oldValue, newValue) -> oldValue);
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add("rushtheflag", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(IHyriPlayer.get(player));
    }

    public static RTFStatistics get(IHyriPlayer account) {
        RTFStatistics statistics = account.getStatistics().read("rushtheflag", new RTFStatistics());

        if (statistics == null) {
            statistics = new RTFStatistics();
        }
        return statistics;
    }

    public static RTFStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long finalKills;
        private long deaths;
        private long victories;
        private long gamesPlayed;
        private long capturedFlags;
        private long flagsBroughtBack;

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("finalKills", this.finalKills);
            document.append("deaths", this.deaths);
            document.append("victories", this.victories);
            document.append("gamesPlayed", this.gamesPlayed);
            document.append("capturedFlags", this.capturedFlags);
            document.append("flagsBroughtBack", this.flagsBroughtBack);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.finalKills = document.getLong("finalKills");
            this.deaths = document.getLong("deaths");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("gamesPlayed");
            this.capturedFlags = document.getLong("capturedFlags");
            this.flagsBroughtBack = document.getLong("flagsBroughtBack");
        }

        public long getKills() {
            return this.kills;
        }

        public void setKills(long kills) {
            this.kills = kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
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

        public long getDeaths() {
            return this.deaths;
        }

        public void setDeaths(long deaths) {
            this.deaths = deaths;
        }

        public void addDeaths(long deaths) {
            this.deaths += deaths;
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

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void setGamesPlayed(long gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public void addGamesPlayed(int gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
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

        public long getFlagsBroughtBack() {
            return this.flagsBroughtBack;
        }

        public void setFlagsBroughtBack(long flagsBroughtBack) {
            this.flagsBroughtBack = flagsBroughtBack;
        }

        public void addFlagsBroughtBack(long flagsBroughtBack) {
            this.flagsBroughtBack += flagsBroughtBack;
        }

    }

}
