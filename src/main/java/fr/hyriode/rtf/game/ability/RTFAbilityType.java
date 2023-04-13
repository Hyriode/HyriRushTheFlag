package fr.hyriode.rtf.game.ability;

import fr.hyriode.api.language.HyriLanguageMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 21/05/2022 at 11:11
 */
public enum RTFAbilityType {

    ATTACK(0, Material.IRON_SWORD),
    MOVEMENT(1, Material.CHAINMAIL_BOOTS),
    SPECIAL(2, Material.EMERALD),
    BUILD(3, Material.SANDSTONE)
    ;

    private final int id;
    private final Material material;

    RTFAbilityType(int id, Material material) {
        this.id = id;
        this.material = material;
    }

    public String getDisplayName(Player player) {
        return HyriLanguageMessage.get("ability.type." + this.name().toLowerCase()).getValue(player);
    }

    public List<String> getLore(Player player) {
        final String str = HyriLanguageMessage.get("ability.type-lore." + this.name().toLowerCase()).getValue(player);
        final String[] splitLore = str.split("\n");

        return new ArrayList<>(Arrays.asList(splitLore));
    }

    public int getId() {
        return this.id;
    }

    public Material getMaterial() {
        return this.material;
    }
}
