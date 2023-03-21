package fr.hyriode.rtf.api;

import java.util.Optional;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 18:26
 */
public enum RTFAbilityModel {

    ASTRONAUT,
    SHOOTER,
    RUNNER,
    KANGAROO,
    GHOST,
    BARBARIAN,
    TANK

    ;

    public static Optional<RTFAbilityModel> getByName(String name) {
        for (RTFAbilityModel ability : values()) {
            if (ability.name().equals(name)) {
                return Optional.of(ability);
            }
        }
        return Optional.empty();
    }

}
