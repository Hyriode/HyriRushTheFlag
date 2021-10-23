package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.rushtheflag.HyriRTF;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyriRTFMethods {

    private final HyriRTF hyriRTF;
    private boolean blueTeamCanRespawn = true;
    private boolean redTeamCanRespawn = true;
    private int bluePoints = 3;
    private int redPoints = 3;
    private int finalKilledBlue = 0;
    private int finalKilledRed = 0;

    private final List<ItemStack> armor = Arrays.asList(
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_HELMET)
    );

    public HyriRTFMethods(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    public void killPlayer(Player player) {
        HyriGamePlayer hyriGamePlayer = hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(player.getUniqueId());
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        boolean playerCanRespawn = true;

        if (hyriGamePlayer.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
            if (!blueTeamCanRespawn) {
                playerCanRespawn = false;
            }
        } else {
            if (!redTeamCanRespawn) {
                playerCanRespawn = false;
            }
        }

        if(hyriRTF.getBlueFlag().flagIsTaken) {
            if(hyriRTF.getBlueFlag().playerWhoTookFlag.equals(player)) {
                hyriRTF.getBlueFlag().playerLooseFlag();
            }
        }

        if(hyriRTF.getRedFlag().flagIsTaken) {
            if(hyriRTF.getRedFlag().playerWhoTookFlag.equals(player)) {
                hyriRTF.getRedFlag().playerLooseFlag();
            }
        }

        if(playerCanRespawn) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(player.getWorld().getSpawnLocation().add(0, 20, 0));

            player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.YELLOW + "respawn dans " + ChatColor.RED + 5 + ChatColor.YELLOW + " secondes");

            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> player.teleport(hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + ".spawnLocation")), 20L*5);

            for(int i = 4; i > 0; i--) {
                int finalI = 5-i;
                Bukkit.getScheduler().runTaskLater(hyriRTF, () -> player.sendTitle(ChatColor.RED + "Vous êtes mort", ChatColor.YELLOW + "respawn dans " + ChatColor.RED + finalI + ChatColor.YELLOW + " secondes"), 20L*i);
            }

            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> spawnPlayer(hyriGamePlayer), 20L*5);
        }else {
            player.sendTitle(ChatColor.RED + "Game terminée", ChatColor.YELLOW + "Vous ne pouvez plus respawn");
            player.getInventory().clear();
            player.getInventory().setItem(4, HyriGameItems.LEAVE.apply(player));
            hyriGamePlayer.setSpectator(true);
            if(hyriGamePlayer.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
                this.finalKilledBlue++;
                if(hyriGamePlayer.getTeam().getPlayers().size() == finalKilledBlue) {
                    this.winGame(hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.RED.getTeamName()));
                }
            }else {
                this.finalKilledRed++;
                if(hyriGamePlayer.getTeam().getPlayers().size() == finalKilledRed) {
                    this.winGame(hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.BLUE.getTeamName()));
                }
            }

        }
    }

    public void spawnPlayer(HyriGamePlayer hyriGamePlayer) {
        Player player = hyriGamePlayer.getPlayer().getPlayer();

        player.teleport(hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + ".spawnLocation"));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setCanPickupItems(false);

        hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = sword.getItemMeta();
        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
        sword.setItemMeta(itemMeta);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta itemMeta1 = pickaxe.getItemMeta();
        itemMeta1.addEnchant(Enchantment.DIG_SPEED, 2, false);
        pickaxe.setItemMeta(itemMeta1);

        player.getInventory().addItem(new ItemStack(Material.SANDSTONE,64*9));

        player.getInventory().setItem(hyriRTF.getHyriRTFconfiguration().swordSlot(player), sword);
        player.getInventory().setItem(hyriRTF.getHyriRTFconfiguration().gappleSlot(player), new ItemStack(Material.GOLDEN_APPLE, 64));
        player.getInventory().setItem(hyriRTF.getHyriRTFconfiguration().pickaxeSlot(player), pickaxe);

        ArrayList<ItemStack> dyedArmor = new ArrayList<>();

        for(ItemStack itemStack : this.armor) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            if(hyriGamePlayer.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
            leatherArmorMeta.setColor(Color.BLUE);
            }else {
                leatherArmorMeta.setColor(Color.RED);
            }
            leatherArmorMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, false);
            itemStack.setItemMeta(leatherArmorMeta);
            dyedArmor.add(itemStack);
        }

        player.getInventory().setArmorContents(dyedArmor.toArray(new ItemStack[0]));
    }

    public void captureFlag(HyriGameTeam whoCapture) {
        HyriRTFFlag capturedFlag;
        if(whoCapture.getColor().equals(HyriGameTeamColor.BLUE)) {
            capturedFlag = this.hyriRTF.getRedFlag();
        }else {
            capturedFlag = this.hyriRTF.getBlueFlag();
        }

        Player playerWhoCapture = capturedFlag.playerWhoTookFlag;
        capturedFlag.playerLooseFlag();
        this.spawnPlayer(hyriRTF.getHyrame().getGameManager().getCurrentGame().getPlayer(playerWhoCapture.getUniqueId()));

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("flag captured", whoCapture.getName());
        }

        if(whoCapture.getColor().equals(HyriGameTeamColor.BLUE)) {
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

        Scoreboard scoreboard = this.refreshScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public Scoreboard refreshScoreboard() {
        final Scoreboard scoreboard =  Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("general", "dummy");
        objective.setDisplayName(ChatColor.DARK_BLUE + "Hyriode");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final List<String> strings = Arrays.asList(
                ChatColor.BLUE + "hyriode.fr",
                " ",
                ChatColor.GREEN + "Partie en cours",
                "  ",
                ChatColor.BOLD + "" + ChatColor.BLUE + Teams.BLUE.getTeamName() + ChatColor.BLUE + " : " + hyriRTF.getHyriRTFMethods().getBluePoints(),
                ChatColor.BOLD + "" + ChatColor.RED + Teams.RED.getTeamName() + ChatColor.RED + " : " + (6 - hyriRTF.getHyriRTFMethods().getBluePoints()),
                ChatColor.UNDERLINE + "" + ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "Teams :",
                "   ",
                ChatColor.GRAY + "RTF par Hyriode"
        );

        for(String string : strings) {
            final Score score = objective.getScore(string);
            score.setScore(strings.indexOf(string));
        }

        return scoreboard;
    }

    public int getBluePoints() {
        return this.bluePoints;
    }

    public void winGame(HyriGameTeam winner) {
        HyriGameTeam looser = hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.RED.getTeamName());
        if(winner.getColor().equals(HyriGameTeamColor.RED)) {
            looser = hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.BLUE.getTeamName());
        }

        hyriRTF.getHyrame().getGameManager().getCurrentGame().setState(HyriGameState.ENDED);
        for(HyriGamePlayer hyriGamePlayer : winner.getPlayers()) {
            hyriGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.GOLD + "VICTOIRE", ChatColor.YELLOW + "Vous avez remporter la partie");
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.CREATIVE);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
        for(HyriGamePlayer hyriGamePlayer : looser.getPlayers()) {
            hyriGamePlayer.getPlayer().getPlayer().sendTitle(ChatColor.RED + "DÉFAITE", ChatColor.DARK_RED + "Vous avez perdu la partie");
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.SPECTATOR);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
    }
}
