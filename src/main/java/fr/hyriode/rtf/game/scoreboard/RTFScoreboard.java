package fr.hyriode.rtf.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.RTFGameTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 19:02
 */
public class RTFScoreboard extends HyriGameScoreboard<RTFGame> {

    private final HyriRTF plugin;

    public RTFScoreboard(HyriRTF plugin, Player player) {
        super(plugin, plugin.getGame(), player, "rushtheflag");
        this.plugin = plugin;

        this.addLines();

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(4);
        this.addBlankLine(8);
        this.addGameTimeLine(9, this.getLinePrefix("time"));
        this.addBlankLine(10);

        this.addHostnameLine();
    }

    private void addLines() {
        this.setLine(2, this.getTeamLine(this.plugin.getGame().getFirstTeam()));
        this.setLine(3, this.getTeamLine(this.plugin.getGame().getSecondTeam()));
        this.setLine(5, this.getKillsLine());
        this.setLine(6, this.getFinalKillsLine());
        this.setLine(7, this.getDeathsLine());
    }

    public void update() {
        this.addLines();
        this.updateLines();
    }

    private RTFGamePlayer getGamePlayer() {
        return this.plugin.getGame().getPlayer(this.player.getUniqueId());
    }

    private String getKillsLine() {
        return this.getLinePrefix("kills") + ChatColor.AQUA + this.getGamePlayer().getKills();
    }

    private String getDeathsLine() {
        return this.getLinePrefix("deaths") + ChatColor.AQUA + this.getGamePlayer().getDeaths();
    }

    private String getFinalKillsLine() {
        return this.getLinePrefix("final-kills") + ChatColor.AQUA + this.getGamePlayer().getFinalKills();
    }

    private String getTeamLine(RTFGameTeam team) {
        final String teamDisplay = team.getColor().getChatColor() + team.getDisplayName().getValue(this.player) + ChatColor.RESET;
        final String colon = ChatColor.WHITE + HyriLanguageMessage.get("character.colon").getValue(this.player);

        if (this.getGamePlayer().getTeam().equals(team)) {
            if(team.hasLife()) {
                return teamDisplay + colon + ChatColor.GREEN + "✔" + this.getLinePrefix("you");
            } else if(team.hasPlayersPlaying()) {
                return teamDisplay + colon + ChatColor.AQUA + team.getPlayersPlaying().size() + this.getLinePrefix("you");
            } else {
                return teamDisplay + colon + ChatColor.RED + "✘" + this.getLinePrefix("you");
            }
        } else {
            if(team.hasLife()) {
                return teamDisplay + colon + ChatColor.GREEN + "✔";
            } else if(team.hasPlayersPlaying()) {
                return teamDisplay + colon + ChatColor.AQUA + team.getPlayersPlaying().size();
            } else {
                return teamDisplay + colon + ChatColor.RED + "✘";
            }
        }
    }

    private String getLinePrefix(String prefix) {
        return HyriLanguageMessage.get("scoreboard." + prefix + ".display").getValue(this.player);
    }

}