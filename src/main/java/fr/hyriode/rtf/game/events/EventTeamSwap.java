package fr.hyriode.rtf.game.events;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
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
        if(!team1.isEmpty()) {
            Collections.shuffle(team1);
            Collections.shuffle(team2);

            for(HyriGamePlayer player : team1) {
                if(!player.isDead()) {
                    if(!team2.isEmpty()) {
                        HyriRTFGamePlayer player1 = this.plugin.getGame().getPlayer(team2.get(team1.indexOf(player)).getUUID());
                        if(player1 == null || player1.isDead()) {
                            player.getPlayer().teleport(this.plugin.getGame().getSecondTeam().getSpawnLocation());
                            player.getPlayer().setVelocity(new Vector(0,0,0));
                        }else {
                            Location playerLocation = player.getPlayer().getLocation();
                            player.getPlayer().teleport(player1.getPlayer().getLocation());
                            player.getPlayer().setVelocity(new Vector(0,0,0));
                            player1.getPlayer().teleport(playerLocation);
                            player1.getPlayer().setVelocity(new Vector(0,0,0));
                        }
                    }else {
                        player.getPlayer().teleport(this.plugin.getGame().getSecondTeam().getSpawnLocation());
                    }
                }
            }
        }else {
            for(HyriGamePlayer player : team2) {
                if(!player.isDead()) {
                    player.getPlayer().teleport(this.plugin.getGame().getSecondTeam().getSpawnLocation());
                    player.getPlayer().setVelocity(new Vector(0,0,0));
                }
            }
        }
        this.isRunning =  false;
    }
}
