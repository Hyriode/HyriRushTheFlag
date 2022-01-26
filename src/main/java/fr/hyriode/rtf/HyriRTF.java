package fr.hyriode.rtf;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.rtf.api.HyriRTFAPI;
import fr.hyriode.rtf.config.HyriRTFConfig;
import fr.hyriode.rtf.game.HyriRTFGame;
import fr.hyriode.rtf.game.events.Event;
import fr.hyriode.rtf.game.events.EventCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:01
 */
public class HyriRTF extends JavaPlugin {

    public static final String NAME = "RTF";
    public static final Supplier<World> WORLD = () -> Bukkit.getWorld("world");

    private static IHyriLanguageManager languageManager;

    private HyriRTFConfig configuration;
    private IHyrame hyrame;
    private HyriRTFAPI api;
    private HyriRTFGame game;
    private Event event;

    @Override
    public void onEnable() {
        final ChatColor color = ChatColor.RED;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color + "  ___         _  _____ _        ___ _           ");
        sender.sendMessage(color + " | _ \\_  _ __| ||_   _| |_  ___| __| |__ _ __ _ ");
        sender.sendMessage(color + " |   / || (_-< ' \\| | | ' \\/ -_) _|| / _` / _` |");
        sender.sendMessage(color + " |_|_\\\\_,_/__/_||_|_| |_||_\\___|_| |_\\__,_\\__, |");
        sender.sendMessage(color + "                                          |___/ ");

        log("Starting " + NAME + "...");

        this.configuration = new HyriRTFConfig(this);
        this.configuration.create();
        this.configuration.load();
        this.hyrame = HyrameLoader.load(new HyriRTFProvider(this));


        languageManager = this.hyrame.getLanguageManager();

        this.api = new HyriRTFAPI(HyriAPI.get().getRedisConnection().getPool());
        this.api.start();
        this.game = new HyriRTFGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(this.game);

        getCommand("event").setExecutor(new EventCommand(this));
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.RED + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(game);
        this.api.stop();
    }

    public HyriRTFConfig getConfiguration() {
        return this.configuration;
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }

    public HyriRTFAPI getAPI() {
        return this.api;
    }

    public HyriRTFGame getGame() {
        return this.game;
    }

}
