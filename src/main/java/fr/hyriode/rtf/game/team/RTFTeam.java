package fr.hyriode.rtf.game.team;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 31/12/2021 at 18:43
 */
public enum RTFTeam {

    BLUE("blue", HyriGameTeamColor.BLUE),
    RED("red", HyriGameTeamColor.RED),
    GREEN("green", HyriGameTeamColor.GREEN),
    YELLOW("yellow",HyriGameTeamColor.YELLOW),
    AQUA("aqua", HyriGameTeamColor.CYAN),
    PINK("pink", HyriGameTeamColor.PINK),
    WHITE("white", HyriGameTeamColor.WHITE),
    BLACK("black", HyriGameTeamColor.BLACK);

    private static final List<Pair<RTFTeam, RTFTeam>> PAIRS = Arrays.asList(new Pair<>(BLUE, RED), new Pair<>(GREEN, YELLOW), new Pair<>(AQUA, PINK), new Pair<>(WHITE, BLACK));

    private final String name;
    private final HyriGameTeamColor color;
    private final Supplier<HyriLanguageMessage> displayName;

    RTFTeam(String name, HyriGameTeamColor color) {
        this.name = name;
        this.color = color;
        this.displayName = () -> HyriLanguageMessage.get("team." + this.name + ".display");
    }

    public String getName() {
        return this.name;
    }

    public HyriGameTeamColor getColor() {
        return this.color;
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName.get();
    }

    public static Pair<RTFTeam, RTFTeam> get() {
        return PAIRS.get(ThreadLocalRandom.current().nextInt(PAIRS.size()));
    }

}
