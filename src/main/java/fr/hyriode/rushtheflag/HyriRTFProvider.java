package fr.hyriode.rushtheflag;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRTFProvider implements IPluginProvider {

    private final JavaPlugin plugin;

    public HyriRTFProvider(JavaPlugin plugin) {
       this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[] {"fr.hyriode.rushtheflag"};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[] {"fr.hyriode.rushtheflag"};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }
}
