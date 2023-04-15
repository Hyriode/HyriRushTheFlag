package fr.hyriode.rtf.game.ability.movement;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 19:19
 */
public class AstronautAbility extends RTFAbility {

    public AstronautAbility(HyriRTF pl) {
        super(RTFAbilityModel.ASTRONAUT,
                "astronaut",
                Material.FIREWORK,
                RTFAbilityType.MOVEMENT,
                22);
    }

    @Override
    public void use(Player player) {
        Vector normal = player.getEyeLocation().getDirection();
        normal.setY(0.75 + Math.abs(normal.getY()) * 0.55);
        player.setVelocity(normal);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 3f, 3f);
    }
}
