package fr.hyriode.rtf.game.host.category;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.option.IntegerOption;
import fr.hyriode.hyrame.host.option.TimeOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.util.RTFDisplay;
import fr.hyriode.rtf.util.RTFHead;
import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 19/08/2022 at 13:54
 */
public class RTFHostMainCategory extends HostCategory {

    public RTFHostMainCategory() {
        super(RTFDisplay.categoryDisplay("rtf-main", new ItemBuilder(Material.BANNER, 1, 15).build()));

        this.addOption(20, new IntegerOption(RTFDisplay.optionDisplay("lives", Material.GOLDEN_APPLE), 1, 0, Integer.MAX_VALUE));
        this.addOption(21, new TimeOption(RTFDisplay.optionDisplay("respawn-time", ItemBuilder.asHead(RTFHead.DEATH).build()), 5L, 0L, 60L, new long[] {1, 3}));
        this.addOption(29, new TimeOption(RTFDisplay.optionDisplay("game-time", Material.WATCH), 10 * 60L, 0L, Long.MAX_VALUE, new long[] {60L, 5 * 60L}));

        this.addSubCategory(23, new RTFHostAbilitiesCategory());
    }

}
