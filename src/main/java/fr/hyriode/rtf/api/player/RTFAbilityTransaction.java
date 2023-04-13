package fr.hyriode.rtf.api.player;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.model.IHyriTransactionContent;
import fr.hyriode.rtf.api.ability.RTFAbilityModel;

public class RTFAbilityTransaction implements IHyriTransactionContent {

    public static final String TYPE = "ability";

    private RTFAbilityModel ability;

    @Override
    public void save(MongoDocument document) {
        document.append("ability", this.ability.name());
    }

    @Override
    public void load(MongoDocument document) {
        ability = RTFAbilityModel.valueOf(document.getString("ability"));
    }
}
