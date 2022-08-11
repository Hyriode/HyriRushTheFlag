package fr.hyriode.rtf.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.api.player.HyriRTFPlayer;
import fr.hyriode.rtf.api.statistics.RTFStatistics;
import fr.hyriode.rtf.game.ablity.RTFAbility;
import fr.hyriode.rtf.game.items.RTFAbilityItem;
import fr.hyriode.rtf.game.scoreboard.RTFScoreboard;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class RTFGamePlayer extends HyriGamePlayer {

    private RTFScoreboard scoreboard;
    private RTFAbility ability;

    private boolean cooldown = false;

    private HyriRTFPlayer account;
    private RTFStatistics statistics;
    private long kills;
    private long finalKills;
    private long deaths;
    private long capturedFlags;
    private long flagsBroughtBack;

    private HyriRTF plugin;

    public RTFGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void startGame() {
        this.scoreboard = new RTFScoreboard(this.plugin, this.player);
        this.scoreboard.show();

        this.spawn(true);
    }

    public IHyriPlayer asHyriode() {
        return HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId());
    }

    public void spawn(boolean teleport) {
        PlayerUtil.resetPlayer(this.player, true);
        this.player.setGameMode(GameMode.SURVIVAL);

        this.giveHotBar();
        this.giveArmor();

        if (teleport) {
            this.player.teleport(this.team.getSpawnLocation());
            this.handleCooldown(this.getAbility().getCooldown() / 2);
        }
    }

    public void giveHotBar() {
        final PlayerInventory inventory = this.player.getInventory();
        this.player.setExp(0);
        this.player.setLevel(0);

        inventory.addItem(new ItemBuilder(Material.SANDSTONE, 64 * 9, 2).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, 16).build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(this.account.getHotBar().getSlot(HyriRTFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_PICKAXE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());

        this.plugin.getHyrame().getItemManager().giveItem(this.player, this.account.getHotBar().getSlot(HyriRTFHotBar.Item.ABILITY_ITEM), RTFAbilityItem.class);
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
                final ActionBar bar = new ActionBar(RTFMessage.ABILITY_WAITING_BAR.asString(player).replace("%time%", this.index + "s"));

                bar.send(player);
                player.setLevel(this.index);

                if (isDead()) {
                    player.setLevel(0);
                    bar.remove(player);
                    this.cancel();
                }

                if (index == 0) {
                    final ActionBar finishedBar = new ActionBar(RTFMessage.ABILITY_READY_BAR.asString(player));
                    setCooldown(false);
                    player.setLevel(0);
                    bar.remove(player);
                    finishedBar.send(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.8F, 2.0F);
                    this.cancel();
                }
                index--;
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }

    public boolean kill() {
        final RTFGame game = this.plugin.getGame();
        final boolean hasLife = this.getTeam().hasLife();

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

        this.addDeath();

        this.scoreboard.update();

        if (this.getLastHitterGamePlayer() != null) {
            if (hasLife) {
                this.getLastHitterGamePlayer().addKill();
            } else {
                this.getLastHitterGamePlayer().addFinalKill();
            }

            this.getLastHitterGamePlayer().getScoreboard().update();
        }

        if (game.isHoldingFlag(this.player)) {
            game.getHoldingFlag(this.player).lost();
        }

        if (!hasLife) {
            this.setSpectator(true);
        }

        game.win(game.getWinner());

        return hasLife;
    }

    private ItemStack getArmorPiece(Material material) {
        return new ItemBuilder(material)
                .withLeatherArmorColor(this.team.getColor().getDyeColor().getColor())
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .unbreakable()
                .build();
    }

    public boolean hasFlag() {
        RTFGameTeam team = (RTFGameTeam) this.team;
        return team.getOppositeTeam().getFlag().getHolder() != null && team.getOppositeTeam().getFlag().getHolder().equals(this.getPlayer());
    }

    public HyriRTFPlayer getAccount() {
        return this.account;
    }

    public void setAccount(HyriRTFPlayer account) {
        this.account = account;
    }

    public RTFStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(RTFStatistics statistics) {
        this.statistics = statistics;
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

    public RTFScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(RTFScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    void setPlugin(HyriRTF plugin) {
        this.plugin = plugin;
    }

    @Override
    public RTFGameTeam getTeam() {
        return (RTFGameTeam) this.team;
    }

    public boolean isCooldown() {
        return this.cooldown;
    }

    public void setCooldown(boolean cooldown) {
        this.cooldown = cooldown;
    }

    public RTFAbility getAbility() {
        return this.ability;
    }

    public void setAbility(RTFAbility Power) {
        this.ability = Power;
    }

    public Player getLastHitterPlayer() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.game.getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asPlayer();
        }
        return null;
    }

    public RTFGamePlayer getLastHitterGamePlayer() {
        final Player player = this.getLastHitterPlayer();
        if (player != null) {
            final RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player.getUniqueId());

            if (gamePlayer != null) {
                return gamePlayer;
            }
        }
        return null;
    }
}
