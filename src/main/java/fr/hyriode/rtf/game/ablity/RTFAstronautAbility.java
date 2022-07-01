package fr.hyriode.rtf.game.ablity;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.HyriRTFAbilityModel;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:19
 */
public class RTFAstronautAbility extends RTFAbility {

    public RTFAstronautAbility(HyriRTF pl) {
        super(HyriRTFAbilityModel.ASTRONAUT,
                "astronaut",
                Material.FIREWORK,
                RTFAbilityType.MOVEMENT,
                20000,
                20);

        abilityMap.put(RTFAstronautAbility.class, this);
    }

    @Override
    public void use(Player player) {
        Vector normal = player.getEyeLocation().getDirection();
        normal.setY(0.75 + Math.abs(normal.getY()) * 0.55);
        player.setVelocity(normal);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 3f, 3f);
    }
}
