package fr.hyriode.rtf.game.ability.movement;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 19/03/2022 at 19:31
 */
public class RTFRunnerAbility extends RTFAbility {

    public RTFRunnerAbility(HyriRTF pl) {
        super(RTFAbilityModel.RUNNER,
                "runner",
                Material.SUGAR,
                RTFAbilityType.MOVEMENT,
                0,
                20
        );
    }

    @Override
    public void use(Player player) {
        player.playSound(player.getLocation(), Sound.DRINK, 3f, 1f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2), false);
    }
}
