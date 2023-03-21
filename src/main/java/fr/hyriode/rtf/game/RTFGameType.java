package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.HyriGameType;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 20/04/2022 at 17:33
 */
public enum RTFGameType implements HyriGameType {

    SOLO("Solo", 1, 2, 2),
    DOUBLES("Doubles", 2, 4, 4),
    MDT("4v4", 4, 6, 8),
    ;

    private final String displayName;
    private final int teamSize;
    private final int minPlayers;
    private final int maxPlayers;

    RTFGameType(String displayName, int teamSize, int minPlayers, int maxPlayers) {
        this.displayName = displayName;
        this.teamSize = teamSize;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getTeamSize() {
        return this.teamSize;
    }

}
