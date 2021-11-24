package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.api.RTFAPI;
import fr.hyriode.rushtheflag.api.RTFPlayerManager;
import fr.hyriode.rushtheflag.game.HyriRTFFlag;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import fr.hyriode.rushtheflag.game.HyriRTFTeams;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTF extends JavaPlugin {

    private IHyrame hyrame;
    private HyriAPI api;
    private HyriRTFConfiguration configuration;
    private HyriRTFFlag blueFlag;
    private HyriRTFFlag redFlag;
    private HyriRTFGame game;
    private final RTFAPI rtfapi = new RTFAPI(HyriAPI.get().getRedisConnection().getPool());
    private final RTFPlayerManager rtfPlayerManager = new RTFPlayerManager(rtfapi);

    public static final String RTF = "RushTheFlag";

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = HyrameLoader.load(new HyriRTFProvider(this));
        this.configuration = new HyriRTFConfiguration(this);
        this.game = new HyriRTFGame(this.hyrame, this);

        this.blueFlag = new HyriRTFFlag(this, this.getGame().getTeam(HyriRTFTeams.BLUE.getTeamName()));
        this.redFlag = new HyriRTFFlag(this, this.getGame().getTeam(HyriRTFTeams.RED.getTeamName()));

        this.hyrame.getGameManager().registerGame(this.game);
    }

    public void onDisable() {
        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public HyriAPI getAPI() {
        return this.api;
    }

    public HyriRTFConfiguration getConfiguration() {
        return configuration;
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

    public RTFPlayerManager getRtfPlayerManager() {
        return rtfPlayerManager;
    }
}
