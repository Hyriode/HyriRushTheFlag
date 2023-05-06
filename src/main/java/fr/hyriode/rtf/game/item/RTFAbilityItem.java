package fr.hyriode.rtf.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.util.RTFMessage;
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
        super(plugin, "ability_item", () -> HyriLanguageMessage.get("item.ability.name"), null, Material.NETHER_STAR);
    }

    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        return new ItemBuilder(itemStack)
                .withName(this.display.get().getValue(player)
                        .replace("%ability%", this.plugin.getGame().getPlayer(player).getAbility().getName(player)))
                .withLore(this.plugin.getGame().getPlayer(player).getAbility().getLore(player))
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
            gamePlayer.getPlayer().sendMessage(RTFMessage.ABILITY_IN_COOLDOWN_MESSAGE.asString(player));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.8F, 0.1F);
        }
    }

    private void handleAction(RTFGamePlayer gamePlayer) {

        gamePlayer.getPlayer().sendMessage(RTFMessage.ABILITY_USED_MESSAGE.asString(gamePlayer.getPlayer())
                .replace("%ability%", gamePlayer.getAbility().getName(gamePlayer.getPlayer()))
        );

        gamePlayer.getAbility().use(gamePlayer.getPlayer());
        gamePlayer.handleCooldown(gamePlayer.getAbility().getCooldown());
    }

}
