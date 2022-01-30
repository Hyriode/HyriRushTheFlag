package fr.hyriode.rtf.game.event;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.game.HyriRTFGamePlayer;
import fr.hyriode.rtf.game.event.events.*;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Event {

    protected HyriRTF plugin;
    protected boolean isRunning;
    public static final Map<String, Event> EVENTS = new HashMap<>();

    public Event(HyriRTF plugin, String eventName) {
        this.plugin = plugin;
        this.isRunning = false;
        Bukkit.broadcastMessage(eventName);
        Event.EVENTS.put(eventName,this);
    }

    public static void registerEvents(HyriRTF plugin) {
        new EventTeamSwap(plugin, Events.TEAM_SWAP.name());
        new EventShadowBlocks(plugin, Events.SHADOW_BLOCK.name());
        new EventTNTRain(plugin, Events.TNT_RAIN.name());
        new EventFlagged(plugin, Events.FLAGGED.name());
        new EventSnowed(plugin, Events.SNOWED.name());
    }

    public abstract void execute(Collection<HyriRTFGamePlayer> players);

    public boolean isRunning() {
        return this.isRunning;
    }

    public void send(Object object){}
}
