package fr.hyriode.rtf.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 22/04/2022 at 16:28
 */
public class RTFConfig implements IHyriConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    private final AreaWrapper area;

    private final Team firstTeam;
    private final Team secondTeam;

    public RTFConfig(HyriWaitingRoom.Config waitingRoom, AreaWrapper area, Team firstTeam, Team secondTeam) {
        this.waitingRoom = waitingRoom;
        this.area = area;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

    public AreaWrapper getArea() {
        return area;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }

    public static class Team {

        private final LocationWrapper spawn;
        private final AreaWrapper spawnArea;

        private final List<LocationWrapper> flags;

        private List<Location> flagsBukkit;

        public Team(LocationWrapper spawn, AreaWrapper spawnArea, List<LocationWrapper> flags) {
            this.spawn = spawn;
            this.spawnArea = spawnArea;
            this.flags = flags;
        }

        public LocationWrapper getSpawn() {
            return this.spawn;
        }

        public AreaWrapper getSpawnArea() {
            return this.spawnArea;
        }

        public List<LocationWrapper> getFlags() {
            return this.flags;
        }

        public List<Location> getFlagsAsBukkit() {
            return this.flagsBukkit == null ? this.flagsBukkit = this.flags.stream().map(LocationWrapper::asBukkit).collect(Collectors.toList()) : this.flagsBukkit;
        }

    }

}
