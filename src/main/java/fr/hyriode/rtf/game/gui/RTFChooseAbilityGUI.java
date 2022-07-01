package fr.hyriode.rtf.game.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.ablity.RTFAbility;
import fr.hyriode.rtf.game.ablity.RTFAbilityType;
import fr.hyriode.rtf.utils.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    private final RTFGamePlayer gamePlayer;

    public RTFChooseAbilityGUI(RTFGamePlayer owner, RTFAbilityType type) {

        super(owner.getPlayer(), HyriRTF.getLanguageManager().getValue(owner.getPlayer(), "ability.gui.name")
                        .replace("%ability%", owner.getAbility().getName(owner.getPlayer()))
                , 54);

        this.gamePlayer = owner;
        this.fillInventory(type);
    }

    private void fillInventory(RTFAbilityType type) {
        this.addBorders();
        this.addCategories(type);
        this.addItems(type);
    }

    private void addBorders() {
        for (int separatorSlot : InventoryUtil.getSeparatorSlots()) {
            this.setItem(separatorSlot, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ")
                    .withAllItemFlags()
                    .build());
        }
    }

    private void addCategories(RTFAbilityType type) {
        final int[] slots = new int[]{
                0, 9, 18, 27, 36, 45
        };

        for (RTFAbilityType value : RTFAbilityType.values()) {
            for (int slot : slots) {
                if (this.inventory.getItem(slot) == null) {
                    if (type.equals(value)) {
                        this.setItem(slot, this.addCategoryItem(value, true), event -> event.setCancelled(true));
                    } else {
                        this.setItem(slot, this.addCategoryItem(value, false), event -> {
                            event.setCancelled(true);
                            new RTFChooseAbilityGUI(gamePlayer, value).open();
                        });
                    }
                    break;
                }
            }
        }

        for (int slot : slots) {
            if (this.inventory.getItem(slot) == null) {
                this.setItem(slot, new ItemBuilder(Material.BARRIER).withName(ChatColor.GOLD + "" + ChatColor.MAGIC + "hyriode")
                        .withAllItemFlags()
                        .withLore(HyriLanguageMessage.get("ability.soon").getForPlayer(gamePlayer.getPlayer()))
                        .build(), event -> event.setCancelled(true));
            }
        }
    }

    private void addItems(RTFAbilityType type) {
        for (RTFAbility ability : RTFAbility.getAbilities(type)) {
            for (int availableSlot : InventoryUtil.getAvailableSlots()) {
                if (this.inventory.getItem(availableSlot) == null) {
                        if (this.gamePlayer.getAbility().equals(ability)) {
                            this.setItem(availableSlot, this.getAbilityItem(this.gamePlayer, ability, AbilityStatus.SELECTED), event -> {
                                event.setCancelled(true);
                            });
                        } else {
                            this.setItem(availableSlot, this.getAbilityItem(this.gamePlayer, ability, AbilityStatus.SELECT), event -> {
                                event.setCancelled(true);
                                gamePlayer.setAbility(ability);
                                gamePlayer.getAccount().setLastAbility(ability.getModel());
                                new RTFChooseAbilityGUI(gamePlayer, gamePlayer.getAbility().getType()).open();
                            });
                        }
                    break;
                }
            }
        }
    }


    private ItemStack getAbilityItem(RTFGamePlayer gamePlayer, RTFAbility ability, AbilityStatus status) {
        final Player player = gamePlayer.getPlayer();
        List<String> lore = new ArrayList<>();

        lore.add(HyriLanguageMessage.get("ability.gui.type").getForPlayer(player).replace("%value%", ability.getType().getDisplayName(player)));
        lore.add(" ");
        lore.addAll(2, ability.getLore(player));
        lore.add(" ");

        ItemBuilder builder = new ItemBuilder(ability.getIcon())
                .withAllItemFlags()
                .withName(ability.getName(player));

        switch (status) {
            case SELECT:
                lore.add(HyriLanguageMessage.get("ability.gui.select").getForPlayer(player));
                break;
            case SELECTED:
                lore.add(HyriLanguageMessage.get("ability.gui.selected").getForPlayer(player));
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
        final int i = RTFAbility.getAbilities(type).size();

        lore.addAll(0, type.getLore(player));
        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " " + HyriLanguageMessage.get("ability.type.lore.list").getForPlayer(player) + ChatColor.AQUA + i);

        ItemBuilder builder = new ItemBuilder(type.getMaterial())
                .withName(type.getDisplayName(player))
                .withAllItemFlags()
                .withLore(lore);

        if (selected) {
            builder.withGlow();
        }

        return builder.build();
    }

    public enum AbilityStatus {
        SELECTED,
        SELECT;
    }

}
