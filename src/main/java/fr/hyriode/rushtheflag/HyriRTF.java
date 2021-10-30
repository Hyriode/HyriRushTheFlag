package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.game.HyriRTFFlag;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import fr.hyriode.rushtheflag.game.Teams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTF extends JavaPlugin {

    private Hyrame hyrame;
    private HyriAPI api;
    private HyriRTFConfiguration hyriRTFconfiguration;
    private HyriRTFFlag blueFlag;
    private HyriRTFFlag redFlag;
    private HyriRTFGame game;
    private final String RTF = "RushTheFlag";

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = new Hyrame(new HyriRTFProvider(this));
        this.hyriRTFconfiguration = new HyriRTFConfiguration(this);
        this.game = new HyriRTFGame(this);

        this.blueFlag = new HyriRTFFlag(this, this.getGame().getTeam(Teams.BLUE.getTeamName()));
        this.redFlag = new HyriRTFFlag(this, this.getGame().getTeam(Teams.RED.getTeamName()));

        this.hyrame.getGameManager().registerGame(this.game);
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

    public HyriRTFConfiguration getHyriRTFconfiguration() {
        return hyriRTFconfiguration;
    }

    public HyriRTFFlag getBlueFlag() {
        return blueFlag;
    }

    public void setBlueFlag(HyriRTFFlag blueFlag) {
        this.blueFlag = blueFlag;
    }

    public HyriRTFFlag getRedFlag() {
        return redFlag;
    }

    public void setRedFlag(HyriRTFFlag redFlag) {
        this.redFlag = redFlag;
    }

    public HyriRTFGame getGame() {
        return game;
    }

    public String getRTF() {
        return RTF;
    }
}
