package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.api.HyriRTFGamePlayerHotbar;
import fr.hyriode.rushtheflag.utils.HyriRTFConfiguration;
import fr.hyriode.tools.item.ItemBuilder;
import fr.hyriode.tools.title.Title;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class HyriRTFGamePlayer extends HyriGamePlayer {

    private HyriRTF hyriRTF;

    private static final HyriLanguageMessage GAME_OVER_TITLE = new HyriLanguageMessage("game.over.title").addValue(HyriLanguage.FR, ChatColor.RED + "Game terminée").addValue(HyriLanguage.EN, "Game over");
    private static final HyriLanguageMessage GAME_OVER_SUB = new HyriLanguageMessage("game.over.sub").addValue(HyriLanguage.FR, ChatColor.YELLOW + "Vous ne pouvez plus respawn").addValue(HyriLanguage.EN, ChatColor.YELLOW + "You can't respawn");
    private static final HyriLanguageMessage GAME_RESPAWN_TITLE = new HyriLanguageMessage("game.respawn.title").addValue(HyriLanguage.FR, ChatColor.RED + "Tu es mort").addValue(HyriLanguage.EN, ChatColor.RED + "You died");
    private static final HyriLanguageMessage GAME_RESPAWN_SUB_1 = new HyriLanguageMessage("game.respawn.sub1").addValue(HyriLanguage.FR, ChatColor.YELLOW + "🥖Réaparition🥖 dans ").addValue(HyriLanguage.EN, ChatColor.YELLOW + "respawn in ");
    private static final HyriLanguageMessage GAME_RESPAWN_SUB_2 = new HyriLanguageMessage("game.respawn.sub2").addValue(HyriLanguage.FR, ChatColor.YELLOW + " secondes").addValue(HyriLanguage.EN, ChatColor.YELLOW + " seconds");
    private static final HyriLanguageMessage GAME_KILL_1 = new HyriLanguageMessage("game.kill").addValue(HyriLanguage.FR, ChatColor.GRAY + " a été tué par ").addValue(HyriLanguage.EN, ChatColor.GRAY + " was killed by ");
    private static final HyriLanguageMessage GAME_KILL_2 = new HyriLanguageMessage("game.death").addValue(HyriLanguage.FR, ChatColor.GRAY + " est mort").addValue(HyriLanguage.EN, ChatColor.GRAY + " died");

    private Player lastDamager;
    private boolean lastDamagerExist = false;
    private int lastDamagerTask;
    private final HyriRTFGamePlayerHotbar hotbar;

    private long kills;
    private long finalKills;
    private long death;
    private long woolsCaptured;
    private long woolsBroughtBack;

    private final List<ItemStack> armor = Arrays.asList(
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_HELMET)
    );

    public HyriRTFGamePlayer(Player player) {
        super(player);
        if(HyriRTFGamePlayerHotbar.hotbars.get(player.getUniqueId()) != null) {
            this.hotbar = HyriRTFGamePlayerHotbar.hotbars.get(player.getUniqueId());
        }else {
            this.hotbar = new HyriRTFGamePlayerHotbar(player.getUniqueId());
        }
    }

    public void setHyriRTF(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    public void kill() {
        this.death++;
        Player player = this.getPlayer().getPlayer();

        Bukkit.broadcastMessage(this.deathMessage(player));

        if(this.lastDamagerExist) {
            player.playSound(this.lastDamager.getLocation(), Sound.WOLF_HOWL, 1, 1);
            this.lastDamager.playNote(this.lastDamager.getLocation(), Instrument.PIANO, Note.flat(1, Note.Tone.A));
        }

        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        boolean playerCanRespawn = true;

        if (this.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
            if (!this.hyriRTF.getGame().isRedTeamCanRespawn()) {
                playerCanRespawn = false;
            }
        } else {
            if (!this.hyriRTF.getGame().isBlueTeamCanRespawn()) {
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
            if(this.lastDamagerExist) {
                this.hyriRTF.getGame().getPlayer(this.lastDamager.getUniqueId()).setKills(this.hyriRTF.getGame().getPlayer(this.lastDamager.getUniqueId()).getKills() + 1);
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(player.getWorld().getSpawnLocation().add(0, 20, 0));

            Title.sendTitle(player ,GAME_RESPAWN_TITLE.getForPlayer(player), GAME_RESPAWN_SUB_1.getForPlayer(player) + ChatColor.RED + 5 + GAME_RESPAWN_SUB_2.getForPlayer(player), 0, 20,0);

            Bukkit.getScheduler().runTaskLater(hyriRTF, () -> player.teleport(hyriRTF.getConfiguration().getLocation(this.getTeam().getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY)), 20L*5);

            for(int i = 4; i > 0; i--) {
                int finalI = 5-i;
                Bukkit.getScheduler().runTaskLater(hyriRTF, () -> Title.sendTitle(player,GAME_RESPAWN_TITLE.getForPlayer(player), GAME_RESPAWN_SUB_1.getForPlayer(player) + ChatColor.RED + finalI + GAME_RESPAWN_SUB_2.getForPlayer(player), 0, 20, 0), 20L*i);
            }

            Bukkit.getScheduler().runTaskLater(hyriRTF, this::spawn, 20L*5);
        }else {

            if(this.lastDamagerExist) {
                this.hyriRTF.getGame().getPlayer(this.lastDamager.getUniqueId()).setFinalKills(this.hyriRTF.getGame().getPlayer(this.lastDamager.getUniqueId()).getFinalKills() + 1);
            }

            Title.sendTitle(player,GAME_OVER_TITLE.getForPlayer(player), GAME_OVER_SUB.getForPlayer(player), 5, 70, 5);
            player.getInventory().clear();
            HyriGameItems.LEAVE_ITEM.give(this.hyriRTF.getHyrame(), player, 4);
            this.setSpectator(true);
            if(this.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
                this.hyriRTF.getGame().setFinalKilledBlue(this.hyriRTF.getGame().getFinalKilledBlue() + 1);
                if(this.getTeam().getPlayers().size() == this.hyriRTF.getGame().getFinalKilledBlue()) {
                    this.hyriRTF.getGame().winGame(hyriRTF.getGame().getTeam(HyriRTFTeams.RED.getTeamName()));
                }
            }else {
                this.hyriRTF.getGame().setFinalKilledRed(this.hyriRTF.getGame().getFinalKilledRed() + 1);
                if(this.getTeam().getPlayers().size() == this.hyriRTF.getGame().getFinalKilledRed()) {
                    this.hyriRTF.getGame().winGame(hyriRTF.getGame().getTeam(HyriRTFTeams.BLUE.getTeamName()));
                }
            }
        }

        Map<Material, Integer> materialList = new HashMap<Material, Integer>() {
            {
                put(Material.IRON_SWORD, hotbar.getIronSwordSlot());
                put(Material.GOLDEN_APPLE, hotbar.getGoldenAppleSlot());
                put(Material.IRON_PICKAXE, hotbar.getIronPickaxeSlot());
            }
        };

        for(Material material : materialList.keySet()) {
            if(player.getInventory().getItem(materialList.get(material)) != null && !player.getInventory().getItem(materialList.get(material)).getType().equals(material)) {
                for (int i = 0; i < 9; i++) {
                    if(player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType().equals(material)) {
                        boolean valueExist = false;
                        for(Integer integer : materialList.values()) {
                            if(i == integer) {
                                valueExist = true;
                                break;
                            }
                        }
                        if(!valueExist) {
                            materialList.replace(material, i);
                        }
                        break;
                    }
                }
            }
        }

        this.hotbar.setIronSwordSlot(materialList.get(Material.IRON_SWORD));
        this.hotbar.setGoldenAppleSlot(materialList.get(Material.GOLDEN_APPLE));
        this.hotbar.setIronPickaxeSlot(materialList.get(Material.IRON_PICKAXE));
    }

    public void spawn() {
        Player player = this.getPlayer().getPlayer();

        Bukkit.getScheduler().runTaskLater(this.hyriRTF, () -> {
            Location spawnLocation = hyriRTF.getConfiguration().getLocation(getTeam().getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY);
            player.teleport(new Location(player.getWorld() ,spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), hyriRTF.getConfiguration().getPitch(getTeam().getName() + HyriRTFConfiguration.SPAWN_LOCATION_KEY),(float) 0));
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setCanPickupItems(false);

            getPlayer().getPlayer().getInventory().clear();

            final ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta itemMeta = sword.getItemMeta();
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            sword.setItemMeta(itemMeta);

            final ItemStack pickaxe = new ItemBuilder(Material.IRON_PICKAXE)
                    .withEnchant(Enchantment.DIG_SPEED, 2)
                    .nbt().setBoolean("RTFPickaxe", true)
                    .build();

            player.getInventory().addItem(new ItemStack(Material.SANDSTONE,64*9));

            player.getInventory().setItem(hyriRTF.getConfiguration().swordSlot(player), sword);
            player.getInventory().setItem(hyriRTF.getConfiguration().gappleSlot(player), new ItemStack(Material.GOLDEN_APPLE, 64));
            player.getInventory().setItem(hyriRTF.getConfiguration().pickaxeSlot(player), pickaxe);

            ArrayList<ItemStack> dyedArmor = new ArrayList<>();

            for(ItemStack itemStack : armor) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                if(getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
                    leatherArmorMeta.setColor(Color.BLUE);
                }else {
                    leatherArmorMeta.setColor(Color.RED);
                }
                leatherArmorMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, false);
                itemStack.setItemMeta(leatherArmorMeta);
                dyedArmor.add(itemStack);
            }

            player.getInventory().setArmorContents(dyedArmor.toArray(new ItemStack[0]));
        }, 1);
    }

    public void setLastDamagerExist(Player newLastDamager) {
        if(this.lastDamagerExist) {
            Bukkit.getScheduler().cancelTask(this.lastDamagerTask);
        }
        this.lastDamagerExist = true;
        this.lastDamager = newLastDamager;
        this.lastDamagerTask = Bukkit.getScheduler().scheduleSyncDelayedTask(hyriRTF, () -> {
            lastDamagerExist = false;
            lastDamager = null;
        }, 20*20L);
    }

    private String deathMessage(Player player) {
        String finalKill = "";
        HyriLanguageMessage languageMessage = GAME_KILL_2;
        String killerName = "";
        ChatColor playerColor = ChatColor.BLUE;
        ChatColor killerColor = ChatColor.RED;

        if(this.lastDamagerExist) {
            languageMessage = GAME_KILL_1;
            killerName = this.lastDamager.getName();
        }

        if(this.getTeam().getColor().equals(HyriGameTeamColor.BLUE)) {
            if(!this.hyriRTF.getGame().isBlueTeamCanRespawn()) {
                finalKill = ChatColor.AQUA + "" + ChatColor.BOLD + "FINAL KILL !";
            }
        }else {
            playerColor = ChatColor.RED;
            killerColor = ChatColor.BLUE;
            if(!this.hyriRTF.getGame().isRedTeamCanRespawn()) {
                finalKill = ChatColor.AQUA + "" + ChatColor.BOLD + "FINAL KILL !";
            }
        }
        return playerColor + player.getName() + languageMessage.getForPlayer(player) + killerColor + killerName + finalKill;
    }

    public long getKills() {
        return kills;
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public long getFinalKills() {
        return finalKills;
    }

    public void setFinalKills(long finalKills) {
        this.finalKills = finalKills;
    }

    public long getDeath() {
        return death;
    }

    public long getWoolsCaptured() {
        return woolsCaptured;
    }

    public void setWoolsCaptured(long woolsCaptured) {
        this.woolsCaptured = woolsCaptured;
    }

    public long getWoolsBroughtBack() {
        return woolsBroughtBack;
    }

    public void setWoolsBroughtBack(long woolsBroughtBack) {
        this.woolsBroughtBack = woolsBroughtBack;
    }
}


