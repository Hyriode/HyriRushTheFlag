package fr.hyriode.rtf.game.scoreboard;

import fr.hyriode.hyrame.game.scoreboard.HyriScoreboardIpConsumer;
import fr.hyriode.hyrame.scoreboard.Scoreboard;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.HyriRTFGameTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 19:02
 */
public class HyriRTFScoreboard extends Scoreboard {

    private final HyriRTF plugin;

    public HyriRTFScoreboard(HyriRTF plugin, Player player) {
        super(plugin, player, "rtf", ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + plugin.getGame().getDisplayName() + "     ");
        this.plugin = plugin;

        this.addLines();

        this.setLine(2, this.getTeamLine(this.plugin.getGame().getFirstTeam()), line -> line.setValue(this.getTeamLine(this.plugin.getGame().getFirstTeam())), 20);
        this.setLine(3, this.getTeamLine(this.plugin.getGame().getSecondTeam()), line -> line.setValue(this.getTeamLine(this.plugin.getGame().getSecondTeam())), 20);
        this.setLine(9, this.getTimeLine(), line -> line.setValue(this.getTimeLine()), 20);
        this.setLine(11, ChatColor.DARK_AQUA + "hyriode.fr", new HyriScoreboardIpConsumer("hyriode.fr"), 2);
    }

    private void addLines() {
        this.setLine(0, this.getDateLine());
        this.setLine(1, "§1");
        this.setLine(4, "§2");
        this.setLine(5, this.getKillsLine(), line -> line.setValue(this.getKillsLine()), 40);
        this.setLine(6, this.getFinalKillsLine(), line -> line.setValue(this.getFinalKillsLine()), 40);
        this.setLine(7, this.getDeathsLine(), line -> line.setValue(this.getDeathsLine()), 40);
        this.setLine(8, "§3");
        this.setLine(10, "§4");
    }

    public void update() {
        this.addLines();

        this.updateLines();
    }

    private HyriRTFGamePlayer getGamePlayer() {
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

    private String getTeamLine(HyriRTFGameTeam team) {
        final String teamDisplay = team.getColor().getChatColor() + team.getDisplayName().getForPlayer(this.player) + ChatColor.RESET;
        final String colon = ChatColor.WHITE + HyriRTF.getLanguageManager().getValue(this.player, "character.colon");

        if (team.hasLife()) {
            if (this.getGamePlayer().getTeam().equals(team)) {
                return teamDisplay + colon + ChatColor.GREEN + "✔" + this.getLinePrefix("you");
            }
            return teamDisplay + colon + ChatColor.GREEN + "✔";
        } else {

            if (this.getGamePlayer().getTeam().equals(team)) {
                return teamDisplay + colon + ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD + ChatColor.WHITE + " " + Symbols.DOT_BOLD + " " + ChatColor.AQUA + team.getPlayersPlaying().size() + this.getLinePrefix("you");
            }

            return teamDisplay + colon + ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD + ChatColor.WHITE + " " + Symbols.DOT_BOLD + " " + ChatColor.AQUA + team.getPlayersPlaying().size();
        }
    }

    private String getTimeLine() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(this.plugin.getGame().getGameTime() * 1000);

        return this.getLinePrefix("time") + ChatColor.AQUA + (line.startsWith("00:") ? line.substring(3) : line);
    }

    private String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private String getLinePrefix(String prefix) {
        return HyriRTF.getLanguageManager().getValue(this.player, "scoreboard." + prefix + ".display");
    }

}
