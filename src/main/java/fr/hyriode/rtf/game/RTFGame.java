package fr.hyriode.rtf.game;

import fr.hyriode.api.HyriAPI;
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
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.player.RTFPlayer;
import fr.hyriode.rtf.api.statistics.RTFStatistics;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.item.RTFChooseAbilityItem;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import fr.hyriode.rtf.game.team.RTFTeam;
import fr.hyriode.rtf.util.RTFMessage;
import fr.hyriode.rtf.util.RTFValues;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("rushtheflag"), RTFGamePlayer.class, HyriGameType.getFromData(RTFGameType.values()));
        this.plugin = plugin;
        this.description = HyriLanguageMessage.get("message.game.description");
        this.reconnectionTime = 120;
        this.waitingRoom = new RTFWaitingRoom(this);
        this.flagsAvailable = true;

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
    public void start() {
        super.start();

        this.firstTeam.getFlag().place();
        this.secondTeam.getFlag().place();
        this.firstTeam.setLives(RTFValues.LIVES.get());
        this.secondTeam.setLives(RTFValues.LIVES.get());

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

        gamePlayer.setPlugin(this.plugin);

        final RTFPlayer account = RTFPlayer.getPlayer(uuid);
        final RTFStatistics statistics = RTFStatistics.get(uuid);

        gamePlayer.setStatistics(statistics);
        gamePlayer.setAccount(account);
        gamePlayer.setCooldown(false);

        RTFAbility.getWithModel(account.getLastAbility()).filter(RTFAbility::isEnabled).ifPresent(gamePlayer::setAbility);

        this.hyrame.getItemManager().giveItem(player, 4, RTFChooseAbilityItem.class);

        player.sendMessage(RTFMessage.LAST_ABILITY_MESSAGE.asString(player).replace("%ability%", gamePlayer.getAbility().getName(player)));
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);

        this.refreshAPIPlayer(gamePlayer);

        if (this.getState() == HyriGameState.PLAYING) {
            if (!gamePlayer.getTeam().hasPlayersPlaying()) {
                this.win(((RTFGameTeam) gamePlayer.getTeam()).getOppositeTeam());
            }
        }
    }

    @Override
    public void win(HyriGameTeam winner) {
        for (RTFGamePlayer gamePlayer : this.players) {
            gamePlayer.getScoreboard().update();
        }

        super.win(winner);

        if (winner == null || this.getState() != HyriGameState.ENDED) {
            return;
        }

        for (RTFGamePlayer gamePlayer : this.players) {
            if (winner.contains(gamePlayer.getUniqueId())) {
                gamePlayer.getStatistics().getData(this.getType()).addVictories(1);
            }

            this.refreshAPIPlayer(gamePlayer);

            final Player player = gamePlayer.getPlayer();
            final List<String> killsLines = new ArrayList<>();
            final List<RTFGamePlayer> topKillers = new ArrayList<>(this.players);

            topKillers.sort((o1, o2) -> (int) (o2.getKills() - o1.getKills()));

            for (int i = 0; i <= 2; i++) {
                final RTFGamePlayer endPlayer = topKillers.size() > i ? topKillers.get(i) : null;
                final String line = HyriLanguageMessage.get("message.game.end.kills").getValue(player).replace("%position%", HyriLanguageMessage.get("message.game.end." + (i + 1)).getValue(player));

                if (endPlayer == null) {
                    killsLines.add(line.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player))
                            .replace("%kills%", "0"));
                    continue;
                }

                final IHyriPlayerSession session = IHyriPlayerSession.get(endPlayer.getUniqueId());

                killsLines.add(line
                        .replace("%player%", session.hasNickname() ? session.getNickname().getName() : session.getNameWithRank())
                        .replace("%kills%", String.valueOf(endPlayer.getKills())));
            }

            final int kills = (int) gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner);
            final double xp = HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner);
            final List<String> rewards = new ArrayList<>();

            rewards.add(ChatColor.LIGHT_PURPLE + String.valueOf(hyris) + " Hyris");
            rewards.add(ChatColor.GREEN + String.valueOf(xp) + " XP");

            final IHyriPlayer account = gamePlayer.asHyriode();

            account.getHyris().add(hyris).withMessage(false).exec();
            account.getNetworkLeveling().addExperience(xp);
            account.update();

            player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, killsLines, rewards));
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
            RTFGameTeam team = (RTFGameTeam) gameTeam;
            team.getFlag().resetHolder();
            if(team.hasLife()) {
                team.getFlag().getBlocks().forEach(block -> block.setType(Material.AIR));
                team.removeLife();
            }
        }

        for (RTFGamePlayer player : players) {
            player.getScoreboard().update();

            if (player.isSpectator() || player.isDead()) {
                continue;
            }

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
        final RTFPlayer account = gamePlayer.getAccount();
        final RTFStatistics statistics = gamePlayer.getStatistics();
        final RTFStatistics.Data data = statistics.getData(this.getType());

        account.setLastAbility(gamePlayer.getAbility().getModel());

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING && !HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            data.setPlayedTime(data.getPlayedTime() + gamePlayer.getPlayTime());
            data.addGamesPlayed(1);
            data.addKills(gamePlayer.getKills());
            data.addFinalKills(gamePlayer.getFinalKills());
            data.addDeaths(gamePlayer.getDeaths());
            data.addCapturedFlags(gamePlayer.getCapturedFlags());
            data.addFlagsBroughtBack(gamePlayer.getFlagsBroughtBack());

            statistics.update(account.getUniqueId());
        }

        RTFPlayer.updatePlayer(account);
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
