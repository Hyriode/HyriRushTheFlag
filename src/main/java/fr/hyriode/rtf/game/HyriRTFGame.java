package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.api.statistics.HyriRTFStatistics;
import fr.hyriode.rtf.game.events.Event;
import fr.hyriode.rtf.game.scoreboard.HyriRTFScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class HyriRTFGame extends HyriGame<HyriRTFGamePlayer> {

    private HyriRTFGameTeam firstTeam;
    private HyriRTFGameTeam secondTeam;

    private final Location spawn;
    private final HyriRTF plugin;

    public HyriRTFGame(IHyrame hyrame, HyriRTF plugin) {
        super(hyrame, plugin, "rtf", "RushTheFlag", HyriRTFGamePlayer.class);
        this.plugin = plugin;
        this.spawn = this.plugin.getConfiguration().getSpawn();
        this.minPlayers = 2;
        this.maxPlayers = 4;

        this.registerTeams();
    }

    private void registerTeams() {
        final Pair<HyriRTFTeams, HyriRTFTeams> teamsPair = HyriRTFTeams.get();

        this.firstTeam = new HyriRTFGameTeam(this.plugin, teamsPair.getKey(), this.plugin.getConfiguration().getFirstTeam());
        this.secondTeam = new HyriRTFGameTeam(this.plugin, teamsPair.getValue(), this.plugin.getConfiguration().getSecondTeam());

        this.registerTeam(this.firstTeam);
        this.registerTeam(this.secondTeam);
    }

    @Override
    public void start() {
        super.start();

        this.firstTeam.getFlag().place();
        this.secondTeam.getFlag().place();

        this.firstTeam.teleportToSpawn();
        this.secondTeam.teleportToSpawn();

        for (HyriRTFGamePlayer player : this.players) {
            final HyriRTFScoreboard scoreboard = new HyriRTFScoreboard(this.plugin, player.getPlayer());

            player.setScoreboard(scoreboard);

            scoreboard.show();

            player.spawn();
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setCanPickupItems(false);

        final UUID uuid = player.getUniqueId();
        final HyriRTFGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);

        HyriRTFPlayer account = this.plugin.getAPI().getPlayerManager().getPlayer(uuid);

        if(account == null) {
            account = new HyriRTFPlayer(uuid);
        }

        gamePlayer.setAccount(account);
        gamePlayer.setConnectionTime();

        HyriGameItems.TEAM_CHOOSER.give(this.hyrame, player, 0);
        HyriGameItems.LEAVE_ITEM.give(this.hyrame, player, 8);

        player.teleport(this.spawn);

        Event.registerEvents(this.plugin);
        Bukkit.broadcastMessage(String.valueOf(Event.EVENTS));
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final HyriRTFGamePlayer gamePlayer = this.getPlayer(uuid);
        this.refreshAPIPlayer(gamePlayer);
        super.handleLogout(player);
    }

    @Override
    public void win(HyriGameTeam team) {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            final HyriRTFGamePlayer gamePlayer = this.getPlayer(player.getUniqueId());
            this.refreshAPIPlayer(gamePlayer);
        }
        super.win(team);
    }

    public HyriRTFGameTeam getWinner() {
        if (this.firstTeam.hasPlayersPlaying() && !this.secondTeam.hasPlayersPlaying()) {
            return this.firstTeam;
        } else if (!this.firstTeam.hasPlayersPlaying() && this.secondTeam.hasPlayersPlaying()) {
            return this.secondTeam;
        }
        return null;
    }

    public boolean isHoldingFlag(Player player) {
        return this.getHoldingFlag(player) != null;
    }

    public HyriRTFFlag getHoldingFlag(Player player) {
        if (this.firstTeam.getFlag().getHolder() == player) {
            return this.firstTeam.getFlag();
        } else if (this.secondTeam.getFlag().getHolder() == player) {
            return this.secondTeam.getFlag();
        }
        return null;
    }

    public HyriRTFGameTeam getFirstTeam() {
        return this.firstTeam;
    }

    public HyriRTFGameTeam getSecondTeam() {
        return this.secondTeam;
    }

    private void refreshAPIPlayer(HyriRTFGamePlayer gamePlayer) {
        final HyriRTFPlayer account = gamePlayer.getAccount();
        final HyriRTFStatistics statistics = account.getStatistics();

        if (this.state != HyriGameState.READY && this.state != HyriGameState.WAITING) {
            gamePlayer.getScoreboard().hide();

            statistics.setPlayedTime(gamePlayer.getPlayedTime());
            statistics.addGamesPlayed(1);
            statistics.addKills(gamePlayer.getKills());
            statistics.addFinalKills(gamePlayer.getFinalKills());
            statistics.addDeaths(gamePlayer.getDeaths());
            statistics.addCapturedFlags(gamePlayer.getCapturedFlags());
            statistics.addFlagsBroughtBack(gamePlayer.getFlagsBroughtBack());

            this.plugin.getAPI().getPlayerManager().sendPlayer(account);
        }

    }

}