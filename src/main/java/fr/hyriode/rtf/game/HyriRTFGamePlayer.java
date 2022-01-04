package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.util.HyriDeadScreen;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.game.scoreboard.HyriRTFScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.function.Function;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class HyriRTFGamePlayer extends HyriGamePlayer {

    private Player lastHitter;
    private BukkitTask lastHitterTask;

    private HyriRTFScoreboard scoreboard;

    private HyriRTFPlayer account;
    private long kills;
    private long finalKills;
    private long deaths;
    private long capturedFlags;
    private long flagsBroughtBack;

    private HyriRTF plugin;

    public HyriRTFGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void spawn() {
        this.player.getInventory().clear();
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setHealth(20.0F);

        this.giveHotBar();
        this.giveArmor();
    }

    public void giveHotBar() {
        final PlayerInventory inventory = this.player.getInventory();

        inventory.addItem(new ItemBuilder(Material.SANDSTONE, 64 * 9, 2).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, 16).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_PICKAXE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());
    }

    public void giveArmor() {
        final PlayerInventory inventory = this.player.getInventory();

        inventory.setHelmet(this.getArmorPiece(Material.LEATHER_HELMET));
        inventory.setChestplate(this.getArmorPiece(Material.LEATHER_CHESTPLATE));
        inventory.setLeggings(this.getArmorPiece(Material.LEATHER_LEGGINGS));
        inventory.setBoots(this.getArmorPiece(Material.LEATHER_BOOTS));
    }

    public void kill(boolean diedFromVoid) {
        final HyriRTFGame game = this.plugin.getGame();

        this.hide();

        final PlayerInventory playerInventory = this.player.getInventory();

        final Map<HyriRTFHotBar.Item, Integer> hotbar = this.account.getHotBar().getItems();

        for(HyriRTFHotBar.Item item : hotbar.keySet()) {
            if(playerInventory.getItem(hotbar.get(item)) != null) {
                if(!playerInventory.getItem(hotbar.get(item)).getType().equals(HyriRTFHotBar.HOTBAR_ITEMS.get(item))) {
                    for (int i = 0; i < 9; i++) {
                        if(playerInventory.getItem(i) != null) {
                            if(playerInventory.getItem(i).getType().equals(HyriRTFHotBar.HOTBAR_ITEMS.get(item))) {
                                boolean valueExist = false;
                                for(Integer integer : hotbar.values()) {
                                    if (i == integer) {
                                        valueExist = true;
                                        break;
                                    }
                                }
                                if(!valueExist) {
                                    hotbar.put(item, i);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }


        playerInventory.setArmorContents(null);
        playerInventory.clear();

        this.player.setHealth(20.0F);
        this.player.setGameMode(GameMode.SPECTATOR);
        this.player.teleport(this.plugin.getConfiguration().getSpawn());

        this.addDeath();

        final IHyriLanguageManager languageManager = HyriRTF.getLanguageManager();
        final String formattedName = this.team.formatName(this.player);
        final boolean hasLife = this.getTeam().hasLife();

        Function<Player, String> killMessage;
        if (this.lastHitter != null) {
            final HyriRTFGamePlayer lastHitterGamePlayer = this.plugin.getGame().getPlayer(this.lastHitter.getUniqueId());
            final Function<Player, String> defaultMessage = target -> formattedName + ChatColor.GRAY + languageManager.getValue(target, "message.kill-by-player") + lastHitterGamePlayer.getTeam().formatName(this.lastHitter) + ChatColor.GRAY;

            if (!hasLife) {
                lastHitterGamePlayer.addFinalKill();
            }

            lastHitterGamePlayer.addKill();
            lastHitterGamePlayer.getScoreboard().update();

            if (diedFromVoid) {
                killMessage = target -> defaultMessage.apply(target) + languageManager.getValue(target, "message.and-void");
            } else {
                killMessage = target -> defaultMessage.apply(target) + ".";
            }
        } else {
            if (diedFromVoid) {
               killMessage = target -> formattedName + ChatColor.GRAY + languageManager.getValue(target, "message.died-void");
            } else {
                killMessage = target -> formattedName + ChatColor.GRAY + languageManager.getValue(target, "message.died");
            }
        }

        if (game.isHoldingFlag(this.player)) {
            game.getHoldingFlag(this.player).lost();
        }

        this.scoreboard.update();

        if (hasLife) {
            this.dead = true;

            this.game.sendMessageToAll(killMessage);

            HyriDeadScreen.create(this.plugin, this.player, 5, () -> {
                this.dead = false;

                this.show();

                this.player.teleport(this.team.getSpawnLocation());

                this.spawn();
            });
        } else {
            this.game.sendMessageToAll(target -> killMessage.apply(target) + ChatColor.BOLD + ChatColor.RED + languageManager.getValue(target, "message.eliminated"));

            this.eliminated = true;
            this.spectator = true;

            this.player.setGameMode(GameMode.ADVENTURE);
            this.player.setAllowFlight(true);
            this.player.setFlying(true);

            // TODO Give spectating objects

            game.win(game.getWinner());
        }

        this.lastHitter = null;
    }

    private ItemStack getArmorPiece(Material material) {
        return new ItemBuilder(material)
                .withLeatherArmorColor(this.team.getColor().getDyeColor().getColor())
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .unbreakable()
                .build();
    }

    public HyriRTFPlayer getAccount() {
        return this.account;
    }

    public void setAccount(HyriRTFPlayer account) {
        this.account = account;
    }

    public void addKill() {
        this.kills += 1;
    }

    public long getKills() {
        return this.kills;
    }

    public void addFinalKill() {
        this.finalKills += 1;
    }

    public long getFinalKills() {
        return this.finalKills;
    }

    public void addDeath() {
        this.deaths += 1;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public void addCapturedFlag() {
        this.capturedFlags += 1;
    }

    public long getCapturedFlags() {
        return this.capturedFlags;
    }

    public void addFlagBroughtBack() {
        this.flagsBroughtBack += 1;
    }

    public long getFlagsBroughtBack() {
        return this.flagsBroughtBack;
    }

    public HyriRTFScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(HyriRTFScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setLastHitter(Player lastHitter) {
        if (!this.game.areInSameTeam(this.player, lastHitter)) {
            this.lastHitter = lastHitter;

            if (this.lastHitterTask != null) {
                this.lastHitterTask.cancel();
            }

            this.lastHitterTask = new BukkitRunnable() {
                @Override
                public void run() {
                    setLastHitter(null);
                }
            }.runTaskLater(this.plugin, 20 * 15L);
        }
    }

    void setPlugin(HyriRTF plugin) {
        this.plugin = plugin;
    }

    @Override
    public HyriRTFGameTeam getTeam() {
        return (HyriRTFGameTeam) this.team;
    }

}
