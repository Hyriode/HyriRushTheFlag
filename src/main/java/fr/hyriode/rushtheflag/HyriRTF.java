package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rushtheflag.game.HyriRTFGame;
import fr.hyriode.rushtheflag.listeners.HyriDamageListener;
import fr.hyriode.rushtheflag.listeners.HyriPlayerPlaceBlockListener;
import fr.hyriode.rushtheflag.listeners.HyriVoidListener;
import fr.hyriode.rushtheflag.utils.HyriRTFconfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTF extends JavaPlugin {

    private Hyrame hyrame;
    private HyriAPI api;

    public void onEnable() {
        this.api = HyriAPI.get();
        this.hyrame = new Hyrame(new HyriRTFProvider(this));
        HyriRTFconfiguration hyriRTFconfiguration = new HyriRTFconfiguration(this);

        this.hyrame.getGameManager().registerGame(new HyriRTFGame(this));

        Bukkit.getServer().getPluginManager().registerEvents(new HyriVoidListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriDamageListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HyriPlayerPlaceBlockListener(this), this);
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
}
