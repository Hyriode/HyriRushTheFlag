package fr.hyriode.rushtheflag.commands;

import fr.hyriode.rushtheflag.HyriRTF;
import fr.hyriode.rushtheflag.game.Teams;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    private final HyriRTF hyriRTF;

    public TestCommand(HyriRTF hyriRTF) {
        this.hyriRTF = hyriRTF;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("test")) {
            if(args.length == 0) {
                this.hyriRTF.getHyrame().getGameManager().getCurrentGame().startGame();
                sender.sendMessage(ChatColor.GREEN + "Command executée");
                return true;
            }else if(args[0].equalsIgnoreCase("red")) {
                this.hyriRTF.getBlueFlag().playerTakeFlag((Player) sender);
                this.hyriRTF.getHyriRTFMethods().captureFlag(hyriRTF.getHyrame().getGameManager().getCurrentGame().getTeam(Teams.RED.getTeamName()));
            }
        }
        return false;
    }
}
