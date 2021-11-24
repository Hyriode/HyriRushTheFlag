package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.api.RTFPlayer;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import fr.hyriode.tools.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class HyriRTFGame extends HyriGame<HyriRTFGamePlayer> {

    private final HyriRTF hyriRTF;

    private static final HyriLanguageMessage VICTORY_TITLE = new HyriLanguageMessage("victory.title").addValue(HyriLanguage.FR, ChatColor.GOLD + "VICTOIRE").addValue(HyriLanguage.EN, ChatColor.GOLD + "VICTORY");
    private static final HyriLanguageMessage VICTORY_SUB = new HyriLanguageMessage("victory.sub").addValue(HyriLanguage.FR, ChatColor.YELLOW + "Vous avez remporter la partie").addValue(HyriLanguage.EN, ChatColor.YELLOW + "You win the game");
    private static final HyriLanguageMessage DEFEAT_TITLE = new HyriLanguageMessage("loose.title").addValue(HyriLanguage.FR, ChatColor.RED + "DÉFAITE").addValue(HyriLanguage.EN, ChatColor.RED + "DEFEAT");
    private static final HyriLanguageMessage DEFEAT_SUB = new HyriLanguageMessage("loose.sub").addValue(HyriLanguage.FR, ChatColor.DARK_RED + "Vous avez perdu la partie").addValue(HyriLanguage.EN, ChatColor.DARK_RED + "You lost the game");
    private static final HyriLanguageMessage CAPTURE_ENEMY_TITLE = new HyriLanguageMessage("game.capture.enemy.title").addValue(HyriLanguage.FR, ChatColor.RED + "L'équipe adverse a capturé votre drapeau").addValue(HyriLanguage.EN, ChatColor.RED + "The opponents captured your flag");
    private static final HyriLanguageMessage CAPTURE_ENEMY_SUB = new HyriLanguageMessage("game.capture.enemy.sub").addValue(HyriLanguage.FR, ChatColor.DARK_RED + "Vous perdez une vie").addValue(HyriLanguage.EN, ChatColor.DARK_RED + "You lose a life");
    private static final HyriLanguageMessage CAPTURE_ALLY_TITLE = new HyriLanguageMessage("game.capture.ally.title").addValue(HyriLanguage.FR, ChatColor.GREEN + "Vous avez capturé le drapeau adverse").addValue(HyriLanguage.EN, ChatColor.GREEN + "You captured the opposing flag");
    private static final HyriLanguageMessage CAPTURE_ALLY_SUB = new HyriLanguageMessage("game.capture.ally.sub").addValue(HyriLanguage.FR, ChatColor.DARK_GREEN + "Vous remportez une vie supplémentaire").addValue(HyriLanguage.EN, ChatColor.DARK_GREEN + "You gain an extra life");

    private int finalKilledBlue = 0;
    private int finalKilledRed = 0;
    private boolean blueTeamCanRespawn = true;
    private boolean redTeamCanRespawn = true;
    private int bluePoints = 3;
    private int redPoints = 3;
    private Map<HyriRTFGamePlayer, HyriGamePlayingScoreboard> scoreboards;

    public HyriRTFGame(IHyrame hyrame, JavaPlugin plugin) {
        super(hyrame, plugin, "rtf", HyriRTF.RTF, HyriRTFGamePlayer.class, true);
        this.hyriRTF = (HyriRTF) plugin;

        //this.registerTabListManager();
        this.minPlayers = 2;
        this.maxPlayers = 2;
        this.registerTeams();

    }


    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.getInventory().clear();
        HyriGameItems.TEAM_CHOOSER.give(this.hyriRTF.getHyrame(), player, 0);
        HyriGameItems.LEAVE_ITEM.give(this.hyriRTF.getHyrame(), player, 8);
        player.setHealth(20);

        player.setGameMode(GameMode.ADVENTURE);
        player.setCanPickupItems(false);

        hyriRTF.getGame().getPlayer(player.getUniqueId()).setHyriRTF(this.hyriRTF);
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);
        sendPlayerAPI(hyriRTF.getGame().getPlayer(player.getUniqueId()));
    }

    @Override
    public void start() {
        super.start();

        for (HyriGameTeam team : teams) {
            team.setSpawnLocation(hyriRTF.getHyriRTFconfiguration().getLocation(team.getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY));
            team.teleportToSpawn();
            for(HyriGamePlayer hyriGamePlayer : team.getPlayers()) {
                hyriRTF.getGame().getPlayer(hyriGamePlayer.getPlayer().getPlayer().getUniqueId()).spawn();
                hyriGamePlayer.getPlayer().getPlayer().setFoodLevel(20);
                hyriGamePlayer.getPlayer().getPlayer().setSaturation(7f);
                HyriGamePlayingScoreboard hyriGamePlayingScoreboard = new HyriGamePlayingScoreboard(this.hyriRTF, hyriGamePlayer.getPlayer().getPlayer());
                scoreboards.put((HyriRTFGamePlayer) hyriGamePlayer, hyriGamePlayingScoreboard);
                hyriGamePlayingScoreboard.show();
            }
        }
        hyriRTF.setBlueFlag(new HyriRTFFlag(hyriRTF, hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName())));
        hyriRTF.setRedFlag(new HyriRTFFlag(hyriRTF, hyriRTF.getGame().getTeam(Teams.RED.getTeamName())));
    }

    private void registerTeams() {
        this.registerTeam(new HyriGameTeam(Teams.BLUE.getTeamName(), Teams.BLUE.getDisplayName(), Teams.BLUE.getColor(), Teams.BLUE.getTeamSize()));
        this.registerTeam(new HyriGameTeam(Teams.RED.getTeamName(), Teams.RED.getDisplayName(), Teams.RED.getColor(), Teams.RED.getTeamSize()));
    }

    public void captureFlag(HyriGameTeam whoCapture) {
        HyriRTFFlag capturedFlag;
        if(whoCapture.getColor().equals(HyriGameTeamColor.BLUE)) {
            capturedFlag = this.hyriRTF.getRedFlag();
        }else {
            capturedFlag = this.hyriRTF.getBlueFlag();
        }

        Player playerWhoCapture = capturedFlag.getPlayerWhoTookFlag();
        capturedFlag.playerLooseFlag();
        hyriRTF.getGame().getPlayer(playerWhoCapture.getUniqueId()).spawn();

        for(Player player : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player, "flag captured", whoCapture.getName(), 3, 45, 3);
        }


        if(whoCapture.getColor().equals(HyriGameTeamColor.BLUE)) {
            for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
                Title.sendTitle(hyriRedGamePlayer.getPlayer().getPlayer(), CAPTURE_ENEMY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), CAPTURE_ENEMY_SUB.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
                hyriRedGamePlayer.getPlayer().getPlayer().playSound(hyriRedGamePlayer.getPlayer().getPlayer().getLocation(), Sound.WOLF_DEATH, 2,2);
            }
            for(HyriGamePlayer hyriBlueGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
                Title.sendTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), CAPTURE_ALLY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), CAPTURE_ALLY_SUB.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
            }

            this.bluePoints++;
            this.redPoints--;
            this.redTeamCanRespawn = this.redPoints != 0;
            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> {
                if(!redTeamCanRespawn) {
                    capturedFlag.disableFlag();
                }else {
                    capturedFlag.enableFlag();
                }
            }, 1L);
        }else {
            for(HyriGamePlayer hyriBlueGamePlayer : this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
                Title.sendTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), CAPTURE_ENEMY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), CAPTURE_ENEMY_SUB.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
                hyriBlueGamePlayer.getPlayer().getPlayer().playSound(hyriBlueGamePlayer.getPlayer().getPlayer().getLocation(), Sound.WOLF_DEATH, 2,2);
            }
            for(HyriGamePlayer hyriRedGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
                Title.sendTitle(hyriRedGamePlayer.getPlayer().getPlayer(), CAPTURE_ALLY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), CAPTURE_ALLY_SUB.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
            }

            this.bluePoints--;
            this.redPoints++;
            this.blueTeamCanRespawn = this.bluePoints != 0;
            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> {
                if(!blueTeamCanRespawn) {
                    capturedFlag.disableFlag();
                }else {
                    capturedFlag.enableFlag();
                }
            }, 1L);
        }
    }


    public void winGame(HyriGameTeam winner) {
        HyriGameTeam looser = hyriRTF.getGame().getTeam(Teams.RED.getTeamName());
        if(winner.getColor().equals(HyriGameTeamColor.RED)) {
            looser = hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName());
        }

        hyriRTF.getGame().end();
        for(HyriGamePlayer hyriGamePlayer : winner.getPlayers()) {
            Title.sendTitle(hyriGamePlayer.getPlayer().getPlayer(),VICTORY_TITLE.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), VICTORY_SUB.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), 5, 70, 5);
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.CREATIVE);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
        for(HyriGamePlayer hyriGamePlayer : looser.getPlayers()) {
            Title.sendTitle(hyriGamePlayer.getPlayer().getPlayer() ,DEFEAT_TITLE.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), DEFEAT_SUB.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), 5, 70, 10);
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.SPECTATOR);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
    }

    public void sendPlayerAPI(HyriRTFGamePlayer player) {
        if(hyriRTF.getRtfPlayerManager().getPlayer(player.getUUID()) != null) {
            RTFPlayer rtfPlayer = hyriRTF.getRtfPlayerManager().getPlayer(player.getUUID());
            rtfPlayer.setDeaths(player.getDeath() + rtfPlayer.getDeaths());
            rtfPlayer.setKills(player.getKills() + rtfPlayer.getKills());
            rtfPlayer.setFinalKills(player.getFinalKills() + rtfPlayer.getFinalKills());
            rtfPlayer.setPlayTime(this.scoreboards.get(player).getCurrentIGSeconds() + rtfPlayer.getPlayTime());
            rtfPlayer.setGamesPlayed(rtfPlayer.getGamesPlayed() + 1);
        }

    }

    public int getBluePoints() {
        return this.bluePoints;
    }

    public boolean isBlueTeamCanRespawn() {
        return this.blueTeamCanRespawn;
    }

    public boolean isRedTeamCanRespawn() {
        return this.redTeamCanRespawn;
    }

    public int getFinalKilledBlue() {
        return finalKilledBlue;
    }

    public void setFinalKilledBlue(int finalKilledBlue) {
        this.finalKilledBlue = finalKilledBlue;
    }

    public int getFinalKilledRed() {
        return finalKilledRed;
    }

    public void setFinalKilledRed(int finalKilledRed) {
        this.finalKilledRed = finalKilledRed;
    }
}
