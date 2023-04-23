package fr.hyriode.rtf.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.ThreadUtil;
import fr.hyriode.hyrame.utils.block.BlockUtil;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFHotBar;
import fr.hyriode.rtf.game.item.RTFAbilityItem;
import fr.hyriode.rtf.game.ui.scoreboard.RTFScoreboard;
import fr.hyriode.rtf.game.team.RTFGameTeam;
import fr.hyriode.rtf.util.RTFMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 14:31
 */
public class RTFFlag {

    public static final String METADATA_KEY = "RTFFlag";

    private Player holder;

    private final Supplier<List<Block>> blocks;
    private final Supplier<List<Location>> locations;

    private final RTFGameTeam team;
    private final HyriRTF plugin;

    public RTFFlag(HyriRTF plugin, RTFGameTeam team) {
        this.plugin = plugin;
        this.team = team;
        this.locations = () -> team.getConfig().getFlagsAsBukkit();
        this.blocks = () -> this.locations.get().stream().map(location -> IHyrame.WORLD.get().getBlockAt(location)).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public void place() {
        if (!this.team.hasLife()) {
            return;
        }

        ThreadUtil.backOnMainThread(HyriRTF.get(), () -> this.blocks.get().forEach(block -> {
            block.setType(Material.WOOL);
            block.setData(this.team.getColor().getDyeColor().getWoolData());
            block.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, this.team.getName()));
        }));
    }

    public void respawn() {
        final RTFGame game = this.plugin.getGame();

        this.place();

        for (RTFGamePlayer player : game.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.PISTON_EXTEND, 5f, 1f);
        }

        game.getPlayers().forEach(target ->
                target.getPlayer().sendMessage(" \n" + RTFMessage.FLAG_RESPAWN_MESSAGE.asString(target.getPlayer())
                        .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target)) + " \n"));
    }

    public void broughtBack() {
        if (this.holder != null) {
            final RTFGame game = this.plugin.getGame();
            final RTFGamePlayer gamePlayer = game.getPlayer(this.holder.getUniqueId());

            gamePlayer.spawn(false);
            gamePlayer.addFlagBroughtBack();

            this.team.removeLife();

            game.getPlayers().forEach(target ->  target.getPlayer().sendMessage("\n" + RTFMessage.FLAG_BROUGHT_BACK_MESSAGE.asString(target.getPlayer())
                    .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target))
                    .replace("%player%", this.getFormattedHolderName()) + "\n "));

            this.holder = null;

            for (RTFGamePlayer player : game.getPlayers()) {
                if (!player.isOnline()) {
                    return;
                }

                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 1f);
            }

            for (HyriGamePlayer target : this.team.getPlayers()) {
                final Player player = target.getPlayer();

                Title.sendTitle(player.getPlayer(),
                        HyriLanguageMessage.get("title.flag-destroyed").getValue(player),
                        HyriLanguageMessage.get("subtitle.flag-destroyed").getValue(player),
                        5, 3 * 20, 10);
            }

            IHyrame.get().getScoreboardManager().getScoreboards(RTFScoreboard.class).forEach(RTFScoreboard::update);

            if (!this.team.getOppositeTeam().hasLife()) {
                game.setFlagsAvailable(false);
            }
        }
    }

    public void lost() {
        if (this.holder != null) {
            this.respawn();

            this.holder = null;
        }
    }

    @SuppressWarnings("deprecation")
    public void capture(Player player) {
        final RTFGame game = this.plugin.getGame();
        final RTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if (gamePlayer == null) {
            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final byte data = this.team.getColor().getDyeColor().getData();

        for (RTFHotBar.Item item : gamePlayer.getData().getHotBar().getItems().keySet()) {
            for (int i = 0; i <= 9; i++) {
                if (inventory.getItem(i) != null) {
                    if (inventory.getItem(i).getType() == Material.getMaterial(item.getName())) {
                        gamePlayer.getData().getHotBar().setItem(item, i);
                    }
                }
            }
        }

        player.setGameMode(GameMode.ADVENTURE);

        this.holder = player;
        this.blocks.get().forEach(block -> block.setType(Material.AIR));

        gamePlayer.addCapturedFlag();

        inventory.clear();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemBuilder(Material.WOOL, 1, data).build());
        }

        inventory.setHelmet(new ItemBuilder(Material.WOOL, 1, data).build());

        game.getPlayers().forEach(target ->  target.getPlayer().sendMessage("\n" + RTFMessage.FLAG_CAPTURED_MESSAGE.asString(target.getPlayer())
                        .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target))
                        .replace("%player%", this.getFormattedHolderName())
                + " \n "));
        this.locations.get().forEach(location -> location.getWorld().strikeLightningEffect(location));

        if (gamePlayer.getAbility() != null) {
            this.plugin.getHyrame().getItemManager().giveItem(player, gamePlayer.getData().getHotBar().getSlot(RTFHotBar.Item.ABILITY_ITEM), RTFAbilityItem.class);
        }
    }

    private String getFormattedHolderName() {
        if (this.holder != null) {
            return this.plugin.getGame().getPlayer(this.holder.getUniqueId()).formatNameWithTeam();
        }
        return "";
    }

    public Player getHolder() {
        return this.holder;
    }

    public void resetHolder() {
        this.holder = null;
    }

    public List<Block> getBlocks() {
        return this.blocks.get();
    }

}
