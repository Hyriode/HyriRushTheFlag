package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.HyriGameType;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 20/04/2022 at 17:33
 */
public enum RTFGameType implements HyriGameType {

    SOLO("solo", 1),
    DOUBLES("doubles", 2),
    MDT("mdt", 4)
    ;

    private final String name;
    private final int teamSize;

    RTFGameType(String name, int teamSize) {
        this.name = name;
        this.teamSize = teamSize;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getTeamSize() {
        return this.teamSize;
    }
}
