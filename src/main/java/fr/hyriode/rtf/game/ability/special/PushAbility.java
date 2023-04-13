package fr.hyriode.rtf.game.ability.special;

import fr.hyriode.rtf.api.ability.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PushAbility extends RTFAbility {

    public PushAbility() {
        super(RTFAbilityModel.PUSH, "push", Material.PISTON_BASE, RTFAbilityType.SPECIAL, 3000, 20);
    }

    @Override
    public void use(Player player) {
        for (Entity entity : player.getNearbyEntities(5, 5, 5)){
            Vector push = player.getLocation().subtract(entity.getLocation()).toVector().normalize().multiply(-3);
            if(entity.getUniqueId() != player.getUniqueId()){
                entity.setVelocity(push);
            }
        }
    }
}
