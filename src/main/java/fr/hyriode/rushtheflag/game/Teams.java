package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.ChatColor;

public enum Teams {

    BLUE("blueTeam", new HyriLanguageMessage("game.teams.red.displayName").addValue(HyriLanguage.FR, ChatColor.BLUE + "Bleu").addValue(HyriLanguage.EN, ChatColor.BLUE + "Blue"), HyriGameTeamColor.BLUE, 1),
    RED("redTeam", new HyriLanguageMessage("game.teams.blue.displayName").addValue(HyriLanguage.FR, ChatColor.RED + "Rouge").addValue(HyriLanguage.EN, ChatColor.RED + "Red"), HyriGameTeamColor.RED, 1);

    private final String teamName;
    private final HyriLanguageMessage displayName;
    private final HyriGameTeamColor color;
    private final int teamSize;


    Teams(String teamName, HyriLanguageMessage displayName, HyriGameTeamColor color, int teamSize) {
        this.teamName = teamName;
        this.displayName = displayName;
        this.color = color;
        this.teamSize = teamSize;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public HyriLanguageMessage getDisplayName() {
        return displayName;
    }

    public HyriGameTeamColor getColor() {
        return color;
    }

    public int getTeamSize() {
        return teamSize;
    }

}
