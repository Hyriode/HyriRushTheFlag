package fr.hyriode.rtf.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.IHyriLeaderboardProvider;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriAntiSpawnKillProtocol;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFData;
import fr.hyriode.rtf.api.RTFStatistics;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.item.RTFChooseAbilityItem;
import fr.hyriode.rtf.game.ui.scoreboard.RTFScoreboard;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import fr.hyriode.rtf.game.team.RTFTeam;
import fr.hyriode.rtf.util.RTFMessage;
import fr.hyriode.rtf.util.RTFValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class RTFGame extends HyriGame<RTFGamePlayer> {

    private RTFGameTeam firstTeam;
    private RTFGameTeam secondTeam;

    private final HyriRTF plugin;

    private boolean flagsAvailable;

    public RTFGame(IHyrame hyrame, HyriRTF plugin) {
        super(hyrame, plugin,
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo("rushtheflag", "RushTheFlag") : HyriAPI.get().getGameManager().getGameInfo("rushtheflag"),
                RTFGamePlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? RTFGameType.SOLO : HyriGameType.getFromData(RTFGameType.values()));
        this.plugin = plugin;
        this.description = HyriLanguageMessage.get("message.game.description");
        this.reconnectionTime = 120;
        this.waitingRoom = new RTFWaitingRoom(this);
        this.flagsAvailable = true;

        this.registerTeams();
    }

    private void registerTeams() {
        final Pair<RTFTeam, RTFTeam> teamsPair = RTFTeam.get();

        this.firstTeam = new RTFGameTeam(this.plugin, teamsPair.getKey(), () -> this.plugin.getConfiguration().getFirstTeam(), this.getType().getTeamSize());
        this.secondTeam = new RTFGameTeam(this.plugin, teamsPair.getValue(), () -> this.plugin.getConfiguration().getSecondTeam(), this.getType().getTeamSize());

        this.registerTeam(this.firstTeam);
        this.registerTeam(this.secondTeam);
    }

    @Override
    public void start() {
        super.start();

        this.firstTeam.setLives(RTFValues.LIVES.get());
        this.secondTeam.setLives(RTFValues.LIVES.get());
        this.firstTeam.getFlag().place();
        this.secondTeam.getFlag().place();

        for (RTFGamePlayer player : this.players) {
            player.startGame();
        }

        final HyriDeathProtocol.Options.YOptions yOptions = new HyriDeathProtocol.Options.YOptions(this.plugin.getConfiguration().getArea().asArea().getMin().getY());

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 10 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            final Player player = gamePlayer.getPlayer();

            player.teleport(this.waitingRoom.getConfig().getSpawn().asBukkit());

            return ((RTFGamePlayer) gamePlayer).kill();
        }, this.createDeathScreen(), HyriDeathProtocol.ScreenHandler.Default.class).withOptions(new HyriDeathProtocol.Options().withYOptions(yOptions)));
        this.protocolManager.enableProtocol(new HyriAntiSpawnKillProtocol(this.hyrame, new HyriAntiSpawnKillProtocol.Options(2 * 20, true)));

        this.startGameTimer();
    }

    private HyriDeathProtocol.Screen createDeathScreen() {
        return new HyriDeathProtocol.Screen(Math.toIntExact(RTFValues.RESPAWN_TIME.get()), player -> {
            final RTFGamePlayer gamePlayer = this.getPlayer(player);

            if (gamePlayer == null) {
                return;
            }

            gamePlayer.spawn(true);
        });
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);

        if (gamePlayer == null) {
            return;
        }

        final RTFData data = RTFData.get(uuid);
        final RTFStatistics statistics = RTFStatistics.get(uuid);

        gamePlayer.setStatistics(statistics);
        gamePlayer.setData(data);
        gamePlayer.setCooldown(false);

        RTFAbility.getWithModel(data.getLastAbility())
                .filter(RTFAbility::isEnabled)
                .ifPresent(gamePlayer::setAbility);

        this.hyrame.getItemManager().giveItem(player, 4, RTFChooseAbilityItem.class);

        if (gamePlayer.getAbility() != null) {
            player.sendMessage(RTFMessage.LAST_ABILITY_MESSAGE.asString(player).replace("%ability%", gamePlayer.getAbility().getName(player)));
        }
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);

        this.refreshAPIPlayer(gamePlayer);

        super.handleLogout(player);

        if (this.getState().isAccessible()) {
            return;
        }

        if (this.getState() == HyriGameState.PLAYING) {
            if (!gamePlayer.getTeam().hasPlayersPlaying()) {
                this.win(((RTFGameTeam) gamePlayer.getTeam()).getOppositeTeam());
            }
        }
    }

    @Override
    public void win(HyriGameTeam winner) {
        if (winner == null || this.getState() != HyriGameState.PLAYING) {
            return;
        }

        IHyrame.get().getScoreboardManager().getScoreboards(RTFScoreboard.class).forEach(RTFScoreboard::update);

        super.win(winner);

        final List<HyriLanguageMessage> positions = Arrays.asList(
                HyriLanguageMessage.get("message.game.end.1"),
                HyriLanguageMessage.get("message.game.end.2"),
                HyriLanguageMessage.get("message.game.end.3")
        );

        final List<RTFGamePlayer> topKillers = new ArrayList<>(this.players);

        topKillers.sort((o1, o2) -> Math.toIntExact(o2.getKills() - o1.getKills()));

        final Function<Player, List<String>> killersLineProvider = player -> {
            final List<String> killersLine = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final String killerLine = HyriLanguageMessage.get("message.game.end.kills").getValue(player).replace("%position%", positions.get(i).getValue(player));

                if (topKillers.size() > i){
                    final RTFGamePlayer topKiller = topKillers.get(i);

                    killersLine.add(killerLine.replace("%player%", topKiller.formatNameWithTeam()).replace("%kills%", String.valueOf(topKiller.getKills())));
                    continue;
                }

                killersLine.add(killerLine.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player)).replace("%kills%", "0"));
            }

            return killersLine;
        };

        // Send message to not-playing players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.getPlayer(player) == null) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, killersLineProvider.apply(player), null));
            }
        }

        for (RTFGamePlayer gamePlayer : this.players) {
            final UUID playerId = gamePlayer.getUniqueId();
            final IHyriPlayer account = gamePlayer.asHyriPlayer();

            // Hyris and XP calculations
            final boolean host = HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST;
            final int kills = (int) gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = host ? 0 : account.getHyris().add(
                    HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner)
                            + gamePlayer.getCapturedFlags() * 10L
                            + gamePlayer.getFlagsBroughtBack() * 20L).
                    withMessage(false)
                    .exec();
            final double xp = host ? 0.0D : account.getNetworkLeveling().addExperience(
                    HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner)
                            + gamePlayer.getCapturedFlags() * 10D
                            + gamePlayer.getFlagsBroughtBack() * 20D);

            if (!host) {
                // Experience leaderboard updates
                final IHyriLeaderboardProvider provider = HyriAPI.get().getLeaderboardProvider();

                provider.getLeaderboard(NetworkLeveling.LEADERBOARD_TYPE, "rushtheflag-experience").incrementScore(playerId, xp);
                provider.getLeaderboard("rushtheflag", "kills").incrementScore(playerId, kills);
                provider.getLeaderboard("rushtheflag", "flags-brought-back").incrementScore(playerId, gamePlayer.getFlagsBroughtBack());

                if (isWinner) {
                    provider.getLeaderboard("rushtheflag", "victories").incrementScore(playerId, 1);

                    gamePlayer.getStatistics().getData(this.getType()).addVictories(1);
                }
            }

            account.update();

            this.refreshAPIPlayer(gamePlayer);

            // Send message
            final String rewardsLine = ChatColor.LIGHT_PURPLE + "+" + hyris + " Hyris " + ChatColor.GREEN + "+" + xp + " XP";

            if (gamePlayer.isOnline()) {
                final Player player = gamePlayer.getPlayer();

                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, gamePlayer.getPlayer(), winner, killersLineProvider.apply(player), rewardsLine));
            } else if (HyriAPI.get().getPlayerManager().isOnline(playerId)) {
                HyriAPI.get().getPlayerManager().sendMessage(playerId, HyriGameMessages.createOfflineWinMessage(this, account, rewardsLine));
            }
        }
    }

    public RTFGameTeam getWinner() {
        if (this.firstTeam.hasPlayersPlaying() && !this.secondTeam.hasPlayersPlaying()) {
            return this.firstTeam;
        } else if (!this.firstTeam.hasPlayersPlaying() && this.secondTeam.hasPlayersPlaying()) {
            return this.secondTeam;
        }
        return null;
    }

    public void startGameTimer() {
        this.timer.setOnTimeChanged(index -> {
            final long maxGameTime = RTFValues.GAME_TIME.get();

            if (this.flagsAvailable) {
                if (index == maxGameTime - 5 * 60) {
                    this.endingGame(5);
                } else if (index == maxGameTime - 4 * 60) {
                    this.endingGame(4);
                } else if (index == maxGameTime - 3 * 60) {
                    this.endingGame(3);
                } else if (index == maxGameTime - 2 * 60) {
                    this.endingGame(2);
                } else if (index == maxGameTime - 60) {
                    this.endingGame(1);
                } else if (index >= maxGameTime) {
                    Bukkit.getScheduler().runTask(plugin, this::destroyFlags);
                }
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
        this.players.forEach(player -> player.getPlayer().sendMessage(" \n" + RTFMessage.ENDING_GAME_MESSAGE.asString(player.getPlayer()).replace("%time%", String.valueOf(minutesLeft)) + "\n "));

        if (minutesLeft == 1) {
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 3f, 3f);
            }
        } else {
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f);
            }
        }
    }

    public void destroyFlags() {
        if (this.reconnectionTime == -1) {
            return;
        }

        this.getPlayers().forEach(player -> player.getPlayer().sendMessage(" \n" + RTFMessage.END_GAME_MESSAGE.asString(player.getPlayer()) + "\n "));
        this.reconnectionTime = -1;

        for (HyriGameTeam gameTeam : this.teams) {
            final RTFGameTeam team = (RTFGameTeam) gameTeam;

            team.getFlag().resetHolder();

            if (team.hasLife()) {
                team.getFlag().getBlocks().forEach(block -> block.setType(Material.AIR));
                team.removeLife();
            }
        }

        IHyrame.get().getScoreboardManager().getScoreboards(RTFScoreboard.class).forEach(RTFScoreboard::update);

        for (RTFGamePlayer gamePlayer : this.players) {
            if (gamePlayer.isSpectator() || gamePlayer.isDead()) {
                continue;
            }

            final Player player = gamePlayer.getPlayer();

            player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 3f, 3f);
            gamePlayer.spawn(false);

            Title.sendTitle(player.getPlayer(),
                    HyriLanguageMessage.get("title.flag-destroyed").getValue(player),
                    HyriLanguageMessage.get("subtitle.all-flags-destroyed").getValue(player),
                    5, 3 * 20, 10);
        }
    }

    public RTFGameTeam getFirstTeam() {
        return this.firstTeam;
    }

    public RTFGameTeam getSecondTeam() {
        return this.secondTeam;
    }

    private void refreshAPIPlayer(RTFGamePlayer gamePlayer) {
        final RTFData data = gamePlayer.getData();
        final RTFStatistics statistics = gamePlayer.getStatistics();
        final RTFStatistics.Data statisticsData = statistics.getData(this.getType());

        data.setLastAbility(gamePlayer.getAbility().getModel());

        if (!this.getState().isAccessible() && !HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            statisticsData.addGamesPlayed(1);
            statisticsData.addKills(gamePlayer.getKills());
            statisticsData.addFinalKills(gamePlayer.getFinalKills());
            statisticsData.addDeaths(gamePlayer.getDeaths());
            statisticsData.addCapturedFlags(gamePlayer.getCapturedFlags());
            statisticsData.addFlagsBroughtBack(gamePlayer.getFlagsBroughtBack());

            statistics.update(gamePlayer.getUniqueId());
        }

        data.update(gamePlayer.getUniqueId());
    }

    @Override
    public RTFGameType getType() {
        return (RTFGameType) super.getType();
    }

    public void setFlagsAvailable(boolean flagsAvailable) {
        this.flagsAvailable = flagsAvailable;
    }

    public RTFWaitingRoom getWaitingRoom() {
        return (RTFWaitingRoom) this.waitingRoom;
    }

}
