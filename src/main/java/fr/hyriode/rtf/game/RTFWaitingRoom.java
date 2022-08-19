package fr.hyriode.rtf.game;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.player.IHyriPlayer;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.rtf.api.statistics.RTFStatistics;
import fr.hyriode.rtf.api.statistics.RTFStatistics;
import org.bukkit.Material;

import java.util.UUID;
import java.util.function.Function;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 17/07/2022 at 12:15
 */
public class RTFWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc.data." + name);

    public RTFWaitingRoom(HyriGame<?> game) {
        super(game, Material.BANNER, createConfig());

        this.addStatistics(20, RTFGameType.SOLO);
        this.addStatistics(22, RTFGameType.DOUBLES);
        this.addStatistics(24, RTFGameType.MDT);
    }

    private void addStatistics(int slot, RTFGameType gameType) {
        final NPCCategory normal = new NPCCategory(new HyriLanguageMessage("").addValue(HyriLanguage.EN, gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("final-kills"), account -> String.valueOf(this.getStatistics(gameType, account).getFinalKills())));
        normal.addData(new NPCData(LANG_DATA.apply("deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeaths())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("captured-flags"), account -> String.valueOf(this.getStatistics(gameType, account).getCapturedFlags())));
        normal.addData(new NPCData(LANG_DATA.apply("flags-brought-back"), account -> String.valueOf(this.getStatistics(gameType, account).getFlagsBroughtBack())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, this.getStatistics(gameType, account).getPlayedTime())));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private RTFStatistics.Data getStatistics(RTFGameType gameType, IHyriPlayer account) {
        return ((RTFGamePlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

    private static Config createConfig() {
        return new Config(new LocationWrapper(0.5, 170.5, 0.5, -90, 0), new LocationWrapper(30, 222, -18), new LocationWrapper(-22, 160, 22), new LocationWrapper(5.5F, 170, -2.5F, 90, 0));
    }

}
