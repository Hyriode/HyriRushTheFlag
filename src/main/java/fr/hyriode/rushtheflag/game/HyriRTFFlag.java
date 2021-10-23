package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.rushtheflag.HyriRTF;
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
        this.flagLocation = hyriRTF.getHyriRTFconfiguration().getLocation(hyriGameTeam.getName() + ".flagLocation");
        this.hyriGameTeam = hyriGameTeam;

        if(hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
            this.data = DyeColor.RED.getData();
            this.hyriRTF.setRedFlag(this);
        }else {
            this.data = DyeColor.BLUE.getData();
            this.hyriRTF.setBlueFLag(this);
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

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
            if (this.hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + " a capturé le drapeau adversaire",
                        ChatColor.YELLOW + "Il se trouve en x: " + ChatColor.GREEN + player.getLocation().getX() + ChatColor.YELLOW + " y: " + ChatColor.GREEN + player.getLocation().getY() +
                                ChatColor.YELLOW + " z: " + ChatColor.GREEN + player.getLocation().getZ() + ChatColor.YELLOW + " escortez le");
            }else {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RED + " a capturé votre drapeau", ChatColor.DARK_RED + "tuez le vite");
            }
        }

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if (this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.DARK_PURPLE + player.getName() + ChatColor.GREEN + " a capturé le drapeau adversaire",
                        ChatColor.YELLOW + "Il se trouve en x: " + ChatColor.GREEN + player.getLocation().getX() + ChatColor.YELLOW + " y: " + ChatColor.GREEN + player.getLocation().getY() +
                                ChatColor.YELLOW + " z: " + ChatColor.GREEN + player.getLocation().getZ() + ChatColor.YELLOW + " escortez le");
            } else {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RED + " a capturé votre drapeau", ChatColor.DARK_RED + "tuez le vite");
            }
        }

        player.sendTitle(ChatColor.GOLD + "Vous avez capturé le drapeau", "Rapportez le à votre base");
    }

    public void playerLooseFlag() {
        this.flagIsTaken = false;
        this.playerWhoTookFlag = null;

        this.enableFlag();

        for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
            if(!this.hyriGameTeam.getColor().equals(HyriGameTeamColor.RED)) {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.GREEN + "Vous avez récuperé votre drapeau", "");
            }else {
                hyriRedGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.RED + "Vous avez perdu le drapeau adverse", "");
            }
        }
        for(HyriGamePlayer hyriBlueGamePlayer :  this.hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
            if(this.hyriGameTeam.getColor().equals(HyriGameTeamColor.BLUE)) {
                hyriBlueGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.GREEN + "Vous avez récuperé votre drapeau", "");
            }else {
                hyriBlueGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.RED + "Vous avez perdu le drapeau adverse", "");
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
