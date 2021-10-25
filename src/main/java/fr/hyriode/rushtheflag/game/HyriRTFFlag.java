package fr.hyriode.rushtheflag.game;

import fr.hyriode.common.title.Title;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HyriRTFFlag {

    private final HyriRTF hyriRTF;
    private final Location flagLocation;
    private final HyriGameTeam hyriGameTeam;
    private final byte data;
    public boolean flagIsTaken = false;
    public Player playerWhoTookFlag;

    public HyriRTFFlag(HyriRTF hyriRTF, HyriGameTeam hyriGameTeam) {
        this.hyriRTF = hyriRTF;
        this.flagLocation = hyriRTF.getHyriRTFconfiguration().getLocation(hyriGameTeam.getName() + HyriRTFConfiguration.FLAG_LOCATION_KEY);
        this.hyriGameTeam = hyriGameTeam;

        if(hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
            this.data = DyeColor.RED.getData();
            this.hyriRTF.setRedFlag(this);
        }else {
            this.data = DyeColor.BLUE.getData();
            this.hyriRTF.setBlueFlag(this);
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
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + " a capturé le drapeau adversaire",
                        ChatColor.YELLOW + "Il se trouve en x: " + ChatColor.GREEN + player.getLocation().getX() + ChatColor.YELLOW + " y: " + ChatColor.GREEN + player.getLocation().getY() +
                                ChatColor.YELLOW + " z: " + ChatColor.GREEN + player.getLocation().getZ() + ChatColor.YELLOW + " escortez le", 3, 40, 3);
            }else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RED + " a capturé votre drapeau", ChatColor.DARK_RED + "tuez le vite", 3, 40, 3);
            }
        }

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if (this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + " a capturé le drapeau adversaire",
                        ChatColor.YELLOW + "Il se trouve en x: " + ChatColor.GREEN + player.getLocation().getX() + ChatColor.YELLOW + " y: " + ChatColor.GREEN + player.getLocation().getY() +
                                ChatColor.YELLOW + " z: " + ChatColor.GREEN + player.getLocation().getZ() + ChatColor.YELLOW + " escortez le", 3, 40, 3);
            } else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RED + " a capturé votre drapeau", ChatColor.DARK_RED + "tuez le vite", 3, 40, 3);
            }
        }

        Title.setTitle(player, ChatColor.GOLD + "Vous avez capturé le drapeau", "Rapportez le à votre base", 3, 40, 3);
    }

    public void playerLooseFlag() {
        this.flagIsTaken = false;
        this.playerWhoTookFlag = null;

        this.enableFlag();

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if(!this.hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),ChatColor.GREEN + "Vous avez récuperé votre drapeau", "", 2, 30, 2);
            }else {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(),ChatColor.RED + "Vous avez perdu le drapeau adverse", "", 2, 30, 2);
            }
        }
        for(HyriGamePlayer hyriBlueGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
            if(this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), ChatColor.GREEN + "Vous avez récuperé votre drapeau", "", 2, 30, 2);
            }else {
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), ChatColor.RED + "Vous avez perdu le drapeau adverse", "", 2, 30, 2);
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
}
