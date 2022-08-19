package fr.hyriode.rtf.game.host.category;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.host.option.TimeOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.util.RTFDisplay;
import fr.hyriode.rtf.util.RTFMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 19/08/2022 at 16:03
 */
public class RTFHostAbilityCategory extends HostCategory {

    private final RTFAbility ability;

    public RTFHostAbilityCategory(RTFAbility ability) {
        super(RTFDisplay.categoryDisplay("rtf-ability", Material.NETHER_STAR));
        this.ability = ability;
        this.guiProvider = player -> new GUI(player, this, this.ability);

        final String abilityName = ability.getModel().name().toLowerCase();

        this.addOption(30, new EnabledOption(RTFDisplay.optionDisplay("rtf-ability-enabled-" + abilityName, "rtf-ability-enabled", Material.INK_SACK))
                .onChanged(value -> {
                    ability.setEnabled(value);

                    if (!value) {
                        final List<RTFAbility> enabledAbilities = RTFAbility.getEnabledAbilities();

                        if (enabledAbilities.size() == 0) {
                            for (RTFGamePlayer gamePlayer : HyriRTF.get().getGame().getPlayers()) {
                                gamePlayer.setAbility(null);
                            }
                            return;
                        }

                        final RTFAbility randomAbility = enabledAbilities.get(ThreadLocalRandom.current().nextInt(0, enabledAbilities.size()));

                        for (RTFGamePlayer gamePlayer : HyriRTF.get().getGame().getPlayers()) {
                            if (gamePlayer.getAbility().getId().equals(this.ability.getId())) {
                                gamePlayer.setAbility(randomAbility);
                            }
                        }
                    }
                }));
        this.addOption(32, new TimeOption(RTFDisplay.optionDisplay("rtf-ability-cooldown-" + abilityName, "rtf-ability-cooldown", Material.WATCH), ability.getCooldown(), 0L, Long.MAX_VALUE, new long[]{1, 5})
                .onChanged(value -> ability.setCooldown(Math.toIntExact(value))));
    }

    @Override
    public ItemStack createItem(Player player) {
        final List<String> lore = ListReplacer.replace(RTFMessage.ABILITY_HOST_CATEGORY_LORE.asList(player), "%enabled%", this.ability.isEnabled() ? ChatColor.GREEN + Symbols.TICK_BOLD : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD)
                .replace("%cooldown%", String.valueOf(this.ability.getCooldown()))
                .list();

        return new ItemBuilder(this.ability.getIcon())
                .withName(this.ability.getName(player))
                .withLore(lore)
                .withAllItemFlags()
                .nbt().setString(HostDisplay.NBT_KEY, this.name).setBoolean(NBT_KEY, true).build();
    }

    private static class EnabledOption extends BooleanOption {

        public EnabledOption(HostDisplay display) {
            super(display, true);
        }

        @Override
        public ItemStack createItem(Player player) {
            final ItemStack itemStack = super.createItem(player);

            itemStack.setDurability((short) (this.value ? 10 : 1));

            return itemStack;
        }

    }

    private static class GUI extends HostGUI {

        private final RTFAbility ability;

        public GUI(Player owner, HostCategory category, RTFAbility ability) {
            super(owner, ChatColor.DARK_GRAY + ChatColor.stripColor(ability.getName(owner)), category);
            this.ability = ability;

            this.addAbilityItem();
        }

        @Override
        protected void addOption(int slot, HostOption<?> option) {
            super.addOption(slot, option);
            this.addAbilityItem();
        }

        private void addAbilityItem() {
            if (this.ability == null) {
                return;
            }

            final List<String> lore = ListReplacer.replace(RTFMessage.ABILITY_HOST_CATEGORY_LORE.asList(this.owner), "%enabled%", this.ability.isEnabled() ? ChatColor.GREEN + Symbols.TICK_BOLD : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD)
                    .replace("%cooldown%", String.valueOf(this.ability.getCooldown()))
                    .list();

            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);

            this.setItem(22, new ItemBuilder(this.ability.getIcon())
                    .withName(this.ability.getName(this.owner))
                    .withLore(lore)
                    .withAllItemFlags()
                    .build());
        }

    }

}
