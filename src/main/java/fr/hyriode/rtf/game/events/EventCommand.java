package fr.hyriode.rtf.game.events;

import fr.hyriode.rtf.HyriRTF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EventCommand implements CommandExecutor {

    private final HyriRTF plugin;

    public EventCommand(HyriRTF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("event")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length == 1) {
                    for(String eventName : Event.EVENTS.keySet()) {
                        if(args[0].equals(eventName)) {
                            Event.EVENTS.get(eventName).execute(Collections.singleton(this.plugin.getGame().getPlayer(player.getUniqueId())));
                            player.sendMessage("execute " + eventName);
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
