package fr.hyriode.rtf.game.ability.attack;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 21/05/2022 at 11:42
 */
public class RTFTankAbility extends RTFAbility {

    public RTFTankAbility(HyriRTF pl) {
        super(RTFAbilityModel.TANK,
                "tank",
                Material.IRON_CHESTPLATE,
                RTFAbilityType.EFFECT,
                6000,
                20);
    }

    @Override
    public void use(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 1), false);
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 3f, 1f);
    }
}
