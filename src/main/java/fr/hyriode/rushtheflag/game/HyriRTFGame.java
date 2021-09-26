package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.*;
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

        player.getInventory().clear();
        player.getInventory().setItem(0, HyriGameItems.CHOOSE_TEAM.apply(this.rtf.getHyrame(), this.getPlayer(player.getUniqueId()), 0));
        player.getInventory().setItem(8, HyriGameItems.LEAVE.apply(player));
        player.setGameMode(GameMode.ADVENTURE);
        player.setCanPickupItems(false);
    }

    @Override
    public void startGame() {
        super.startGame();
        this.teams.forEach(HyriGameTeam::teleportToSpawn);

        System.out.println("sucess");

        Location worldSpawn = players.get(0).getPlayer().getPlayer().getWorld().getSpawnLocation();


    }

    private void registerTeams() {
        this.registerTeam(new HyriGameTeam(Teams.BLUE.getTeamName(), Teams.BLUE.getDisplayName(), Teams.BLUE.getColor(), Teams.BLUE.getTeamSize()));
        this.registerTeam(new HyriGameTeam(Teams.RED.getTeamName(), Teams.RED.getDisplayName(), Teams.RED.getColor(), Teams.RED.getTeamSize()));
    }
}
