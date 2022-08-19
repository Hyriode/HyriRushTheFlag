package fr.hyriode.rtf.game.host.category;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.util.RTFDisplay;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 19/08/2022 at 15:59
 */
public class RTFHostAbilitiesCategory extends HostCategory {

    public RTFHostAbilitiesCategory() {
        super(RTFDisplay.categoryDisplay("rtf-abilities", Material.NETHER_STAR));
        this.guiProvider = player -> new GUI(player, this);

        int index = 0;
        for (RTFAbility ability : RTFAbility.getAbilities()) {
            this.addSubCategory(index, new RTFHostAbilityCategory(ability));
            index++;
        }
    }

    private static class GUI extends HostGUI {

        public GUI(Player owner, HostCategory category) {
            super(owner, name(owner, "gui.host.abilities.name"), category);
            this.paginationManager.setArea(new PaginationArea(19, 34));
            this.usingPages = true;

            this.addCategories();
        }

        @Override
        protected void addCategories() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

            pagination.clear();

            for (HostCategory category : this.category.getSubCategories().values()) {
                if (!(category instanceof RTFHostAbilityCategory)) {
                    continue;
                }

                pagination.add(PaginatedItem.from(category.createItem(this.owner), event -> category.openGUI(this.owner)));
            }

            this.paginationManager.updateGUI();
        }

    }

}
