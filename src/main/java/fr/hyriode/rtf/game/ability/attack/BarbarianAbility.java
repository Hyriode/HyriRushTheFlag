package fr.hyriode.rtf.game.ability.attack;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 21/05/2022 at 11:42
 */
public class BarbarianAbility extends RTFAbility implements Listener {

    public BarbarianAbility() {
        super(RTFAbilityModel.BARBARIAN,
                "barbarian",
                Material.IRON_SWORD,
                RTFAbilityType.ATTACK,
                24);

        HyriRTF.get().getServer().getPluginManager().registerEvents(this, HyriRTF.get());
    }

    @Override
    public void use(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 * 20, 0), false);
        player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 3f, 1f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();

        if (entity instanceof Player && damager instanceof Player) {
            final Player player = (Player) damager;

            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                event.setDamage(event.getDamage() / 0.75);
            }
        }
    }

}
