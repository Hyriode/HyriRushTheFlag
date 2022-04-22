package fr.hyriode.rtf.game.abilities;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import net.minecraft.server.v1_8_R3.EntityFireball;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 19/03/2022 at 19:31
 */
public class RTFShooterAbility extends RTFAbility {
    public RTFShooterAbility(HyriRTF pl) {
        super(HyriRTFAbilityModel.SHOOTER,
                "ability.shooter.name",
                new String[] {"ability.shooter.lore.1", "ability.shooter.lore.2"},
                Material.FIREBALL,
                16
        );
        abilityMap.put(RTFShooterAbility.class, this);
    }

    @Override
    public void use(Player player) {
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(2.25F);
        Vector direction = player.getEyeLocation().getDirection();
        fireball = this.setFireballDirection(fireball, direction);
        fireball.setVelocity(fireball.getDirection().multiply(3.5));
    }

    private Fireball setFireballDirection(Fireball fireball, Vector vector) {
        EntityFireball fb = ((CraftFireball) fireball).getHandle();
        fb.dirX = vector.getX() * 0.1D;
        fb.dirY = vector.getY() * 0.1D;
        fb.dirZ = vector.getZ() * 0.1D;
        return (Fireball) fb.getBukkitEntity();
    }
}
