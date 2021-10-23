package fr.hyriode.rushtheflag.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;

public enum Teams {

    BLUE("blueTeam", new LanguageMessage("team.blue").addValue(Language.FR, "Team bleue"), HyriGameTeamColor.BLUE, 1),
    RED("redTeam", new LanguageMessage("team.red").addValue(Language.FR, "Team rouge"), HyriGameTeamColor.RED, 1);

    private final String teamName;
    private final LanguageMessage displayName;
    private final HyriGameTeamColor color;
    private final int teamSize;


    Teams(String teamName, LanguageMessage displayName, HyriGameTeamColor color, int teamSize) {
        this.teamName = teamName;
        this.displayName = displayName;
        this.color = color;
        this.teamSize = teamSize;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public LanguageMessage getDisplayName() {
        return displayName;
    }

    public HyriGameTeamColor getColor() {
        return color;
    }

    public int getTeamSize() {
        return teamSize;
    }

}
