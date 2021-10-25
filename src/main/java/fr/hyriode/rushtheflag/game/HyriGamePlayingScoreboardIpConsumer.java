package fr.hyriode.rushtheflag.game;

import fr.hyriode.common.board.ScoreboardLine;
import java.util.function.Consumer;
import org.bukkit.ChatColor;

class HyriGamePlayingScoreboardIpConsumer implements Consumer<ScoreboardLine> {
    private int count = 0;
    private int charIndex = 0;
    private final String ip;

    public HyriGamePlayingScoreboardIpConsumer(String ip) {
        this.ip = ip;
    }

    public void accept(ScoreboardLine scoreboardLine) {
        if (this.count >= 20) {
            if (this.charIndex > this.ip.length()) {
                this.count = 0;
                this.charIndex = 0;
                return;
            }

            if (this.charIndex == 0) {
                scoreboardLine.setValue(ChatColor.AQUA + this.ip.substring(0, 1) + ChatColor.DARK_AQUA + this.ip.substring(1));
            } else if (this.charIndex == this.ip.length()) {
                scoreboardLine.setValue(ChatColor.DARK_AQUA + "hyriode.fr");
            } else {
                String start = this.ip.substring(0, this.charIndex);
                String character = this.ip.substring(this.charIndex, this.charIndex + 1);
                String end = this.ip.substring(this.charIndex + 1);
                scoreboardLine.setValue(ChatColor.DARK_AQUA + start + ChatColor.AQUA + character + ChatColor.DARK_AQUA + end);
            }

            ++this.charIndex;
        }

        ++this.count;
    }
}
