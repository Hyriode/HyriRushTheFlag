package fr.hyriode.rtf.game.abilities;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 19/03/2022 at 19:31
 */
public class RunnerAbility extends Ability {

    public RunnerAbility(HyriRTF pl) {
        super(HyriRTFAbilityModel.RUNNER,
                "ability.runner.name",
                new String[] {"ability.runner.lore.1", "ability.runner.lore.2"},
                Material.SUGAR,
                10
        );
        abilityMap.put(RunnerAbility.class, this);
    }

    @Override
    public void use(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2), false);
    }
}
