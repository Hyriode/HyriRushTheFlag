package fr.hyriode.rtf.game.abilities;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 18:32
 */
public class AbilityItem extends HyriItem<HyriRTF> {

    public AbilityItem(HyriRTF plugin) {
        super(plugin, "ability_item", () -> HyriRTF.getLanguageManager().getMessage("item.ability.name"), Material.NETHER_STAR);
    }

    @Override
    public void onGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        player.getInventory().setItem(slot, new ItemBuilder(itemStack).withName(this.displayName.get().getForPlayer(player)
                .replace("%ability%", HyriRTF.getLanguageManager().getValue(player, this.plugin.getGame().getPlayer(player.getUniqueId()).getAbility().getNameKey()))
        ).build());
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        HyriRTFGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player.getUniqueId());

        if (!gamePlayer.isCooldown()) {
            this.handleAction(gamePlayer);
        } else {
            HyriLanguageMessage message = new HyriLanguageMessage("message.in-cooldown")
                    .addValue(HyriLanguage.FR, ChatColor.RED + "La capacité n'est pas utilisable pour l'instant.")
                    .addValue(HyriLanguage.EN, ChatColor.RED + "The ability is in cooldown.");
            gamePlayer.sendMessage(message.getForPlayer(player));
        }
    }

    private void handleAction(HyriRTFGamePlayer gamePlayer) {

        gamePlayer.sendMessage(HyriRTF.getLanguageManager().getValue(gamePlayer.getPlayer(), "message.ability-activated")
                .replace("%ability%", HyriRTF.getLanguageManager().getValue(gamePlayer.getPlayer(), gamePlayer.getAbility().getNameKey()))
        );

        gamePlayer.getAbility().use(gamePlayer.getPlayer());
        gamePlayer.handleCooldown(gamePlayer.getAbility().getCooldown());
    }




}
