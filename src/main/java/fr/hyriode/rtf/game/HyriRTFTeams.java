package fr.hyriode.rtf.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.rtf.HyriRTF;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:43
 */
public enum HyriRTFTeams {

    BLUE("blue", HyriGameTeamColor.BLUE),
    RED("red", HyriGameTeamColor.RED),
    GREEN("green", HyriGameTeamColor.GREEN),
    YELLOW("yellow",HyriGameTeamColor.YELLOW),
    AQUA("aqua", HyriGameTeamColor.CYAN),
    PINK("pink", HyriGameTeamColor.PINK),
    WHITE("white", HyriGameTeamColor.WHITE),
    BLACK("black", HyriGameTeamColor.GRAY);

    private static final List<Pair<HyriRTFTeams, HyriRTFTeams>> PAIRS = Arrays.asList(new Pair<>(BLUE, RED), new Pair<>(GREEN, YELLOW), new Pair<>(AQUA, PINK), new Pair<>(WHITE, BLACK));

    private final String name;
    private final HyriGameTeamColor color;
    private final Supplier<HyriLanguageMessage> displayName;

    HyriRTFTeams(String name, HyriGameTeamColor color) {
        this.name = name;
        this.color = color;
        this.displayName = () -> HyriRTF.getLanguageManager().getMessage("team." + this.name + ".display");
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

    public static Pair<HyriRTFTeams, HyriRTFTeams> get() {
        return PAIRS.get(ThreadLocalRandom.current().nextInt(PAIRS.size()));
    }

}
