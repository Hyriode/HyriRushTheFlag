package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFData;
import fr.hyriode.rtf.api.RTFHotBar;
import fr.hyriode.rtf.api.RTFStatistics;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.item.RTFAbilityItem;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import fr.hyriode.rtf.game.ui.RTFAbilityBar;
import fr.hyriode.rtf.game.ui.scoreboard.RTFScoreboard;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:39
 */
public class RTFGamePlayer extends HyriGamePlayer {

    private RTFScoreboard scoreboard;
    private RTFAbility ability;

    private boolean cooldown = false;
    private int cooldownTime = -1;

    private RTFAbilityBar abilityBar;

    private RTFData data;
    private RTFStatistics statistics;

    private long kills;
    private long finalKills;
    private long deaths;
    private long capturedFlags;
    private long flagsBroughtBack;

    public RTFGamePlayer(Player player) {
        super(player);
    }

    public void startGame() {
        if (this.ability == null) {
            final List<RTFAbility> enabledAbilities = RTFAbility.getEnabledAbilities();

            if (enabledAbilities.size() != 0) {
                this.ability = enabledAbilities.get(ThreadLocalRandom.current().nextInt(enabledAbilities.size()));
            }
        }

        this.initUI();

        this.spawn(true);
    }

    public void initUI() {
        this.scoreboard = new RTFScoreboard(HyriRTF.get(), this.player);
        this.scoreboard.show();

        this.abilityBar = new RTFAbilityBar(this);
        this.abilityBar.runTaskTimer(HyriRTF.get(), 0, 20L);
    }

    public void spawn(boolean teleport) {
        if (!this.isOnline()) {
            return;
        }

        PlayerUtil.resetPlayer(this.player, false);
        PlayerUtil.resetPlayerInventory(this.player);

        this.player.setGameMode(GameMode.SURVIVAL);

        this.giveHotBar();
        this.giveArmor();

        if (teleport) {
            this.player.teleport(((RTFGameTeam) this.getTeam()).getSpawnLocation());

            if (this.ability != null) {
                this.cooldown = false;

                this.handleCooldown(5);
            }
        }
    }

    public void giveHotBar() {
        final PlayerInventory inventory = this.player.getInventory();

        this.player.setExp(0);
        this.player.setLevel(0);

        inventory.addItem(new ItemBuilder(Material.SANDSTONE, 64 * 9, 2).build());
        inventory.setItem(this.data.getHotBar().getSlot(RTFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, 16).build());
        inventory.setItem(this.data.getHotBar().getSlot(RTFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(this.data.getHotBar().getSlot(RTFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_PICKAXE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());

        if (this.ability != null) {
            IHyrame.get().getItemManager().giveItem(this.player, this.data.getHotBar().getSlot(RTFHotBar.Item.ABILITY_ITEM), RTFAbilityItem.class);
        }
    }

    public void giveArmor() {
        final PlayerInventory inventory = this.player.getInventory();

        inventory.setHelmet(this.getArmorPiece(Material.LEATHER_HELMET));
        inventory.setChestplate(this.getArmorPiece(Material.LEATHER_CHESTPLATE));
        inventory.setLeggings(this.getArmorPiece(Material.LEATHER_LEGGINGS));
        inventory.setBoots(this.getArmorPiece(Material.LEATHER_BOOTS));
    }

    public void handleCooldown(final int time) {
        if (this.cooldown || !this.isOnline()) {
            return;
        }

        this.cooldown = true;
        this.cooldownTime = time;
    }

    public boolean kill() {
        final RTFGame game = HyriRTF.get().getGame();
        final boolean hasLife = ((RTFGameTeam) this.getTeam()).hasLife();

        final PlayerInventory playerInventory = this.player.getInventory();

        for (RTFHotBar.Item item : this.data.getHotBar().getItems().keySet()) {
            for (int i = 0; i <= 9; i++) {
                final ItemStack itemStack = playerInventory.getItem(i);

                if (itemStack != null && itemStack.getType() == Material.getMaterial(item.getName())) {
                    this.data.getHotBar().setItem(item, i);
                }
            }
        }

        this.addDeath();

        this.scoreboard.update();

        final RTFGamePlayer killer = this.getLastHitterGamePlayer();

        if (killer != null) {
            killer.addKill();

            if (!hasLife) {
                killer.addFinalKill();
            }

            killer.getScoreboard().update();
        }

        if (game.isHoldingFlag(this.player)) {
            game.getHoldingFlag(this.player).lost();
        }

        return hasLife;
    }

    private ItemStack getArmorPiece(Material material) {
        return new ItemBuilder(material)
                .withLeatherArmorColor(this.getTeam().getColor().getDyeColor().getColor())
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .unbreakable()
                .build();
    }

    public boolean hasFlag() {
        RTFGameTeam team = (RTFGameTeam) this.getTeam();
        return team.getOppositeTeam().getFlag().getHolder() != null && team.getOppositeTeam().getFlag().getHolder().equals(this.getPlayer());
    }

    public RTFData getData() {
        return this.data;
    }

    public void setData(RTFData data) {
        this.data = data;
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

    public boolean isCooldown() {
        return this.cooldown;
    }

    public void setCooldown(boolean cooldown) {
        this.cooldown = cooldown;

        if (!cooldown) {
            this.cooldownTime = -1;
        }
    }

    public int getCooldownTime() {
        return this.cooldownTime;
    }

    public void setCooldownTime(int cooldownTime) {
        if (!this.cooldown) {
            return;
        }

        this.cooldownTime = cooldownTime;
    }

    public RTFAbility getAbility() {
        return this.ability;
    }

    public void setAbility(RTFAbility ability) {
        this.ability = ability;
    }

    public Player getLastHitterPlayer() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = HyriRTF.get().getGame().getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asPlayer();
        }
        return null;
    }

    public RTFGamePlayer getLastHitterGamePlayer() {
        final Player player = this.getLastHitterPlayer();

        if (player != null) {
            return HyriRTF.get().getGame().getPlayer(player);
        }
        return null;
    }

}
