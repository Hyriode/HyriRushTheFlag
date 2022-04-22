package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.api.statistics.HyriRTFStatistics;
import fr.hyriode.rtf.game.abilities.RTFAbility;
import fr.hyriode.rtf.game.items.RTFChooseAbilityItem;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class RTFGame extends HyriGame<RTFGamePlayer> {

    private RTFGameTeam firstTeam;
    private RTFGameTeam secondTeam;

    private final Location spawn;
    private final HyriRTF plugin;

    private final RTFGameType gameType;

    public RTFGame(IHyrame hyrame, HyriRTF plugin) {
        super(hyrame, plugin, "rtf", "RushTheFlag", RTFGamePlayer.class);
        this.plugin = plugin;
        this.spawn = this.plugin.getConfiguration().getSpawn();

        this.gameType = RTFGameType.DOUBLES;

        final int full = this.getType().getTeamSize() * 2;

        this.minPlayers = full;
        this.maxPlayers = full;

        this.registerTeams();
    }

    private void registerTeams() {
        final Pair<RTFTeam, RTFTeam> teamsPair = RTFTeam.get();

        this.firstTeam = new RTFGameTeam(this.plugin, teamsPair.getKey(), this.plugin.getConfiguration().getFirstTeam(), this.getType().getTeamSize());
        this.secondTeam = new RTFGameTeam(this.plugin, teamsPair.getValue(), this.plugin.getConfiguration().getSecondTeam(), this.getType().getTeamSize());

        this.registerTeam(this.firstTeam);
        this.registerTeam(this.secondTeam);
    }

    @Override
    public void postRegistration() {
        super.postRegistration();

        this.protocolManager.enableProtocol(new HyriWaitingProtocol(this.hyrame, this.plugin));
    }

    @Override
    public void start() {
        super.start();

        this.firstTeam.getFlag().place();
        this.secondTeam.getFlag().place();

        for (RTFGamePlayer player : this.players) {
            player.startGame();
        }

        final HyriDeathProtocol.Options.YOptions yOptions = new HyriDeathProtocol.Options.YOptions(new Area(this.plugin.getConfiguration().getGameAreaFirst(), this.plugin.getConfiguration().getGameAreaSecond()).getMin().getY());

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 15 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            final Player player = gamePlayer.getPlayer();

            player.teleport(this.spawn);

            return this.getPlayer(player).kill();
        }, this.createDeathScreen(), HyriDeathProtocol.ScreenHandler.Default.class).withOptions(new HyriDeathProtocol.Options().withYOptions(yOptions).withDeathSound(true).withDeathMessages(true)));

        this.handleEndGame();
    }

    private HyriDeathProtocol.Screen createDeathScreen() {
        return new HyriDeathProtocol.Screen(5, player -> {
            final RTFGamePlayer gamePlayer = this.getPlayer(player.getUniqueId());

            gamePlayer.spawn(true);
        });
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        PlayerUtil.resetPlayer(player, true);

        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);

        HyriRTFPlayer account = this.plugin.getAPI().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            account = new HyriRTFPlayer(uuid);
        }

        gamePlayer.setAccount(account);
        gamePlayer.setConnectionTime();
        gamePlayer.setCooldown(false);
        Optional<RTFAbility> ability = RTFAbility.getWithModel(account.getLastAbility());
        ability.ifPresent(gamePlayer::setAbility);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.hyrame.getItemManager().giveItem(player, 4, RTFChooseAbilityItem.class), 1);
        HyriGameItems.LEAVE.give(this.hyrame, player, 8);

        player.teleport(this.spawn);
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);
        this.refreshAPIPlayer(gamePlayer);
        super.handleLogout(player);
    }

    @Override
    public void win(HyriGameTeam team) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            final RTFGamePlayer gamePlayer = this.getPlayer(player.getUniqueId());
            this.refreshAPIPlayer(gamePlayer);
        }
        super.win(team);
    }

    public RTFGameTeam getWinner() {
        if (this.firstTeam.hasPlayersPlaying() && !this.secondTeam.hasPlayersPlaying()) {
            return this.firstTeam;
        } else if (!this.firstTeam.hasPlayersPlaying() && this.secondTeam.hasPlayersPlaying()) {
            return this.secondTeam;
        }
        return null;
    }

    public void handleEndGame() {
        this.timer.setOnTimeChanged(index -> {
            if (index == 300) {
                endingGame(5);
            } else if (index == 360) {
                endingGame(4);
            } else if (index == 420) {
                endingGame(3);
            } else if (index == 480) {
                endingGame(2);
            } else if (index == 540) {
                endingGame(1);
            } else if (index == 600) {
                Bukkit.getScheduler().runTask(plugin, this::destroyFlags);
            }
        });
    }

    public boolean isHoldingFlag(Player player) {
        return this.getHoldingFlag(player) != null;
    }

    public RTFFlag getHoldingFlag(Player player) {
        if (this.firstTeam.getFlag().getHolder() == player) {
            return this.firstTeam.getFlag();
        } else if (this.secondTeam.getFlag().getHolder() == player) {
            return this.secondTeam.getFlag();
        }
        return null;
    }

    public void endingGame(int minutesLeft) {
        if (minutesLeft == 1) {
            sendMessageToAll(player -> " \n " + RTFMessage.ENDING_GAME_MESSAGE.get().getForPlayer(player)
                    .replace("%time%", "1")
                    .replace("minutes", "minute")
                    + " \n "
            );
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 3f, 3f);
            }
        } else {
            sendMessageToAll(player -> " \n " + RTFMessage.ENDING_GAME_MESSAGE.get().getForPlayer(player)
                    .replace("%time%", String.valueOf(minutesLeft))
                    + " \n "
            );
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f);
            }
        }
    }

    public void destroyFlags() {
        this.sendMessageToAll(player -> " \n " + RTFMessage.END_GAME_MESSAGE.get().getForPlayer(player) + " \n ");

        for (HyriGameTeam gameTeam : this.teams) {
            RTFGameTeam team = (RTFGameTeam) gameTeam;
            team.getFlag().resetHolder();
            if(team.hasLife()) {
                team.getFlag().getBlock().setType(Material.AIR);
                team.removeLife();
            }
        }

        for (RTFGamePlayer player : players) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.WITHER_SPAWN, 3f, 3f);
            player.spawn(false);
        }
    }

    public RTFGameTeam getFirstTeam() {
        return this.firstTeam;
    }

    public RTFGameTeam getSecondTeam() {
        return this.secondTeam;
    }

    private void refreshAPIPlayer(RTFGamePlayer gamePlayer) {
        final HyriRTFPlayer account = gamePlayer.getAccount();
        final HyriRTFStatistics statistics = account.getStatistics();
        account.setLastAbility(gamePlayer.getAbility().getModel());

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING) {
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

    @Override
    public RTFGameType getType() {
        return (RTFGameType) this.gameType;
    }
}
