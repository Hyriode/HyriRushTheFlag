package fr.hyriode.rtf.game.items;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.RTFMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 18:32
 */
public class RTFAbilityItem extends HyriItem<HyriRTF> {

    public RTFAbilityItem(HyriRTF plugin) {
        super(plugin, "ability_item", () -> HyriRTF.getLanguageManager().getMessage("item.ability.name"), Material.NETHER_STAR);
    }

    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        return new ItemBuilder(itemStack)
                .withName(this.displayName.get().getForPlayer(player)
                        .replace("%ability%", this.plugin.getGame().getPlayer(player).getAbility().getName(player)))
                .build();
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        RTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player.getUniqueId());

        if (gamePlayer == null) {
            return;
        }

        if (!gamePlayer.isCooldown()) {
            this.handleAction(gamePlayer);
        } else {
            gamePlayer.sendMessage(RTFMessage.ABILITY_IN_COOLDOWN_MESSAGE.asString(player));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.8F, 0.1F);
        }
    }

    private void handleAction(RTFGamePlayer gamePlayer) {

        gamePlayer.sendMessage(RTFMessage.ABILITY_USED_MESSAGE.asString(gamePlayer.getPlayer())
                .replace("%ability%", gamePlayer.getAbility().getName(gamePlayer.getPlayer()))
        );

        gamePlayer.getAbility().use(gamePlayer.getPlayer());
        gamePlayer.handleCooldown(gamePlayer.getAbility().getCooldown());
    }

}
