package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.commands.TestCommand;
import fr.hyriode.rushtheflag.game.HyriRTFFlag;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import fr.hyriode.rushtheflag.game.HyriRTFMethods;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTF extends JavaPlugin {

    private Hyrame hyrame;
    private HyriAPI api;
    private HyriRTFConfiguration hyriRTFconfiguration;
    private HyriRTFMethods hyriRTFMethods;
    private HyriRTFFlag blueFlag;
    private HyriRTFFlag redFlag;
    private HyriRTFGame game;

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = new Hyrame(new HyriRTFProvider(this));
        this.hyriRTFconfiguration = new HyriRTFConfiguration(this);
        this.hyriRTFMethods = new HyriRTFMethods(this);
        this.game = new HyriRTFGame(this);

        this.hyrame.getGameManager().registerGame(this.game);

        Bukkit.getServer().getPluginManager().registerEvents(new HyriRTFListener(this), this);

        getCommand("test").setExecutor(new TestCommand(this));
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

    public HyriRTFMethods getHyriRTFMethods() {
        return hyriRTFMethods;
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
}
