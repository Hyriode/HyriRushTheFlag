package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.rtf.HyriRTF;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 01/01/2022 at 14:31
 */
public class HyriRTFFlag {

    public static final String METADATA_KEY = "RTFFlag";

    private Player holder;

    private final Block block;
    private final Location location;

    private final HyriRTFGameTeam team;
    private final HyriRTF plugin;

    public HyriRTFFlag(HyriRTF plugin, HyriRTFGameTeam team) {
        this.plugin = plugin;
        this.team = team;
        this.location = this.team.getConfig().getFlag();
        this.block = HyriRTF.WORLD.get().getBlockAt(this.location);
    }

    public void place() {
        this.block.setType(Material.WOOL);

        final BlockState blockState = this.block.getState();
        final MaterialData data = blockState.getData();

        if (data instanceof Wool) {
            final Wool wool = (Wool) data;

            wool.setColor(team.getColor().getDyeColor());

            blockState.setData(data);
            blockState.update();
        }

        this.block.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, this.team.getName()));
    }

    public void respawn() {
        final HyriRTFGame game = this.plugin.getGame();

        this.place();

        game.sendMessageToAll(target -> ChatColor.GRAY + HyriRTF.getLanguageManager().getValue(target, "message.flag-respawn")
                .replace("%team%", this.team.getFormattedDisplayName(target) + ChatColor.GRAY));
    }

    public void broughtBack() {
        if (this.holder != null) {
            final IHyriLanguageManager languageManager = HyriRTF.getLanguageManager();
            final HyriRTFGame game = this.plugin.getGame();
            final HyriRTFGamePlayer gamePlayer = game.getPlayer(this.holder.getUniqueId());
            final HyriRTFGameTeam playerTeam = gamePlayer.getTeam();

            game.sendMessageToAll(target -> this.getFormattedHolderName() + ChatColor.GRAY + languageManager.getValue(target, "message.flag-brought-back"));

            gamePlayer.spawn();
            gamePlayer.addFlagBroughtBack();

            if (playerTeam.hasLife()) {
                playerTeam.addLife();
            }

            this.team.removeLife();

            if (this.team.hasLife()) {
                this.respawn();
            } else {
                this.team.sendTitle(target -> ChatColor.DARK_RED + languageManager.getValue(target, "title.no-life-team"), target -> ChatColor.RED + languageManager.getValue(target, "subtitle.no-life-team"), 0, 60, 5);

                game.sendMessageToAll(target -> ChatColor.GRAY + languageManager.getValue(target, "message.no-life-team")
                        .replace("%team%", this.team.getFormattedDisplayName(target) + ChatColor.GRAY));
            }

            this.holder = null;
        }
    }

    public void lost() {
        if (this.holder != null) {
            this.plugin.getGame().sendMessageToAll(target -> this.getFormattedHolderName() + ChatColor.GRAY + HyriRTF.getLanguageManager().getValue(target, "message.flag-lost"));

            this.respawn();

            this.holder = null;
        }
    }

    public void capture(Player player) {
        final HyriRTFGame game = this.plugin.getGame();
        final HyriRTFGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());
        final IHyriLanguageManager languageManager = HyriRTF.getLanguageManager();
        final PlayerInventory inventory = player.getInventory();
        final byte data = this.team.getColor().getDyeColor().getData();

        player.setGameMode(GameMode.ADVENTURE);

        this.holder = player;

        this.block.setType(Material.AIR);

        gamePlayer.addCapturedFlag();

        inventory.clear();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemBuilder(Material.WOOL, 1, data).build());
        }

        inventory.setHelmet(new ItemBuilder(Material.WOOL, 1, data).build());

        game.sendMessageToAll(target -> this.getFormattedHolderName() + ChatColor.GRAY + languageManager.getValue(player, "message.flag-captured"));

        final String title = ChatColor.DARK_AQUA + languageManager.getValue(player, "title.flag-captured");
        final String subtitle = ChatColor.AQUA + languageManager.getValue(player, "subtitle.flag-captured");

        Title.sendTitle(player, title, subtitle, 0, 60, 5);

        this.team.sendTitle(target -> ChatColor.DARK_AQUA + languageManager.getValue(target, "title.team-flag-captured"), target -> "", 0, 50, 5);
    }

    private String getFormattedHolderName() {
        if (this.holder != null) {
            return this.plugin.getGame().getPlayer(this.holder.getUniqueId()).getTeam().formatName(this.holder);
        }
        return "";
    }

    public Player getHolder() {
        return this.holder;
    }

}
