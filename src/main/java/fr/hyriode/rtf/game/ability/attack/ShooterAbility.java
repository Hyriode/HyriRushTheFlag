package fr.hyriode.rtf.game.ability.attack;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import net.minecraft.server.v1_8_R3.EntityFireball;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 19/03/2022 at 19:31
 */
public class ShooterAbility extends RTFAbility implements Listener {

    public ShooterAbility() {
        super(RTFAbilityModel.SHOOTER,
                "shooter",
                Material.FIREBALL,
                RTFAbilityType.ATTACK,
                20
        );

        HyriRTF.get().getServer().getPluginManager().registerEvents(this, HyriRTF.get());
    }

    @Override
    public void use(Player player) {
        final Vector direction = player.getEyeLocation().getDirection();
        Fireball fireball = player.launchProjectile(Fireball.class);

        fireball.setShooter(player);
        fireball.setYield(2.25F);
        fireball = this.setFireballDirection(fireball, direction);
        fireball.setVelocity(fireball.getDirection().multiply(3.5));
        fireball.setFireTicks(0);
        fireball.setIsIncendiary(false);
    }

    private Fireball setFireballDirection(Fireball fireball, Vector vector) {
        final EntityFireball fb = ((CraftFireball) fireball).getHandle();

        fb.dirX = vector.getX() * 0.1D;
        fb.dirY = vector.getY() * 0.1D;
        fb.dirZ = vector.getZ() * 0.1D;

        return (Fireball) fb.getBukkitEntity();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();

        if (entity instanceof Fireball) {
            event.setCancelled(true);
        }

        if (damager instanceof Fireball && entity instanceof Player) {
            final ProjectileSource source = ((Fireball) damager).getShooter();

            if (source instanceof Player) {
                final Player player = (Player) source;

                if (HyriRTF.get().getGame().getPlayerTeam(player).contains(entity.getUniqueId())) { // Cancel damage for team's member
                    event.setDamage(0.0D);
                    event.setCancelled(true);
                }
            }
        }
    }

}
