package fr.hyriode.rtf.game.ability.special;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.RTFAbilityModel;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.ability.RTFAbility;
import fr.hyriode.rtf.game.ability.RTFAbilityType;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyriode-Development
 * Created by Akkashi
 * on 21/05/2022 at 11:34
 */
public class RTFGhostAbility extends RTFAbility {

    private final HyriRTF plugin;

    public RTFGhostAbility(HyriRTF pl) {
        super(RTFAbilityModel.GHOST,
                "ghost",
                Material.GHAST_TEAR,
                RTFAbilityType.SPECIAL,
                15000,
                30);

        this.plugin = pl;
    }

    @Override
    public void use(Player player) {
        final RTFGame game = this.plugin.getGame();
        final RTFGamePlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        // HIDE PLAYER
        this.hide(player);

        new BukkitRunnable() {
            private int index = 4;

            @Override
            public void run() {
                player.getWorld().playEffect(player.getLocation(), Effect.FOOTSTEP, Effect.FOOTSTEP.getData());

                if (gamePlayer.isDead()) {
                    this.cancel();
                }

                if (this.index == 0) {
                    show(player);
                    this.cancel();
                }

                this.index--;
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }

    private void hide(Player player) {
        final RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player);
        final RTFGameTeam oppositeTeam = ((RTFGameTeam) gamePlayer.getTeam()).getOppositeTeam();

        for (HyriGamePlayer oppositeTeamPlayer : oppositeTeam.getPlayers()) {
            final Player oppositePlayer = oppositeTeamPlayer.getPlayer();

            PlayerUtil.hideArmor(player, oppositePlayer);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2, false, false));
        this.plugin.getHyrame().getTabListManager().hideNameTag(player);

        player.playSound(player.getLocation(), Sound.FIZZ, 1f, 4f);
    }

    private void show(Player player) {
        final RTFGame game = this.plugin.getGame();

        for (RTFGamePlayer gamePlayer : game.getPlayers()) {
            final Player target = gamePlayer.getPlayer();

            PlayerUtil.showArmor(player, target);
        }

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.plugin.getHyrame().getTabListManager().showNameTag(player);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 4f);
    }
}
