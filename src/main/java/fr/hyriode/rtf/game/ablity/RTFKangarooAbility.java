package fr.hyriode.rtf.game.ablity;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.HyriRTFAbilityModel;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 21/05/2022 at 11:21
 */
public class RTFKangarooAbility extends RTFAbility {

    public RTFKangarooAbility(HyriRTF pl) {
        super(HyriRTFAbilityModel.KANGAROO,
                "kangaroo",
                Material.RABBIT_FOOT,
                RTFAbilityType.MOVEMENT,
                8000,
                26);

        abilityMap.put(RTFKangarooAbility.class, this);
    }

    @Override
    public void use(Player player) {
        player.setVelocity(new Vector(player.getVelocity().getX(), 2.0, player.getVelocity().getZ()));
        player.playSound(player.getLocation(), Sound.FALL_BIG, 3f, 1f);
    }
}
