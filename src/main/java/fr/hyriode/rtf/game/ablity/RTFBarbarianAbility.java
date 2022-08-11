package fr.hyriode.rtf.game.ablity;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.HyriRTFAbilityModel;
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
public class RTFBarbarianAbility extends RTFAbility {

    public RTFBarbarianAbility(HyriRTF pl) {
        super(HyriRTFAbilityModel.BARBARIAN,
                "barbarian",
                Material.IRON_SWORD,
                RTFAbilityType.EFFECT,
                6000,
                24);

        abilityMap.put(RTFBarbarianAbility.class, this);
    }

    @Override
    public void use(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 * 20, 0), false);
        player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 3f, 1f);
    }
}
