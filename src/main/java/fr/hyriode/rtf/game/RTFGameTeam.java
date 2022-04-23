package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.RTFConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 01/01/2022 at 11:02
 */
public class RTFGameTeam extends HyriGameTeam {

    private int lives;
    private final HyriRTF plugin;

    private final RTFConfig.Team config;
    private final RTFFlag flag;

    public RTFGameTeam(HyriRTF plugin, RTFTeam team, RTFConfig.Team config, int teamSize) {
        super(
                plugin.getGame(),
                team.getName(),
                team.getDisplayName(),
                team.getColor(),
                teamSize
        );
        this.plugin = plugin;
        this.config = config;
        this.spawnLocation = this.config.getSpawn().asBukkit();
        this.flag = new RTFFlag(plugin, this);

        this.lives = 1;
    }

    public boolean isInBase(Player player) {
        if (this.contains(player.getUniqueId())) {
            return this.config.getArea().isInArea(player.getLocation());
        }
        return false;
    }

    public boolean isInBase(Location location) {
        return this.config.getArea().isInArea(location);
    }


    public RTFGameTeam getOppositeTeam() {
        if (this.equals(this.plugin.getGame().getFirstTeam())) {
            return this.plugin.getGame().getSecondTeam();
        } else {
            return this.plugin.getGame().getFirstTeam();
        }
    }

    public RTFConfig.Team getConfig() {
        return this.config;
    }

    public RTFFlag getFlag() {
        return this.flag;
    }

    public int getLives() {
        return this.lives;
    }

    public void addLife() {
        this.lives += 1;
    }

    public void removeLife() {
        this.lives -= 1;
    }

    public boolean hasLife() {
        return this.lives > 0;
    }
}
