package fr.hyriode.rushtheflag;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    private final HyriRTF hyriRTF;

    public TestCommand(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("test")) {
            this.hyriRTF.getHyrame().getGameManager().getCurrentGame().startGame();
            sender.sendMessage(ChatColor.GREEN + "Command executée");
            return true;
        }
        return false;
    }
}
