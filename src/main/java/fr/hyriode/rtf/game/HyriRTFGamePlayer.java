package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.util.HyriDeadScreen;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.game.abilities.Ability;
import fr.hyriode.rtf.game.abilities.AbilityItem;
import fr.hyriode.rtf.game.scoreboard.HyriRTFScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.event.ActionListener;
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
    private Ability ability;

    private boolean cooldown = false;

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

        this.handleCooldown(this.getAbility().getCooldown() / 2);
    }

    public void giveHotBar() {
        final PlayerInventory inventory = this.player.getInventory();
        this.player.setExp(0);
        this.player.setLevel(0);

        inventory.addItem(new ItemBuilder(Material.SANDSTONE, 64 * 9, 2).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, 16).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_PICKAXE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());
        this.plugin.getHyrame().getItemManager().giveItem(this.player, this.account.getHotBar().getSlot(HyriRTFHotBar.Item.ABILITY_ITEM), AbilityItem.class);
    }

    public void giveArmor() {
        final PlayerInventory inventory = this.player.getInventory();

        inventory.setHelmet(this.getArmorPiece(Material.LEATHER_HELMET));
        inventory.setChestplate(this.getArmorPiece(Material.LEATHER_CHESTPLATE));
        inventory.setLeggings(this.getArmorPiece(Material.LEATHER_LEGGINGS));
        inventory.setBoots(this.getArmorPiece(Material.LEATHER_BOOTS));
    }

    public void handleCooldown(final int i) {
        if (!this.isCooldown()) {
            this.setCooldown(true);
        }
        new BukkitRunnable() {
            private int index = i;

            @Override
            public void run() {
                HyriLanguageMessage actionBar = new HyriLanguageMessage("actionbar.cooldown.display")
                        .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + Symbols.SPARKLES + " Capacité en attente : " + ChatColor.AQUA + this.index + "s " + ChatColor.DARK_AQUA + Symbols.SPARKLES)
                        .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + Symbols.SPARKLES + " Ability in cooldown: " + ChatColor.AQUA + this.index + "s " + ChatColor.DARK_AQUA + Symbols.SPARKLES);
                final ActionBar bar = new ActionBar(actionBar.getForPlayer(player));

                bar.send(player);
                player.setLevel(this.index);

                if(isDead()) {
                    player.setLevel(0);
                    bar.remove(player);
                    this.cancel();
                }
                if(index == 0) {
                    HyriLanguageMessage actionBarFinished = new HyriLanguageMessage("actionbar.finished.display")
                            .addValue(HyriLanguage.FR, ChatColor.GREEN + "✔ Capacité disponible ✔")
                            .addValue(HyriLanguage.EN, ChatColor.GREEN + "✔ Ability available ✔");
                    final ActionBar finishedBar = new ActionBar(actionBarFinished.getForPlayer(player));
                    setCooldown(false);
                    player.setLevel(0);
                    bar.remove(player);
                    finishedBar.send(player);
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 3f, 3f);
                    this.cancel();
                }
                index--;
            }
        }.runTaskTimer(this.plugin, 0, 20);

    }

    public void kill(boolean diedFromVoid) {
        final HyriRTFGame game = this.plugin.getGame();

        this.hide();

        this.player.getActivePotionEffects().forEach(effect -> this.player.removePotionEffect(effect.getType()));

        final PlayerInventory playerInventory = this.player.getInventory();

        for (HyriRTFHotBar.Item item : this.account.getHotBar().getItems().keySet()) {

            if (this.account.getHotBar().getSlot(item) != null) {
                for (int i = 0; i <= 9; i++) {
                    if (playerInventory.getItem(i) != null) {
                        if (playerInventory.getItem(i).getType() == Material.getMaterial(item.getName())) {
                            this.account.getHotBar().setItem(item, i);
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

    public boolean hasFlag() {
        HyriRTFGameTeam team = (HyriRTFGameTeam) this.team;
        return team.getOppositeTeam().getFlag().getHolder() != null && team.getOppositeTeam().getFlag().getHolder().equals(this.getPlayer());
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
        if (lastHitter != null) {
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
    }

    void setPlugin(HyriRTF plugin) {
        this.plugin = plugin;
    }

    @Override
    public HyriRTFGameTeam getTeam() {
        return (HyriRTFGameTeam) this.team;
    }

    public boolean isCooldown() {
        return this.cooldown;
    }

    public void setCooldown(boolean cooldown) {
        this.cooldown = cooldown;
    }

    public Ability getAbility() {
        return this.ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
}
