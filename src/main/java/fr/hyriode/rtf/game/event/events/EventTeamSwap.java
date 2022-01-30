package fr.hyriode.rtf.game.event.events;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.Event;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EventTeamSwap extends Event {

    public EventTeamSwap(HyriRTF plugin, String eventName) {
        super(plugin, eventName);
    }

    @Override
    public void execute(Collection<HyriRTFGamePlayer> players) {
        this.isRunning = true;
        this.plugin.getGame().sendMessageToAll(target -> HyriRTF.getLanguageManager().getValue(target, "message.event-swap"));

        List<HyriGamePlayer> team1 = this.plugin.getGame().getFirstTeam().getPlayers();
        List<HyriGamePlayer> team2 = this.plugin.getGame().getSecondTeam().getPlayers();

        if(team1.isEmpty()) {
            for(HyriGamePlayer player : team2) {
                if(!player.isDead()) {
                    this.teleport(player, this.plugin.getGame().getFirstTeam().getSpawnLocation());
                }
            }
        }else if(team2.isEmpty()) {
            for (HyriGamePlayer player : team1) {
                if(!player.isDead()) {
                    this.teleport(player, this.plugin.getGame().getSecondTeam().getSpawnLocation());
                }
            }
        }else {
            Collections.shuffle(team1);
            Collections.shuffle(team2);

            for(HyriGamePlayer player : team1) {
                if(!player.isDead()) {
                    HyriRTFGamePlayer player1 = this.plugin.getGame().getPlayer(team2.get(team1.indexOf(player)).getUUID());
                    if(player1 == null || player1.isDead()) {
                        this.teleport(player, this.plugin.getGame().getSecondTeam().getSpawnLocation());
                    }else {
                        Location playerLocation = player.getPlayer().getLocation();
                        this.teleport(player, player1.getPlayer().getLocation());
                        this.teleport(player1, playerLocation);
                    }
                }
            }
        }
        this.isRunning =  false;
    }

    private void teleport(HyriGamePlayer player, Location location) {
        player.getPlayer().teleport(location);
        player.getPlayer().setVelocity(new Vector(0,0,0));
    }
}
