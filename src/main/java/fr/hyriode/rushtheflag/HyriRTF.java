package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTF extends JavaPlugin {

    private Hyrame hyrame;
    private HyriAPI api;

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = new Hyrame(new HyriRTFProvider(this));

        this.hyrame.getGameManager().registerGame(new HyriRTFGame(this));
    }

    public void onDisable() {
        this.hyrame.getGameManager().unregisterGame();
    }


    public Hyrame getHyrame() {
        return this.hyrame;
    }

    public HyriAPI getAPI() {
        return this.api;
    }
}
