package fr.hyriode.rtf.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
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
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.api.statistics.HyriRTFStatistics;
import fr.hyriode.rtf.config.RTFConfig;
import fr.hyriode.rtf.game.ablity.RTFAbility;
import fr.hyriode.rtf.game.items.RTFChooseAbilityItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class RTFGame extends HyriGame<RTFGamePlayer> {

    private final HyriWaitingRoom waitingRoom;

    private RTFGameTeam firstTeam;
    private RTFGameTeam secondTeam;

    private final Location spawn;
    private final HyriRTF plugin;

    private boolean flagsAvailable;

    public RTFGame(IHyrame hyrame, HyriRTF plugin) {
        super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("rushtheflag"), RTFGamePlayer.class, HyriGameType.getFromData(RTFGameType.values()));
        this.plugin = plugin;

        this.description = HyriLanguageMessage.get("message.game.description");

        this.reconnectionTime = 120;

        this.waitingRoom = new HyriWaitingRoom(this, Material.BANNER, this.plugin.getConfiguration().getWaitingRoom());
        this.waitingRoom.setup();

        this.spawn = this.waitingRoom.getConfig().getSpawn().asBukkit();

        this.registerTeams();
        this.flagsAvailable = true;
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
    }

    @Override
    public void start() {
        this.waitingRoom.remove();

        super.start();

        this.firstTeam.getFlag().place();
        this.secondTeam.getFlag().place();

        for (RTFGamePlayer player : this.players) {
            player.startGame();
        }

        final HyriDeathProtocol.Options.YOptions yOptions = new HyriDeathProtocol.Options.YOptions(this.plugin.getConfiguration().getArea().asArea().getMin().getY());

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 15 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            final Player player = gamePlayer.getPlayer();

            player.teleport(this.spawn);

            return ((RTFGamePlayer) gamePlayer).kill();
        }, this.createDeathScreen(), HyriDeathProtocol.ScreenHandler.Default.class).withOptions(new HyriDeathProtocol.Options().withYOptions(yOptions)));
        this.protocolManager.enableProtocol(new HyriAntiSpawnKillProtocol(this.hyrame, new HyriAntiSpawnKillProtocol.Options(2 * 20, true)));

        this.handleEndGame();
    }

    private HyriDeathProtocol.Screen createDeathScreen() {
        return new HyriDeathProtocol.Screen(5, player -> {
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

        HyriRTFPlayer account = this.plugin.getAPI().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            account = new HyriRTFPlayer(uuid);
        }

        HyriRTFStatistics statistics = this.plugin.getAPI().getStatisticsManager().getStatistics(uuid);

        if (statistics == null) {
            statistics = new HyriRTFStatistics();
        }

        gamePlayer.setStatistics(statistics);
        gamePlayer.setAccount(account);
        gamePlayer.setCooldown(false);

        final Optional<RTFAbility> ability = RTFAbility.getWithModel(account.getLastAbility());

        ability.ifPresent(gamePlayer::setAbility);

        this.hyrame.getItemManager().giveItem(player, 4, RTFChooseAbilityItem.class);

        player.teleport(this.spawn);
        player.sendMessage(RTFMessage.LAST_ABILITY_MESSAGE.asString(player).replace("%ability%", gamePlayer.getAbility().getName(player)));
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final RTFGamePlayer gamePlayer = this.getPlayer(uuid);

        this.refreshAPIPlayer(gamePlayer);

        super.handleLogout(player);

        if(this.getState() == HyriGameState.PLAYING) {
            if(!gamePlayer.getTeam().hasPlayersPlaying()) {
                this.win(gamePlayer.getTeam().getOppositeTeam());
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
            if (winner.contains(gamePlayer.getUUID())) {
                gamePlayer.getStatistics().addVictories(1);
            }

            this.refreshAPIPlayer(gamePlayer);

            final Player player = gamePlayer.getPlayer();
            final List<String> killsLines = new ArrayList<>();
            final List<RTFGamePlayer> topKillers = new ArrayList<>(this.players);

            topKillers.sort((o1, o2) -> (int) (o2.getKills() - o1.getKills()));

            for (int i = 0; i <= 2; i++) {
                final RTFGamePlayer endPlayer = topKillers.size() > i ? topKillers.get(i) : null;
                final String line = HyriLanguageMessage.get("message.game.end.kills").getForPlayer(player).replace("%position%", HyriLanguageMessage.get("message.game.end." + (i + 1)).getForPlayer(player));

                if (endPlayer == null) {
                    killsLines.add(line.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getForPlayer(player))
                            .replace("%kills%", "0"));
                    continue;
                }

                final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(endPlayer.getUUID());

                killsLines.add(line.replace("%player%", account.hasNickname() ? account.getNickname().getName() : account.getNameWithRank())
                        .replace("%kills%", String.valueOf(endPlayer.getKills())));
            }

            final int kills = (int) gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayedTime(), isWinner);
            final long xp = HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayedTime(), isWinner);
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

    public void handleEndGame() {
        this.timer.setOnTimeChanged(index -> {
            if(flagsAvailable) {
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
            sendMessageToAll(player -> " \n " + RTFMessage.ENDING_GAME_MESSAGE.asString(player)
                    .replace("%time%", "1")
                    .replace("minutes", "minute")
                    + " \n "
            );
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 3f, 3f);
            }
        } else {
            sendMessageToAll(player -> " \n " + RTFMessage.ENDING_GAME_MESSAGE.asString(player)
                    .replace("%time%", String.valueOf(minutesLeft))
                    + " \n "
            );
            for (RTFGamePlayer player : players) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f);
            }
        }
    }

    public void destroyFlags() {
        this.sendMessageToAll(player -> " \n " + RTFMessage.END_GAME_MESSAGE.asString(player) + " \n ");
        this.reconnectionTime = -1;

        for (HyriGameTeam gameTeam : this.teams) {
            RTFGameTeam team = (RTFGameTeam) gameTeam;
            team.getFlag().resetHolder();
            if(team.hasLife()) {
                team.getFlag().getBlock().setType(Material.AIR);
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
        final HyriRTFPlayer account = gamePlayer.getAccount();
        final HyriRTFStatistics statistics = gamePlayer.getStatistics();

        account.setLastAbility(gamePlayer.getAbility().getModel());

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING) {
            gamePlayer.getScoreboard().hide();

            statistics.setPlayedTime(statistics.getPlayedTime() + gamePlayer.getPlayedTime());
            statistics.addGamesPlayed(1);
            statistics.addKills(gamePlayer.getKills());
            statistics.addFinalKills(gamePlayer.getFinalKills());
            statistics.addDeaths(gamePlayer.getDeaths());
            statistics.addCapturedFlags(gamePlayer.getCapturedFlags());
            statistics.addFlagsBroughtBack(gamePlayer.getFlagsBroughtBack());

            this.plugin.getAPI().getStatisticsManager().sensStatistics(gamePlayer.getUUID(), statistics);
        }
        this.plugin.getAPI().getPlayerManager().sendPlayer(account);
    }

    @Override
    public RTFGameType getType() {
        return (RTFGameType) super.getType();
    }

    public void setFlagsAvailable(boolean flagsAvailable) {
        this.flagsAvailable = flagsAvailable;
    }
}
