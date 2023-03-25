package fr.hyriode.rtf.game.ui;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.util.RTFMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by AstFaster
 * on 25/03/2023 at 18:51
 */
public class RTFAbilityBar extends BukkitRunnable {

    private final RTFGamePlayer gamePlayer;

    public RTFAbilityBar(RTFGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void run() {
        if (!this.gamePlayer.isOnline() || this.gamePlayer.isDead()) {
            return;
        }

        final Player player = this.gamePlayer.getPlayer();

        ActionBar bar;
        if (this.gamePlayer.isCooldown()) {
            final int cooldownTime = this.gamePlayer.getCooldownTime();

            bar = new ActionBar(RTFMessage.ABILITY_WAITING_BAR.asString(player).replace("%time%", cooldownTime + "s"));

            player.setLevel(cooldownTime);

            if (cooldownTime == 0) {
                bar = new ActionBar(RTFMessage.ABILITY_READY_BAR.asString(player));

                this.gamePlayer.setCooldown(false);

                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.8F, 2.0F);
            }

            this.gamePlayer.setCooldownTime(cooldownTime - 1);
        } else {
            bar = new ActionBar(RTFMessage.ABILITY_READY_BAR.asString(player));
        }

        bar.send(player);
    }

}
