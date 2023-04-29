package fr.hyriode.rtf.game.ability.special;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PushAbility extends RTFAbility {

    public PushAbility() {
        super(RTFAbilityModel.PUSH, "push", Material.PISTON_BASE, RTFAbilityType.SPECIAL, 20);
    }

    @Override
    public void use(Player player) {
        Stream<Entity> playerStream = player.getNearbyEntities(3, 3, 3)
                .stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> HyriRTF.get().getGame().getPlayer(entity.getUniqueId()) != null);

        if (!HyriRTF.get().getGame().getPlayer(player.getUniqueId()).getTeam().isFriendlyFire()) {
            playerStream = playerStream.filter(entity -> !HyriRTF.get().getGame().getPlayer(entity.getUniqueId()).getTeam().contains(player.getUniqueId()));
        }

        for (Entity entity : playerStream.collect(Collectors.toList())) {
            final Vector push = player.getLocation().subtract(entity.getLocation()).toVector().normalize().multiply(-3);

            if (entity.getUniqueId() != player.getUniqueId()){
                entity.setVelocity(push);
            }
        }
    }
}
