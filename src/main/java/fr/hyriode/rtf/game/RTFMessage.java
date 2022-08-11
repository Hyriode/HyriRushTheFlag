package fr.hyriode.rtf.game;

import fr.hyriode.api.language.HyriLanguageMessage;
import org.bukkit.entity.Player;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 20/04/2022 at 17:26
 */
public enum RTFMessage {

    ABILITY_WAITING_BAR("bar.waiting.display"),
    ABILITY_READY_BAR("bar.ready.display"),

    ABILITY_IN_COOLDOWN_MESSAGE("message.ability-in-cooldown.display"),
    ABILITY_USED_MESSAGE("message.ability-used.display"),

    FLAG_CAPTURED_MESSAGE("message.flag-captured.display"),
    FLAG_RESPAWN_MESSAGE("message.flag-respawn.display"),
    FLAG_BROUGHT_BACK_MESSAGE("message.flag-brought-back.display"),
    ENDING_GAME_MESSAGE("message.ending-game.display"),
    END_GAME_MESSAGE("message.end-game.display"),

    ERROR_PLACE_BLOCK_MESSAGE("message.error-place-block.display"),
    ERROR_SPLEEF_MESSAGE("message.spleef.display"),
    ERROR_BREAK_FLAG_MESSAGE("message.error-break-flag.display"),

    LAST_ABILITY_MESSAGE("message.last-ability.display"),
    ;

    private final String key;

    private HyriLanguageMessage languageMessage;

    RTFMessage(String key) {
        this.key = key;
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(Player player) {
        return this.asLang().getValue(player);
    }
}
