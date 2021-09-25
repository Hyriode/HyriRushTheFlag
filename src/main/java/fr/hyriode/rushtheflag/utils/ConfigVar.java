package fr.hyriode.rushtheflag.utils;

import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ConfigVar {

    private final HyriRTF hyriRTF;
    private World world;

    public ConfigVar(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    public static Location blueFlagLocation;
    public static Location blueSpawnLocation;
    public static Location redFlagLocation;
    public static Location redSpawnLocation;

    public static Location blueStartSpawnProtect;
    public static Location blueEndSpawnProtect;

    public static Location blueStartFlagProtect;
    public static Location blueEndFLagProtect;

    public static Location redStartSpawnProtect;
    public static Location redEndSpawnProtect;

    public static Location redStartFLagProtect;
    public static Location redEndFlagProtect;

    public void init() {

        final File file = new File(hyriRTF.getDataFolder(), "HyriRTF/config.yml");
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        List<Field> fields = Arrays.asList(ConfigVar.class.getDeclaredFields());

        if (!file.exists()) {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    configuration.set(field.getName() + ".x", 0);
                    configuration.set(field.getName() + ".y", 0);
                    configuration.set(field.getName() + ".z", 0);
                }else {
                    fields.remove(field);
                }
            }

            try {
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                final ConfigurationSection configurationSection = configuration.getConfigurationSection(field.getName());
                System.out.println("load : " + field.getName());
                System.out.println("result : x: " + configurationSection.getDouble(".x") + " y: " + configurationSection.getDouble(".y") + " z: " + configurationSection.getDouble(".z"));

                try {
                    field.set(field, new Location(world, configurationSection.getDouble(".x"), configurationSection.getDouble(".y"), configurationSection.getDouble(".z")));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
