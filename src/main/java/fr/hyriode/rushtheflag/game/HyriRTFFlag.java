package fr.hyriode.rushtheflag.game;

import fr.hyriode.common.title.Title;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HyriRTFFlag {

    private static final LanguageMessage CAPTURE_ALLY_TITLE = new LanguageMessage("flag.capture.ally.title").addValue(Language.FR, ChatColor.GREEN + " a capturé le drapeau adversaire").addValue(Language.EN, ChatColor.GREEN + " capture the enemy flag");
    private static final LanguageMessage CAPTURE_ALLY_SUB1 = new LanguageMessage("flag.capture.ally.sub1").addValue(Language.FR, ChatColor.YELLOW + "Il se trouve en ").addValue(Language.EN, ChatColor.YELLOW + " He is in ");
    private static final LanguageMessage CAPTURE_ALLY_SUB2 = new LanguageMessage("flag.capture.ally.sub2").addValue(Language.FR, ChatColor.YELLOW + " escortez le").addValue(Language.EN, ChatColor.YELLOW + "escort him");
    private static final LanguageMessage CAPTURE_ENEMY_TILE = new LanguageMessage("flag.capture.enemy.title").addValue(Language.FR, ChatColor.RED + " a capturé votre drapeau").addValue(Language.EN,ChatColor.RED + " captured your flag");
    private static final LanguageMessage CAPTURE_ENEMY_SUB = new LanguageMessage("flag.capture.enemy.sub").addValue(Language.FR, ChatColor.DARK_RED + "tuez le vite").addValue(Language.EN,ChatColor.DARK_RED + "kill him quickly");
    private static final LanguageMessage CAPTURE_TITLE = new LanguageMessage("flag.capture.title").addValue(Language.FR, ChatColor.GOLD + "Vous avez capturé le drapeau").addValue(Language.EN, ChatColor.GOLD + "You captured the flag");
    private static final LanguageMessage CAPTURE_SUB = new LanguageMessage("flag.capture.sub").addValue(Language.FR,ChatColor.YELLOW + "Rapportez le à votre base").addValue(Language.EN, ChatColor.YELLOW + "Go quickly to your base");
    private static final LanguageMessage LOOSE_ALLY_TITLE = new LanguageMessage("flag.loose.ally.title").addValue(Language.FR, ChatColor.GREEN + "Vous avez récuperé votre drapeau").addValue(Language.EN, ChatColor.GREEN +  "You got your flag back");
    private static final LanguageMessage LOOSE_ENEMY_TITLE = new LanguageMessage("flag.loose.enemy.title").addValue(Language.FR, ChatColor.RED + "Vous avez perdu le drapeau adverse").addValue(Language.EN, ChatColor.RED + "You lost the enemy flag");

    private final HyriRTF hyriRTF;
    private final Location flagLocation;
    private final HyriGameTeam hyriGameTeam;
    private final byte data;
    private boolean flagIsTaken;
    private Player playerWhoTookFlag;

    public HyriRTFFlag(HyriRTF hyriRTF, HyriGameTeam hyriGameTeam) {
        this.hyriRTF = hyriRTF;
        this.flagLocation = hyriRTF.getHyriRTFconfiguration().getLocation(hyriGameTeam.getName() + HyriRTFConfiguration.FLAG_LOCATION_KEY);
        this.hyriGameTeam = hyriGameTeam;
        this.flagIsTaken = false;

        if(hyriGameTeam.getName().equalsIgnoreCase(Teams.RED.getTeamName())) {
            this.data = DyeColor.RED.getData();
        }else {
            this.data = DyeColor.BLUE.getData();
        }

        this.enableFlag();
    }

    public void playerTakeFlag(Player player) {
        this.flagIsTaken = true;
        this.playerWhoTookFlag = player;

        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack( Material.WOOL, 64*9, this.data));
        player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.data));
        this.disableFlag();

        player.setGameMode(GameMode.ADVENTURE);

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
            if (this.hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.DARK_PURPLE + player.getName() + CAPTURE_ALLY_TITLE.getForPlayer(player), CAPTURE_ALLY_SUB1.getForPlayer(player) + this.location(player) + CAPTURE_ALLY_SUB2.getForPlayer(player), 3, 40, 3);
            }else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),ChatColor.LIGHT_PURPLE + player.getName() + CAPTURE_ENEMY_TILE.getForPlayer(player), CAPTURE_ENEMY_SUB.getForPlayer(player), 3, 40, 3);
            }
        }

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if (this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
            Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + CAPTURE_ALLY_TITLE.getForPlayer(player), CAPTURE_ALLY_SUB1.getForPlayer(player) + this.location(player) + CAPTURE_ALLY_SUB2.getForPlayer(player), 3, 40, 3);
            } else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.LIGHT_PURPLE + player.getName() + CAPTURE_ENEMY_TILE.getForPlayer(player), CAPTURE_ENEMY_SUB.getForPlayer(player), 3, 40, 3);
            }
        }

        Title.setTitle(player, CAPTURE_TITLE.getForPlayer(player), CAPTURE_SUB.getForPlayer(player), 3, 40, 3);
    }

    private String location(Player player) {
        return ChatColor.GOLD + " x: " + ChatColor.GREEN + player.getLocation().getX() + ChatColor.GOLD + " y: " + ChatColor.GREEN + player.getLocation().getY() + ChatColor.GOLD + " z: " + ChatColor.GREEN + player.getLocation().getZ();
    }

    public void playerLooseFlag() {
        this.flagIsTaken = false;
        this.playerWhoTookFlag = null;

        this.enableFlag();

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if(!this.hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),LOOSE_ALLY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), "", 2, 30, 2);
            }else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),LOOSE_ENEMY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), "", 2, 30, 2);
            }
        }
        for(HyriGamePlayer hyriBlueGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
            if(this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), LOOSE_ALLY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), "", 2, 30, 2);
            }else {
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), LOOSE_ENEMY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), "", 2, 30, 2);
            }
        }
    }

    public Location getFlagLocation() {
        return this.flagLocation;
    }

    public void enableFlag() {
        this.flagLocation.getBlock().setType(Material.WOOL);
        this.flagLocation.getBlock().setData(this.data);
    }

    public void disableFlag() {
        this.flagLocation.getBlock().setType(Material.AIR);
    }

    public boolean isFlagTaken() {
        return this.flagIsTaken;
    }

    public Player getPlayerWhoTookFlag() {
        return playerWhoTookFlag;
    }
}
