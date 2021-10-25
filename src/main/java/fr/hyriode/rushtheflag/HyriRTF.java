package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.commands.TestCommand;
import fr.hyriode.rushtheflag.game.HyriRTFFlag;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import fr.hyriode.rushtheflag.game.HyriRTFMethods;
import fr.hyriode.rushtheflag.listeners.*;
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
    private final HyriRTFGame game = new HyriRTFGame(this);

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = new Hyrame(new HyriRTFProvider(this));
        this.hyriRTFconfiguration = new HyriRTFConfiguration(this);
        this.hyriRTFMethods = new HyriRTFMethods(this);

        this.hyrame.getGameManager().registerGame(new HyriRTFGame(this));

        /*Bukkit.getServer().getPluginManager().registerEvents(new HyriVoidListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriDamageListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriPlayerPlaceBlockListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriPlayerBreakBlockListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriPlayerMoveListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriClickInventoryListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriFoodLevelChangeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriPlayerDropEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriEntitySpawnListener(), this);*/

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
