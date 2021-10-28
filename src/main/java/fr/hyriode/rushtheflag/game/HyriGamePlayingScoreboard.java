package fr.hyriode.rushtheflag.game;

import fr.hyriode.common.board.Scoreboard;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class HyriGamePlayingScoreboard extends Scoreboard {

    private static final String DASH = ChatColor.WHITE + " ⁃ ";
    private static final LanguageMessage GAME = (new LanguageMessage("scoreboard.game")).addValue(Language.FR, "Jeu : ").addValue(Language.EN, "Game: ");
    private static final LanguageMessage MAP = (new LanguageMessage("scoreboard.map")).addValue(Language.FR, "Carte : ").addValue(Language.EN, "Map: ");
    private static final LanguageMessage RED  = (new LanguageMessage("scoreboard.red")).addValue(Language.FR, ChatColor.RED + "Rouge" + ChatColor.AQUA + " ⇒ ").addValue(Language.EN, ChatColor.RED + "Red" + ChatColor.AQUA + " ⇒ ");
    private static final LanguageMessage BLUE = (new LanguageMessage("scoreboard.blue")).addValue(Language.FR, ChatColor.BLUE + "Bleu" + ChatColor.AQUA + " ⇒ ").addValue(Language.EN, ChatColor.RED + "Blue" + ChatColor.AQUA + " ⇒ ");

    private final HyriRTF hyriRTF;
    private int hoursIG = 0;
    private int minutesIG = 0;
    private int secondsIG = 0;

    public HyriGamePlayingScoreboard(HyriRTF hyriRTF, Player player) {
        super(hyriRTF, player, "rtf", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Hyriode");
        this.hyriRTF = hyriRTF;
        this.addLines();
    }

    private void addLines() {
        this.setLine(0, this.getCurrentIgTime(), (scoreboardLine) -> {
            scoreboardLine.setValue(this.getCurrentIgTime());
        }, 20);
        this.setLine(1, " ");
        this.setLine(2, DASH + GAME.getForPlayer(this.player) + ChatColor.AQUA + this.hyriRTF.getGame().getDisplayName());
        this.setLine(3, "  ");
        this.setLine(4, DASH + MAP.getForPlayer(this.player) + ChatColor.AQUA + "Aucune");
        this.setLine(5, "   ");
        this.setLine(6, BLUE.getForPlayer(this.player) + hyriRTF.getHyriRTFMethods().getBluePoints(), (scoreboardLine) -> {
            scoreboardLine.setValue(BLUE.getForPlayer(this.player) + hyriRTF.getHyriRTFMethods().getBluePoints());
        }, 33);
        this.setLine(7, RED.getForPlayer(this.player) + (6 - hyriRTF.getHyriRTFMethods().getBluePoints()), (scoreboardLine) -> {
            scoreboardLine.setValue(RED.getForPlayer(this.player) + (6 - hyriRTF.getHyriRTFMethods().getBluePoints()));
        }, 33);
        this.setLine(8, "     ");
        this.setLine(9, ChatColor.DARK_AQUA + "hyriode.fr", new HyriGamePlayingScoreboardIpConsumer("hyriode.fr"), 2);
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
}
