package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.entity.Player;

public class HyriRTFGame extends HyriGame<HyriRTFGamePlayer> {

    private final HyriRTF rtf;

    public HyriRTFGame(HyriRTF rtf) {
        super(rtf.getHyrame(), "rtf", "Rush The Flag", HyriRTFGamePlayer.class);
        this.rtf = rtf;

        this.registerTabListManager();
        this.setMinPlayers(2);
        this.setMaxPlayers(2);
        this.registerTeams();
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.getInventory().setItem(0, HyriGameItems.CHOOSE_TEAM.apply(this.rtf.getHyrame(), this.getPlayer(player.getUniqueId()), 0));
        player.getInventory().setItem(8, HyriGameItems.LEAVE.apply(player));
    }

    private void registerTeams() {
        this.registerTeam(new HyriGameTeam("CalyxTeam", new LanguageMessage("calyx.team").addValue(Language.FR, "La team de Calyx"), HyriGameTeamColor.BLUE, 1));
        this.registerTeam(new HyriGameTeam("2IQTeam", new LanguageMessage("2IQ.team").addValue(Language.FR, "La team de 2IQ"), HyriGameTeamColor.RED, 1));
    }
}
