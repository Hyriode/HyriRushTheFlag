package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.hotbar.HyriRTFHotBar;
import fr.hyriode.rtf.game.items.RTFAbilityItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 14:31
 */
public class RTFFlag {

    public static final String METADATA_KEY = "RTFFlag";

    private Player holder;

    private final List<Block> blocks;
    private final List<Location> locations;

    private final RTFGameTeam team;
    private final HyriRTF plugin;

    public RTFFlag(HyriRTF plugin, RTFGameTeam team) {
        this.plugin = plugin;
        this.team = team;
        this.locations = team.getConfig().getFlagsAsBukkit();

        this.blocks = new ArrayList<>();
        this.locations.forEach(location -> this.blocks.add(HyriRTF.WORLD.get().getBlockAt(location)));
    }

    public void place() {
        this.blocks.forEach(block -> {
            block.setType(Material.WOOL);

            final BlockState blockState = block.getState();
            final MaterialData data = blockState.getData();

            if (data instanceof Wool) {
                final Wool wool = (Wool) data;

                wool.setColor(team.getColor().getDyeColor());

                blockState.setData(data);
                blockState.update();
            }

            block.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, this.team.getName()));
        });
    }

    public void respawn() {
        final RTFGame game = this.plugin.getGame();

        this.place();

        for (RTFGamePlayer player : game.getPlayers()) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.PISTON_EXTEND, 5f, 1f);
        }

        game.sendMessageToAll(target ->  " \n" +
                RTFMessage.FLAG_RESPAWN_MESSAGE.asString(target)
                        .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target))
                + " \n ");
    }

    public void broughtBack() {
        if (this.holder != null) {
            final RTFGame game = this.plugin.getGame();
            final RTFGamePlayer gamePlayer = game.getPlayer(this.holder.getUniqueId());

            gamePlayer.spawn(false);
            gamePlayer.addFlagBroughtBack();

            this.team.removeLife();

            game.sendMessageToAll(target ->  " \n" +
                    RTFMessage.FLAG_BROUGHT_BACK_MESSAGE.asString(target)
                            .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target))
                            .replace("%player%", this.getFormattedHolderName())
                    + " \n ");

            this.holder = null;

            for (RTFGamePlayer player : game.getPlayers()) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 1f);
                player.getScoreboard().update();
            }

            if(!this.team.getOppositeTeam().hasLife()) {
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

    public void capture(Player player) {
        final RTFGame game = this.plugin.getGame();
        final RTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if (gamePlayer == null) {
            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final byte data = this.team.getColor().getDyeColor().getData();

        for (HyriRTFHotBar.Item item : gamePlayer.getAccount().getHotBar().getItems().keySet()) {
            if (gamePlayer.getAccount().getHotBar().getSlot(item) != null) {
                for (int i = 0; i <= 9; i++) {
                    if (inventory.getItem(i) != null) {
                        if (inventory.getItem(i).getType() == Material.getMaterial(item.getName())) {
                            gamePlayer.getAccount().getHotBar().setItem(item, i);
                        }
                    }
                }
            }
        }
        player.setGameMode(GameMode.ADVENTURE);

        this.holder = player;
        this.blocks.forEach(block -> block.setType(Material.AIR));

        gamePlayer.addCapturedFlag();

        inventory.clear();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemBuilder(Material.WOOL, 1, data).build());
        }

        inventory.setHelmet(new ItemBuilder(Material.WOOL, 1, data).build());

        game.sendMessageToAll(target ->  " \n" +
                RTFMessage.FLAG_CAPTURED_MESSAGE.asString(target)
                        .replace("%team%", this.team.getColor().getChatColor() + this.team.getDisplayName().getValue(target))
                        .replace("%player%", this.getFormattedHolderName())
                + " \n ");
        this.locations.forEach(location -> location.getWorld().strikeLightningEffect(location));

        this.plugin.getHyrame().getItemManager().giveItem(player, gamePlayer.getAccount().getHotBar().getSlot(HyriRTFHotBar.Item.ABILITY_ITEM), RTFAbilityItem.class);
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
        return this.blocks;
    }
}
