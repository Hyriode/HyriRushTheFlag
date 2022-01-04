package fr.hyriode.rtf.config;

import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.rtf.HyriRTF;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

import static fr.hyriode.hyrame.configuration.HyriConfigurationEntry.IntegerEntry;
import static fr.hyriode.hyrame.configuration.HyriConfigurationEntry.LocationEntry;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 23:00
 */
public class HyriRTFConfig implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(HyriRTF.WORLD.get(), 0, 0, 0, 0, 0);

    private Location spawn;
    private final LocationEntry spawnEntry;

    private Location gameAreaFirst;
    private final LocationEntry gameAreaFirstEntry;
    private Location gameAreaSecond;
    private final LocationEntry gameAreaSecondEntry;

    private int teamLives;
    private IntegerEntry teamLivesEntry;

    private final Team firstTeam;
    private final Team secondTeam;

    private final FileConfiguration config;
    private final JavaPlugin plugin;

    public HyriRTFConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.spawn = DEFAULT_LOCATION.get();
        this.spawnEntry = new LocationEntry("spawn", this.config);

        final String areaKey = "game-area.";

        this.gameAreaFirst = DEFAULT_LOCATION.get();
        this.gameAreaFirstEntry = new LocationEntry(areaKey + "first", this.config);
        this.gameAreaSecond = DEFAULT_LOCATION.get();
        this.gameAreaSecondEntry = new LocationEntry(areaKey + "second", this.config);

        this.teamLives = 3;
        //this.teamLivesEntry = new IntegerEntry("team-lives", this.config);

        this.firstTeam = new Team("firstTeam");
        this.secondTeam = new Team("secondTeam");
    }

    @Override
    public void create() {
        this.spawnEntry.setDefault(this.spawn);

        this.gameAreaFirstEntry.setDefault(this.gameAreaFirst);
        this.gameAreaSecondEntry.setDefault(this.gameAreaSecond);

        //this.teamLivesEntry.setDefault(this.teamLives);

        this.firstTeam.create();
        this.secondTeam.create();

        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        HyriRTF.log("Loading configuration...");

        this.spawn = this.spawnEntry.get();

        this.gameAreaFirst = this.gameAreaFirstEntry.get();
        this.gameAreaSecond = this.gameAreaSecondEntry.get();

        //this.teamLives = this.teamLivesEntry.get();

        this.firstTeam.load();
        this.secondTeam.load();
    }

    @Override
    public void save() {
        HyriRTF.log("Saving configuration...");

        this.spawnEntry.set(this.spawn);

        this.gameAreaFirstEntry.set(this.gameAreaFirst);
        this.gameAreaSecondEntry.set(this.gameAreaSecond);

        this.teamLivesEntry.set(this.teamLives);

        this.firstTeam.save();
        this.secondTeam.save();

        this.plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public Location getGameAreaFirst() {
        return this.gameAreaFirst;
    }

    public Location getGameAreaSecond() {
        return this.gameAreaSecond;
    }

    public int getTeamLives() {
        return this.teamLives;
    }

    public Team getFirstTeam() {
        return this.firstTeam;
    }

    public Team getSecondTeam() {
        return this.secondTeam;
    }

    public class Team implements IHyriConfiguration {

        private Location spawn;
        private final LocationEntry spawnEntry;

        private Location spawnAreaFirst;
        private final LocationEntry spawnAreaFirstEntry;
        private Location spawnAreaSecond;
        private final LocationEntry spawnAreaSecondEntry;

        private Location flag;
        private final LocationEntry flagEntry;

        public Team(String key) {
            key += ".";

            final String spawnKey = key + "spawn.";
            final String areaKey = spawnKey + "area.";

            this.spawn = DEFAULT_LOCATION.get();
            this.spawnEntry = new LocationEntry(spawnKey + "location", config);
            this.spawnAreaFirst = DEFAULT_LOCATION.get();
            this.spawnAreaFirstEntry = new LocationEntry(areaKey + "first", config);
            this.spawnAreaSecond = DEFAULT_LOCATION.get();
            this.spawnAreaSecondEntry = new LocationEntry(areaKey + "second", config);
            this.flag = DEFAULT_LOCATION.get();
            this.flagEntry = new LocationEntry(key + "flag", config);
        }

        @Override
        public void create() {
            this.spawnEntry.setDefault(this.spawn);
            this.spawnAreaFirstEntry.setDefault(this.spawnAreaFirst);
            this.spawnAreaSecondEntry.setDefault(this.spawnAreaSecond);
            this.flagEntry.setDefault(flag);
        }

        @Override
        public void load() {
            this.spawn = this.spawnEntry.get();
            this.spawnAreaFirst = this.spawnAreaFirstEntry.get();
            this.spawnAreaSecond = this.spawnAreaSecondEntry.get();
            this.flag = this.flagEntry.get();
        }

        @Override
        public void save() {
            this.spawnEntry.set(this.spawn);
            this.spawnAreaFirstEntry.set(this.spawnAreaFirst);
            this.spawnAreaSecondEntry.set(this.spawnAreaSecond);
            this.flagEntry.set(this.flag);
        }

        @Override
        public FileConfiguration getConfig() {
            return config;
        }

        public Location getSpawn() {
            return this.spawn;
        }

        public Location getSpawnAreaFirst() {
            return this.spawnAreaFirst;
        }

        public Location getSpawnAreaSecond() {
            return this.spawnAreaSecond;
        }

        public Location getFlag() {
            return this.flag;
        }

    }

}
