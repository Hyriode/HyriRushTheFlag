package fr.hyriode.rtf.game.gui;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:24
 */
public class RTFChooseAbilityGUI extends HyriInventory {

    private static final int[] ABILITIES_SLOTS = new int[] {
            11, 12, 13, 14, 15, 16,
            20, 21, 22, 23, 24, 25,
            29, 30 ,31, 32, 33, 34,
            38, 39, 40, 41, 42, 43
    };
    private static final int[] CATEGORIES_SLOTS = new int[] {0, 9, 18, 27, 36, 45};
    private static final int[] SEPARATOR_SLOTS = new int[] {
            1, 2, 3, 4, 5, 6, 7, 8,
            10, 17,
            19, 26,
            28, 35,
            37, 44,
            46, 47,
            48, 49, 50, 51, 52, 53
    };

    private final RTFGamePlayer gamePlayer;

    public RTFChooseAbilityGUI(RTFGamePlayer owner, RTFAbilityType type) {
        super(owner.getPlayer(), HyriLanguageMessage.get("ability.gui.name").getValue(owner).replace("%ability%", owner.getAbility() == null ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : owner.getAbility().getName(owner.getPlayer())), 54);
        this.gamePlayer = owner;
        this.fillInventory(type);
    }

    private void fillInventory(RTFAbilityType type) {
        this.addBorders();
        this.addCategories(type);
        this.addItems(type);
    }

    private void addBorders() {
        for (int separatorSlot : SEPARATOR_SLOTS) {
            this.setItem(separatorSlot, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        }
    }

    private void addCategories(RTFAbilityType type) {
        for (RTFAbilityType value : RTFAbilityType.values()) {
            for (int slot : CATEGORIES_SLOTS) {
                if (this.inventory.getItem(slot) == null) {
                    if (type.equals(value)) {
                        this.setItem(slot, this.addCategoryItem(value, true));
                    } else {
                        this.setItem(slot, this.addCategoryItem(value, false), event -> {
                            this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                            new RTFChooseAbilityGUI(gamePlayer, value).open();
                        });
                    }
                    break;
                }
            }
        }

        for (int slot : CATEGORIES_SLOTS) {
            if (this.inventory.getItem(slot) == null) {
                this.setItem(slot, new ItemBuilder(Material.BARRIER).withName(ChatColor.GOLD + "" + ChatColor.MAGIC + "*******")
                        .withAllItemFlags()
                        .withLore(HyriLanguageMessage.get("ability.soon").getValue(gamePlayer.getPlayer()))
                        .build(), event -> event.setCancelled(true));
            }
        }
    }

    private void addItems(RTFAbilityType type) {
        for (RTFAbility ability : RTFAbility.getAbilities(type)) {
            if (!ability.isEnabled()) {
                continue;
            }

            for (int availableSlot : ABILITIES_SLOTS) {
                if (this.inventory.getItem(availableSlot) != null) {
                    continue;
                }

                if (this.gamePlayer.getAbility().equals(ability)) {
                    this.setItem(availableSlot, this.getAbilityItem(this.gamePlayer, ability, AbilityStatus.SELECTED));
                } else {
                    this.setItem(availableSlot, this.getAbilityItem(this.gamePlayer, ability, AbilityStatus.SELECT), event -> {
                        gamePlayer.setAbility(ability);
                        gamePlayer.getAccount().setLastAbility(ability.getModel());

                        this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                        new RTFChooseAbilityGUI(gamePlayer, gamePlayer.getAbility().getType()).open();
                    });
                }
                break;
            }
        }
    }


    private ItemStack getAbilityItem(RTFGamePlayer gamePlayer, RTFAbility ability, AbilityStatus status) {
        final Player player = gamePlayer.getPlayer();
        List<String> lore = new ArrayList<>();

        lore.add(HyriLanguageMessage.get("ability.gui.type").getValue(player).replace("%value%", ability.getType().getDisplayName(player)));
        lore.add(" ");
        lore.addAll(2, ability.getLore(player));
        lore.add(" ");

        ItemBuilder builder = new ItemBuilder(ability.getIcon())
                .withAllItemFlags()
                .withName(ability.getName(player));

        switch (status) {
            case SELECT:
                lore.add(HyriLanguageMessage.get("ability.gui.select").getValue(player));
                break;
            case SELECTED:
                lore.add(HyriLanguageMessage.get("ability.gui.selected").getValue(player));
                builder.withGlow();
                break;
            default:
                break;
        }

        return builder.withLore(lore).build();
    }

    private ItemStack addCategoryItem(RTFAbilityType type, boolean selected) {
        final List<String> lore = new ArrayList<>();
        final Player player = this.gamePlayer.getPlayer();

        lore.addAll(0, type.getLore(player));
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + HyriLanguageMessage.get("ability.type.lore.list").getValue(player) + ChatColor.AQUA + RTFAbility.getEnabledAbilities(type).size());

        ItemBuilder builder = new ItemBuilder(type.getMaterial())
                .withName(type.getDisplayName(player))
                .withAllItemFlags()
                .withLore(lore);

        if (selected) {
            builder.withGlow();
        }

        return builder.build();
    }

    private enum AbilityStatus {
        SELECTED,
        SELECT
    }

}
