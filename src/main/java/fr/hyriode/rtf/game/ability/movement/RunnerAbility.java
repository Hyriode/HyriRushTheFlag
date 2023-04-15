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
public class RunnerAbility extends RTFAbility {

    public RunnerAbility(HyriRTF pl) {
        super(RTFAbilityModel.RUNNER,
                "runner",
                Material.SUGAR,
                RTFAbilityType.MOVEMENT,
                15
        );
    }

    @Override
    public void use(Player player) {
        player.playSound(player.getLocation(), Sound.DRINK, 3f, 1f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 1), false);
    }
}
