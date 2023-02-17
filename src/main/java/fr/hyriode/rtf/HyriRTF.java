package fr.hyriode.rtf;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.rtf.config.RTFConfig;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.host.category.RTFHostMainCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:01
 */
public class HyriRTF extends JavaPlugin {

    public static final String NAME = "RTF";

    private static HyriRTF instance;

    private RTFConfig configuration;
    private IHyrame hyrame;
    private RTFGame game;

    @Override
    public void onEnable() {
        instance = this;

        final ChatColor color = ChatColor.RED;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color + "  ___         _  _____ _        ___ _           ");
        sender.sendMessage(color + " | _ \\_  _ __| ||_   _| |_  ___| __| |__ _ __ _ ");
        sender.sendMessage(color + " |   / || (_-< ' \\| | | ' \\/ -_) _|| / _` / _` |");
        sender.sendMessage(color + " |_|_\\\\_,_/__/_||_|_| |_||_\\___|_| |_\\__,_\\__, |");
        sender.sendMessage(color + "                                          |___/ ");

        log("Starting " + NAME + "...");

        /*final RTFConfig.GameArea spawnArea = new RTFConfig.GameArea(new LocationWrapper(IHyrame.WORLD.get().getUID(), 23, 201, 19), new LocationWrapper(IHyrame.WORLD.get().getUID(), -24, 157, -24));
        final RTFConfig config = new RTFConfig(// Spawn (en haut de la carte)
                // Spawn (en haut de la carte)
                new LocationWrapper(IHyrame.WORLD.get().getUID(), 0.5, 170, 0.5, -90, 0),

                spawnArea,
                // Pos1 et Pos2 comme world edit pour définir les limites de la map
                new RTFConfig.GameArea(new LocationWrapper(IHyrame.WORLD.get().getUID(), -68, 139, 18), new LocationWrapper(IHyrame.WORLD.get().getUID(), 68, 89, -21)),

                // Les teams: 4 valeurs -> 1) Spawn // 2) Pos1 de la base // 3) Pos2 de la base // 4) Position du block du flag
                new RTFConfig.Team(new LocationWrapper(IHyrame.WORLD.get().getUID(), -46.5, 100, 0.5, -90, 0), new LocationWrapper(IHyrame.WORLD.get().getUID(), -44, 100, 10), new LocationWrapper(IHyrame.WORLD.get().getUID(), -64, 114, -10), new LocationWrapper(IHyrame.WORLD.get().getUID(), -51, 101, 0)),
                new RTFConfig.Team(new LocationWrapper(IHyrame.WORLD.get().getUID(), 47.5, 100, 0.5, 90, 0), new LocationWrapper(IHyrame.WORLD.get().getUID(), 44, 100, -10), new LocationWrapper(IHyrame.WORLD.get().getUID(), 64, 115, 10), new LocationWrapper(IHyrame.WORLD.get().getUID(), 51, 101, 0)));

        HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(config, "rushtheflag", RTFGameType.EVENT.getName(), "Pokémon");
        HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "rushtheflag", RTFGameType.EVENT.getName(), "Pokémon");*/

        this.hyrame = HyrameLoader.load(new HyriRTFProvider(this));

        this.configuration = HyriAPI.get().getConfig().isDevEnvironment() ?
                new RTFConfig(
                        new RTFConfig.GameArea(
                                new LocationWrapper(-40.0f, 90.0f, -20.0f),
                                new LocationWrapper(85.0f, 125.0f, 25.0f)),
                        new RTFConfig.Team(
                                new LocationWrapper(65.0f, 100.0f, 9.0f),
                                new LocationWrapper(74.0f, 98.0f, 14.0f),
                                new LocationWrapper(61.0f, 107.0f, -3.0f),
                                Arrays.asList(
                                        new LocationWrapper(65.0f, 100.0f, 1.0f),
                                        new LocationWrapper(65.0f, 100.0f, 4.0f))),
                        new RTFConfig.Team(
                                new LocationWrapper(-28.0f, 100.0f, 1.0f),
                                new LocationWrapper(-37.0f, 98.0f, -3.0f),
                                new LocationWrapper(-24.0f, 110.0f, 14.0f),
                                Arrays.asList(
                                        new LocationWrapper(-28.0f, 102.0f, 6.0f),
                                        new LocationWrapper(-28.0f, 100.0f, 9.0f)))) :
                HyriAPI.get().getServer().getConfig(RTFConfig.class);

        this.game = new RTFGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        RTFAbility.init(this);

        if (HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            this.hyrame.getHostController().addCategory(25, new RTFHostMainCategory());
        }

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
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
    }

    public static HyriRTF get() {
        return instance;
    }

    public RTFConfig getConfiguration() {
        return this.configuration;
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public RTFGame getGame() {
        return this.game;
    }

}
