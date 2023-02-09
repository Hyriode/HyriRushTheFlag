package fr.hyriode.rtf.listener;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.RTFGame;
import fr.hyriode.rtf.game.RTFGamePlayer;
import fr.hyriode.rtf.game.team.RTFGameTeam;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 27/06/2022 at 20:54
 */
public class RTFGameListener extends HyriListener<HyriRTF> {

    public RTFGameListener(HyriRTF plugin) {
        super(plugin);
    }

    @HyriEventHandler
    public void onReconnection(HyriGameReconnectEvent event) {
        final RTFGamePlayer player = (RTFGamePlayer) event.getGamePlayer();
        final RTFGameTeam team = (RTFGameTeam) player.getTeam();

        if(team.hasLife()) {
            event.allow();
        } else {
            event.disallow();
        }
    }

    @HyriEventHandler
    public void onReconnection(HyriGameReconnectedEvent event) {
        final RTFGamePlayer player = (RTFGamePlayer) event.getGamePlayer();
        final RTFGame game = this.plugin.getGame();

        game.getProtocolManager().getProtocol(HyriDeathProtocol.class).runDeath(HyriGameDeathEvent.Reason.VOID, player.getPlayer());
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final RTFGame game = (RTFGame) event.getGame();
        final HyriGameSpectator spectator = event.getSpectator();

        spectator.getPlayer().teleport(game.getWaitingRoom().getConfig().getSpawn().asBukkit());
    }

}
