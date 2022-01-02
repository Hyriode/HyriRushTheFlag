package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.config.HyriRTFConfig;
import org.bukkit.entity.Player;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 11:02
 */
public class HyriRTFGameTeam extends HyriGameTeam {

    private int lives;

    private final HyriRTFConfig.Team config;
    private final HyriRTFFlag flag;

    public HyriRTFGameTeam(HyriRTF plugin, HyriRTFTeams team, HyriRTFConfig.Team config) {
        super(team.getName(), team.getDisplayName(), team.getColor(), 2);
        this.config = config;
        this.spawnLocation = this.config.getSpawn();
        this.flag = new HyriRTFFlag(plugin, this);

        this.lives = plugin.getConfiguration().getTeamLives();
    }

    public boolean isInBase(Player player) {
        if (this.contains(player.getUniqueId())) {
            return LocationUtil.isInArea(player.getLocation(), config.getSpawnAreaFirst(), config.getSpawnAreaSecond());
        }
        return false;
    }

    public HyriRTFConfig.Team getConfig() {
        return this.config;
    }

    public HyriRTFFlag getFlag() {
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
