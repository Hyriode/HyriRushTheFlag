package fr.hyriode.rtf.game.team;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.RTFConfig;
import fr.hyriode.rtf.game.RTFFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 01/01/2022 at 11:02
 */
public class RTFGameTeam extends HyriGameTeam {

    private int lives;
    private final HyriRTF plugin;

    private final Supplier<RTFConfig.Team> config;
    private final RTFFlag flag;

    public RTFGameTeam(HyriRTF plugin, RTFTeam team, Supplier<RTFConfig.Team> config, int teamSize) {
        super(team.getName(), team.getDisplayName(), team.getColor(), teamSize);
        this.plugin = plugin;
        this.config = config;
        this.flag = new RTFFlag(plugin, this);
    }

    public boolean hasOnlinePlayers() {
        return this.players.values().stream().anyMatch(HyriGamePlayer::isOnline);
    }

    public boolean isInBase(Player player) {
        if (this.contains(player.getUniqueId())) {
            return this.config.get().getSpawnArea().asArea().isInArea(player.getLocation());
        }
        return false;
    }

    public RTFGameTeam getOppositeTeam() {
        if (this.equals(this.plugin.getGame().getFirstTeam())) {
            return this.plugin.getGame().getSecondTeam();
        } else {
            return this.plugin.getGame().getFirstTeam();
        }
    }

    public RTFConfig.Team getConfig() {
        return this.config.get();
    }

    public RTFFlag getFlag() {
        return this.flag;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
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

    public Location getSpawnLocation() {
        return this.config.get().getSpawn().asBukkit();
    }

}
