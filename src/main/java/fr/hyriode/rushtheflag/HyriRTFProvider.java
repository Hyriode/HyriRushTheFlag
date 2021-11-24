package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class HyriRTFProvider implements IPluginProvider {

    private static String PACKAGE = "fr.hyriode.rushtheflag";

    private final JavaPlugin plugin;

    public HyriRTFProvider(JavaPlugin plugin) {
       this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getId() {
        return "rushtheflag";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

    @Override
    public Logger getLogger() {
        return IPluginProvider.super.getLogger();
    }
}
