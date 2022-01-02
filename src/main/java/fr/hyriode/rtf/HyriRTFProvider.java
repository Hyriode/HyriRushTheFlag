package fr.hyriode.rtf;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:01
 */
public class HyriRTFProvider implements IPluginProvider {

    private static final String PACKAGE = "fr.hyriode.rtf";

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
        return "rtf";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
