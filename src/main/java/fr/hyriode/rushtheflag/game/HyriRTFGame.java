package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class HyriRTFGame extends HyriGame<HyriRTFGamePlayer> {

    private final HyriRTF hyriRTF;

    public HyriRTFGame(HyriRTF hyriRTF) {
        super(hyriRTF.getHyrame(), "rtf", "Rush The Flag", HyriRTFGamePlayer.class);
        this.hyriRTF = hyriRTF;

        this.registerTabListManager();
        this.setMinPlayers(2);
        this.setMaxPlayers(2);
        this.registerTeams();
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.getInventory().clear();
        player.getInventory().setItem(0, HyriGameItems.CHOOSE_TEAM.apply(this.hyriRTF.getHyrame(), this.getPlayer(player.getUniqueId()), 0));
        player.getInventory().setItem(8, HyriGameItems.LEAVE.apply(player));
        player.setHealth(20);

        player.setGameMode(GameMode.ADVENTURE);
        player.setCanPickupItems(false);
    }

    @Override
    public void startGame() {
        super.startGame();

        Bukkit.getScheduler().runTaskLater(this.hyriRTF, () -> {
            for (HyriGameTeam team : teams) {
                team.setSpawnLocation(hyriRTF.getHyriRTFconfiguration().getLocation(team.getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY));
                team.teleportToSpawn();
                for(HyriGamePlayer hyriGamePlayer : team.getPlayers()) {
                    hyriRTF.getHyriRTFMethods().spawnPlayer(hyriGamePlayer);
                    hyriGamePlayer.getPlayer().getPlayer().setFoodLevel(20);
                    hyriGamePlayer.getPlayer().getPlayer().setSaturation(7f);
                    final HyriGamePlayingScoreboard hyriGamePlayingScoreboard = new HyriGamePlayingScoreboard(this.hyriRTF, hyriGamePlayer.getPlayer().getPlayer());
                    hyriGamePlayingScoreboard.show();
                }
                new HyriRTFFlag(hyriRTF, team);
            }
        }, 1L);
    }

    private void registerTeams() {
        this.registerTeam(new HyriGameTeam(Teams.BLUE.getTeamName(), Teams.BLUE.getDisplayName(), Teams.BLUE.getColor(), Teams.BLUE.getTeamSize()));
        this.registerTeam(new HyriGameTeam(Teams.RED.getTeamName(), Teams.RED.getDisplayName(), Teams.RED.getColor(), Teams.RED.getTeamSize()));
    }
}
