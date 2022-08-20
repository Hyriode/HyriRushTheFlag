package fr.hyriode.rtf.util;

import fr.hyriode.hyrame.game.util.value.HostValueModifier;
import fr.hyriode.hyrame.game.util.value.ValueProvider;

/**
 * Created by AstFaster
 * on 19/08/2022 at 12:34
 */
public class RTFValues {

    public static final ValueProvider<Long> RESPAWN_TIME = new ValueProvider<>(5L).addModifiers(new HostValueModifier<>(1, Long.class, "respawn-time"));
    public static final ValueProvider<Long> GAME_TIME = new ValueProvider<>(10 * 60L).addModifiers(new HostValueModifier<>(1, Long.class, "game-time"));
    public static final ValueProvider<Integer> LIVES = new ValueProvider<>(1).addModifiers(new HostValueModifier<>(1, Integer.class, "lives"));
    public static final ValueProvider<Boolean> REMOVE_BLOCKS = new ValueProvider<>(true).addModifiers(new HostValueModifier<>(1, Boolean.class, "remove-blocks"));
    public static final ValueProvider<Boolean> SPLEEF = new ValueProvider<>(false).addModifiers(new HostValueModifier<>(1, Boolean.class, "spleef"));

}
