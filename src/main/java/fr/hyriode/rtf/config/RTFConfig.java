package fr.hyriode.rtf.config;

import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 22/04/2022 at 16:28
 */
public class RTFConfig implements IConfig {

    private final LocationWrapper spawn;
    private final GameArea spawnArea;

    private final GameArea area;

    private final Team firstTeam;
    private final Team secondTeam;

    public RTFConfig(LocationWrapper spawn, GameArea spawnArea, GameArea area, Team firstTeam, Team secondTeam) {
        this.spawn = spawn;
        this.spawnArea = spawnArea;
        this.area = area;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public LocationWrapper getSpawn() {
        return spawn;
    }

    public GameArea getArea() {
        return area;
    }

    public GameArea getSpawnArea() {
        return this.spawnArea;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }

    public static class Team {

        private final LocationWrapper spawn;

        private final LocationWrapper spawnAreaFirst;
        private final LocationWrapper spawnAreaSecond;
        private final LocationWrapper flag;

        public Team(LocationWrapper spawn, LocationWrapper spawnAreaFirst, LocationWrapper spawnAreaSecond, LocationWrapper flag) {
            this.spawn = spawn;
            this.spawnAreaFirst = spawnAreaFirst;
            this.spawnAreaSecond = spawnAreaSecond;
            this.flag = flag;
        }

        public LocationWrapper getSpawn() {
            return this.spawn;
        }

        public LocationWrapper getSpawnAreaFirst() {
            return this.spawnAreaFirst;
        }

        public LocationWrapper getSpawnAreaSecond() {
            return this.spawnAreaSecond;
        }

        public Area getArea() {
            return new Area(this.spawnAreaFirst.asBukkit(), this.spawnAreaSecond.asBukkit());
        }

        public LocationWrapper getFlag() {
            return this.flag;
        }
    }

    public static class GameArea {

        private final LocationWrapper areaFirst;
        private final LocationWrapper areaSecond;

        public GameArea(LocationWrapper areaFirst, LocationWrapper areaSecond) {
            this.areaFirst = areaFirst;
            this.areaSecond = areaSecond;
        }

        public LocationWrapper getAreaFirst() {
            return this.areaFirst;
        }

        public LocationWrapper getAreaSecond() {
            return this.areaSecond;
        }

        public Area asArea() {
            return new Area(this.areaFirst.asBukkit(), this.areaSecond.asBukkit());
        }

    }
}
