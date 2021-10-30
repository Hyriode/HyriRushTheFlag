package fr.hyriode.rushtheflag.game;

import fr.hyriode.common.item.ItemBuilder;
import fr.hyriode.common.item.ItemNBT;
import fr.hyriode.common.title.Title;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyriRTFMethods {

    private static final LanguageMessage GAME_OVER_TITLE = (new LanguageMessage("game.over.title")).addValue(Language.FR, ChatColor.RED + "Game terminée").addValue(Language.EN, "Game over");
    private static final LanguageMessage GAME_OVER_SUB = (new LanguageMessage("game.over.sub")).addValue(Language.FR, ChatColor.YELLOW + "Vous ne pouvez plus respawn").addValue(Language.EN, ChatColor.YELLOW + "You can't respawn");
    private static final LanguageMessage GAME_RESPAWN_TITLE = (new LanguageMessage("game.respawn.title")).addValue(Language.FR, ChatColor.RED + "Tu es mort").addValue(Language.EN, ChatColor.RED + "You died");
    private static final LanguageMessage GAME_RESPAWN_SUB_1 = (new LanguageMessage("game.respawn.sub1")).addValue(Language.FR, ChatColor.YELLOW + "respawn dans ").addValue(Language.EN, ChatColor.YELLOW + "respawn in ");
    private static final LanguageMessage GAME_RESPAWN_SUB_2 = (new LanguageMessage("game.respawn.sub2")).addValue(Language.FR, ChatColor.YELLOW + " secondes").addValue(Language.EN, ChatColor.YELLOW + " seconds");
    private static final LanguageMessage VICTORY_TITLE = new LanguageMessage("victory.title").addValue(Language.FR, ChatColor.GOLD + "VICTOIRE").addValue(Language.EN, ChatColor.GOLD + "VICTORY");
    private static final LanguageMessage VICTORY_SUB = new LanguageMessage("victory.sub").addValue(Language.FR, ChatColor.YELLOW + "Vous avez remporter la partie").addValue(Language.EN, ChatColor.YELLOW + "You win the game");
    private static final LanguageMessage DEFEAT_TITLE = new LanguageMessage("loose.title").addValue(Language.FR, ChatColor.RED + "DÉFAITE").addValue(Language.EN, ChatColor.RED + "DEFEAT");
    private static final LanguageMessage DEFEAT_SUB = new LanguageMessage("loose.sub").addValue(Language.FR, ChatColor.DARK_RED + "Vous avez perdu la partie").addValue(Language.EN, ChatColor.DARK_RED + "You lost the game");
    private static final LanguageMessage CAPTURE_ENEMY_TITLE = (new LanguageMessage("game.capture.enemy.title")).addValue(Language.FR, ChatColor.RED + "L'équipe adverse a capturé votre drapeau").addValue(Language.EN, ChatColor.RED + "The opponents captured your flag");
    private static final LanguageMessage CAPTURE_ENEMY_SUB = (new LanguageMessage("game.capture.enemy.sub")).addValue(Language.FR, ChatColor.DARK_RED + "Vous perdez une vie").addValue(Language.EN, ChatColor.DARK_RED + "You lose a life");
    private static final LanguageMessage CAPTURE_ALLY_TITLE = (new LanguageMessage("game.capture.ally.title")).addValue(Language.FR, ChatColor.GREEN + "Vous avez capturé le drapeau adverse").addValue(Language.EN, ChatColor.GREEN + "You captured the opposing flag");
    private static final LanguageMessage CAPTURE_ALLY_SUB = (new LanguageMessage("game.capture.ally.sub")).addValue(Language.FR, ChatColor.DARK_GREEN + "Vous remportez une vie supplémentaire").addValue(Language.EN, ChatColor.DARK_GREEN + "You gain an extra life");

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
        HyriGamePlayer hyriGamePlayer = hyriRTF.getGame().getPlayer(player.getUniqueId());
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

        if(hyriRTF.getBlueFlag().isFlagTaken()) {
            if(hyriRTF.getBlueFlag().getPlayerWhoTookFlag().equals(player)) {
                hyriRTF.getBlueFlag().playerLooseFlag();
            }
        }

        if(hyriRTF.getRedFlag().isFlagTaken()) {
            if(hyriRTF.getRedFlag().getPlayerWhoTookFlag().equals(player)) {
                hyriRTF.getRedFlag().playerLooseFlag();
            }
        }

        if(playerCanRespawn) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(player.getWorld().getSpawnLocation().add(0, 20, 0));

            Title.setTitle(player ,GAME_RESPAWN_TITLE.getForPlayer(player), GAME_RESPAWN_SUB_1.getForPlayer(player) + ChatColor.RED + 5 + GAME_RESPAWN_SUB_2.getForPlayer(player), 0, 20,0);

            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> player.teleport(hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY)), 20L*5);

            for(int i = 4; i > 0; i--) {
                int finalI = 5-i;
                Bukkit.getScheduler().runTaskLater(hyriRTF, () -> Title.setTitle(player,GAME_RESPAWN_TITLE.getForPlayer(player), GAME_RESPAWN_SUB_1.getForPlayer(player) + ChatColor.RED + finalI + GAME_RESPAWN_SUB_2.getForPlayer(player), 0, 20, 0), 20L*i);
            }

            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> spawnPlayer(hyriGamePlayer), 20L*5);
        }else {
            Title.setTitle(player,GAME_OVER_TITLE.getForPlayer(player), GAME_OVER_SUB.getForPlayer(player), 5, 70, 5);
            player.getInventory().clear();
            player.getInventory().setItem(4, HyriGameItems.LEAVE.apply(player));
            hyriGamePlayer.setSpectator(true);
            if(hyriGamePlayer.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
                this.finalKilledBlue++;
                if(hyriGamePlayer.getTeam().getPlayers().size() == finalKilledBlue) {
                    this.winGame(hyriRTF.getGame().getTeam(Teams.RED.getTeamName()));
                }
            }else {
                this.finalKilledRed++;
                if(hyriGamePlayer.getTeam().getPlayers().size() == finalKilledRed) {
                    this.winGame(hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()));
                }
            }

        }

        int swordSlot = 0;
        int gapSlot = 1;
        int pickSlot = 2;

        if(!player.getInventory().getItem(0).getType().equals(Material.IRON_SWORD)) {
            for (int i = 1; i < 9; i++) {
                if(player.getInventory().getItem(i).getType().equals(Material.IRON_SWORD)) {
                    swordSlot = i;
                    break;
                }
            }
        }

        if(!player.getInventory().getItem(1).getType().equals(Material.GOLDEN_APPLE)) {
            for (int i = 1; i < 9; i++) {
                if(player.getInventory().getItem(i).getType().equals(Material.GOLDEN_APPLE)) {
                    gapSlot = i;
                    break;
                }
            }
        }

        if(!player.getInventory().getItem(1).getType().equals(Material.IRON_PICKAXE)) {
            for (int i = 1; i < 9; i++) {
                if(player.getInventory().getItem(i).getType().equals(Material.IRON_PICKAXE)) {
                    pickSlot = i;
                    break;
                }
            }
        }

        hyriRTF.getHyriRTFconfiguration().setHotbar(player, swordSlot, gapSlot, pickSlot);
    }


    public void spawnPlayer(HyriGamePlayer hyriGamePlayer) {
        Player player = hyriGamePlayer.getPlayer().getPlayer();

        player.teleport(hyriRTF.getHyriRTFconfiguration().getLocation(hyriGamePlayer.getTeam().getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setCanPickupItems(false);

        hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();

        final ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = sword.getItemMeta();
        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
        sword.setItemMeta(itemMeta);

        final ItemStack pickaxe = new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .nbt().setBoolean("RTFPickaxe", true)
                .build();

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

        Player playerWhoCapture = capturedFlag.getPlayerWhoTookFlag();
        capturedFlag.playerLooseFlag();
        this.spawnPlayer(hyriRTF.getGame().getPlayer(playerWhoCapture.getUniqueId()));

        for(Player player : Bukkit.getOnlinePlayers()) {
            Title.setTitle(player, "flag captured", whoCapture.getName(), 3, 45, 3);
        }


        if(whoCapture.getColor().equals(HyriGameTeamColor.BLUE)) {
            for(HyriGamePlayer hyriRedGamePlayer : this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), CAPTURE_ENEMY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), CAPTURE_ENEMY_SUB.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
            }
            for(HyriGamePlayer hyriBlueGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName()).getPlayers()) {
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), CAPTURE_ALLY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), CAPTURE_ALLY_SUB.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
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
                Title.setTitle(hyriBlueGamePlayer.getPlayer().getPlayer(), CAPTURE_ENEMY_TITLE.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), CAPTURE_ENEMY_SUB.getForPlayer(hyriBlueGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
            }
            for(HyriGamePlayer hyriRedGamePlayer :  this.hyriRTF.getGame().getTeam(Teams.RED.getTeamName()).getPlayers()) {
                Title.setTitle(hyriRedGamePlayer.getPlayer().getPlayer(), CAPTURE_ALLY_TITLE.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), CAPTURE_ALLY_SUB.getForPlayer(hyriRedGamePlayer.getPlayer().getPlayer()), 2, 30, 2);
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

    public int getBluePoints() {
        return this.bluePoints;
    }

    public void winGame(HyriGameTeam winner) {
        HyriGameTeam looser = hyriRTF.getGame().getTeam(Teams.RED.getTeamName());
        if(winner.getColor().equals(HyriGameTeamColor.RED)) {
            looser = hyriRTF.getGame().getTeam(Teams.BLUE.getTeamName());
        }

        hyriRTF.getGame().setState(HyriGameState.ENDED);
        for(HyriGamePlayer hyriGamePlayer : winner.getPlayers()) {
            Title.setTitle(hyriGamePlayer.getPlayer().getPlayer(),VICTORY_TITLE.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), VICTORY_SUB.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), 5, 70, 5);
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.CREATIVE);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
        for(HyriGamePlayer hyriGamePlayer : looser.getPlayers()) {
            Title.setTitle(hyriGamePlayer.getPlayer().getPlayer() ,DEFEAT_TITLE.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), DEFEAT_SUB.getForPlayer(hyriGamePlayer.getPlayer().getPlayer()), 5, 70, 10);
            hyriGamePlayer.getPlayer().getPlayer().setGameMode(GameMode.SPECTATOR);
            hyriGamePlayer.getPlayer().getPlayer().getInventory().clear();
        }
    }

   }
