package fr.hyriode.rtf.config;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.Location;

import java.util.List;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 22/04/2022 at 16:28
 */
public class RTFConfig implements IConfig {

    private final WaitingRoom room;
    private final GameArea area;
    private final Team firstTeam;
    private final Team secondTeam;

    public RTFConfig(WaitingRoom room, GameArea area, Team firstTeam, Team secondTeam) {
        this.room = room;
        this.area = area;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public GameArea getArea() {
        return area;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }

    public WaitingRoom getWaitingRoom() {
        return this.room;
    }

    public static class WaitingRoom extends HyriWaitingRoom.Config {
        public WaitingRoom(LocationWrapper waitingSpawn, LocationWrapper waitingSpawnPos1, LocationWrapper waitingSpawnPos2, LocationWrapper npcLocation) {
            super(waitingSpawn, waitingSpawnPos1, waitingSpawnPos2, npcLocation);
        }
    }

    public static class Team {

        private final LocationWrapper spawn;

        private final LocationWrapper spawnAreaFirst;
        private final LocationWrapper spawnAreaSecond;
        private final List<LocationWrapper> flags;

        public Team(LocationWrapper spawn, LocationWrapper spawnAreaFirst, LocationWrapper spawnAreaSecond, List<LocationWrapper> flags) {
            this.spawn = spawn;
            this.spawnAreaFirst = spawnAreaFirst;
            this.spawnAreaSecond = spawnAreaSecond;
            this.flags = flags;
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

        public List<LocationWrapper> getFlag() {
            return this.flags;
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
