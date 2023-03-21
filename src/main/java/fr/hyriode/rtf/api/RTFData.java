package fr.hyriode.rtf.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import org.bson.Document;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:18
 */
public class RTFData implements IHyriPlayerData {

    private RTFHotBar hotBar = new RTFHotBar();
    private RTFAbilityModel lastAbility = RTFAbilityModel.RUNNER;

    @Override
    public void save(MongoDocument document) {
        document.append("hotBar", MongoSerializer.serialize(this.hotBar));
        document.append("lastAbility", this.lastAbility.name());
    }

    @Override
    public void load(MongoDocument document) {
        this.hotBar.load(new MongoDocument(document.get("hotBar", Document.class)));

        RTFAbilityModel.getByName(document.getString("lastAbility")).ifPresent(ability -> this.lastAbility = ability);
    }

    public RTFHotBar getHotBar() {
        return this.hotBar == null ? this.hotBar = new RTFHotBar() : this.hotBar;
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

    public static RTFData get(UUID uuid) {
        RTFData data = IHyriPlayer.get(uuid).getData().read("rushtheflag", new RTFData());

        if (data == null) {
            data = new RTFData();
        }
        return data;
    }

    public void update(UUID player) {
        final IHyriPlayer account = IHyriPlayer.get(player);

        account.getData().add("rushtheflag", this);
        account.update();
    }

}
