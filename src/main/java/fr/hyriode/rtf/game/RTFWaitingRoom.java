package fr.hyriode.rtf.game;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFStatistics;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Function;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 17/07/2022 at 12:15
 */
public class RTFWaitingRoom extends HyriWaitingRoom {

    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc.data." + name);

    public RTFWaitingRoom(HyriGame<?> game) {
        super(game, Material.BANNER, HyriRTF.get().getConfiguration().getWaitingRoom());

        this.addLeaderboard(new Leaderboard(NetworkLeveling.LEADERBOARD_TYPE, "rushtheflag-experience",
                player -> HyriLanguageMessage.get("leaderboard.experience.display").getValue(player))
                .withScopes(HyriLeaderboardScope.DAILY, HyriLeaderboardScope.WEEKLY, HyriLeaderboardScope.MONTHLY));
        this.addLeaderboard(new Leaderboard("rushtheflag", "kills", player -> HyriLanguageMessage.get("leaderboard.kills.display").getValue(player)));
        this.addLeaderboard(new Leaderboard("rushtheflag", "victories", player -> HyriLanguageMessage.get("leaderboard.victories.display").getValue(player)));
        this.addLeaderboard(new Leaderboard("rushtheflag", "flags-brought-back", player -> HyriLanguageMessage.get("leaderboard.flags-brought-back.display").getValue(player)));

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
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime("rushtheflag#" + gameType.getName()))));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return playedTime < 1000 ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private RTFStatistics.Data getStatistics(RTFGameType gameType, IHyriPlayer account) {
        return ((RTFGamePlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

}
