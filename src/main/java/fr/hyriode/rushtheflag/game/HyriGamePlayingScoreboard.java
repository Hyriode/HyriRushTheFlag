package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.scoreboard.HyriGameWaitingScoreboardIpConsumer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.tools.scoreboard.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class HyriGamePlayingScoreboard extends Scoreboard {

    private static final String DASH = ChatColor.WHITE + " ⁃ ";
    private static final HyriLanguageMessage GAME = (new HyriLanguageMessage("scoreboard.game")).addValue(HyriLanguage.FR, "Jeu : ").addValue(HyriLanguage.EN, "Game: ");
    private static final HyriLanguageMessage MAP = (new HyriLanguageMessage("scoreboard.map")).addValue(HyriLanguage.FR, "Carte : ").addValue(HyriLanguage.EN, "Map: ");
    private static final HyriLanguageMessage RED  = (new HyriLanguageMessage("scoreboard.red")).addValue(HyriLanguage.FR, ChatColor.RED + "Rouge" + ChatColor.AQUA + " : ").addValue(HyriLanguage.EN, ChatColor.RED + "Red" + ChatColor.AQUA + " : ");
    private static final HyriLanguageMessage BLUE = (new HyriLanguageMessage("scoreboard.blue")).addValue(HyriLanguage.FR, ChatColor.BLUE + "Bleu" + ChatColor.AQUA + " : ").addValue(HyriLanguage.EN, ChatColor.RED + "Blue" + ChatColor.AQUA + " : ");

    private final HyriRTF hyriRTF;
    private int hoursIG = 0;
    private int minutesIG = 0;
    private int secondsIG = 0;

    public HyriGamePlayingScoreboard(HyriRTF hyriRTF, Player player) {
        super(hyriRTF, player, "rtf", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + HyriRTF.RTF);
        this.hyriRTF = hyriRTF;
        this.addLines();
    }

    private void addLines() {
        this.setLine(0, this.getCurrentIgTime(), (scoreboardLine) -> scoreboardLine.setValue(this.getCurrentIgTime()), 20);
        this.setLine(1, " ");
        this.setLine(2, DASH + GAME.getForPlayer(this.player) + ChatColor.AQUA + this.hyriRTF.getGame().getDisplayName());
        this.setLine(3, "  ");
        this.setLine(4, DASH + MAP.getForPlayer(this.player) + ChatColor.AQUA + "Aucune");
        this.setLine(5, "   ");
        this.setLine(6, BLUE.getForPlayer(this.player) + hyriRTF.getGame().getBluePoints(), (scoreboardLine) -> scoreboardLine.setValue(BLUE.getForPlayer(this.player) + hyriRTF.getGame().getBluePoints()), 33);
        this.setLine(7, RED.getForPlayer(this.player) + (6 - hyriRTF.getGame().getBluePoints()), (scoreboardLine) -> scoreboardLine.setValue(RED.getForPlayer(this.player) + (6 - hyriRTF.getGame().getBluePoints())), 33);
        this.setLine(8, "     ");
        this.setLine(9, ChatColor.DARK_AQUA + "hyriode.fr", new HyriGameWaitingScoreboardIpConsumer("hyriode.fr"), 2);
    }

    private String getCurrentIgTime() {
        this.secondsIG++;
        if(this.secondsIG == 60) {
            this.secondsIG = 0;
            this.minutesIG++;
            if(this.minutesIG == 60) {
                this.minutesIG = 0;
                this.hoursIG++;
            }
        }

        return ChatColor.GRAY + "" + new DecimalFormat("00").format(this.hoursIG) + "h" + new DecimalFormat("00").format(this.minutesIG) + "m" + new DecimalFormat("00").format(this.secondsIG);
    }

    public int getCurrentIGSeconds() {
        return this.hoursIG * 3600 + this.minutesIG * 60 + this.secondsIG;
    }
}
