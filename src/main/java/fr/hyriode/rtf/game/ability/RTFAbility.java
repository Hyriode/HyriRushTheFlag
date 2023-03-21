package fr.hyriode.rtf.game.ability;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.RTFAbilityModel;
import fr.hyriode.rtf.game.ability.attack.RTFShooterAbility;
import fr.hyriode.rtf.game.ability.attack.RTFTankAbility;
import fr.hyriode.rtf.game.ability.effect.RTFBarbarianAbility;
import fr.hyriode.rtf.game.ability.effect.RTFGhostAbility;
import fr.hyriode.rtf.game.ability.effect.RTFRunnerAbility;
import fr.hyriode.rtf.game.ability.movement.RTFAstronautAbility;
import fr.hyriode.rtf.game.ability.movement.RTFKangarooAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 17:17
 */
public abstract class RTFAbility {

    private static final Map<Class<? extends RTFAbility>, RTFAbility> ABILITY = new HashMap<>();

    protected boolean enabled = true;

    protected final RTFAbilityModel model;
    protected final String id;
    protected final Material icon;
    protected final RTFAbilityType type;
    protected final int price;
    protected int cooldown;

    protected final HyriLanguageMessage name;
    protected final HyriLanguageMessage lore;

    public RTFAbility(RTFAbilityModel model, String id, Material icon, RTFAbilityType type, int price, int cooldown) {
        this.model = model;
        this.id = id;
        this.icon = icon;
        this.type = type;
        this.price = price;
        this.cooldown = cooldown;
        this.name = HyriLanguageMessage.get("ability." + this.id + ".name");
        this.lore = HyriLanguageMessage.get("ability." + this.id + ".lore");
    }

    public abstract void use(Player player);

    public static void init(HyriRTF pl) {
        HyriRTF.log("Registering abilities...");

        /*  Add challenges here  */
        registerAbility(new RTFAstronautAbility(pl));
        registerAbility(new RTFShooterAbility(pl));
        registerAbility(new RTFRunnerAbility(pl));
        registerAbility(new RTFKangarooAbility(pl));
        registerAbility(new RTFGhostAbility(pl));
        registerAbility(new RTFBarbarianAbility(pl));
        registerAbility(new RTFTankAbility(pl));

        if (!ABILITY.isEmpty()) {
            HyriRTF.log("Registered " + ABILITY.size() + " abilities!");
        }
    }

    public static void registerAbility(RTFAbility ability) {
        HyriRTF.log("Registered '" + ability.getModel().name() + "' ability.");

        ABILITY.put(ability.getClass(), ability);
    }

    public static Map<Class<? extends RTFAbility>, RTFAbility> getAbility() {
        return ABILITY;
    }

    public static List<RTFAbility> getAbilities() {
        return new ArrayList<>(ABILITY.values());
    }

    public static List<RTFAbility> getAbilities(RTFAbilityType type) {
        return getAbilities().stream().filter(rtfAbility -> rtfAbility.getType() == type).collect(Collectors.toList());
    }

    public static List<RTFAbility> getEnabledAbilities() {
        final List<RTFAbility> abilities = new ArrayList<>();

        for (RTFAbility ability : ABILITY.values()) {
            if (ability.isEnabled()) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    public static List<RTFAbility> getEnabledAbilities(RTFAbilityType type) {
        final List<RTFAbility> abilities = new ArrayList<>();

        for (RTFAbility ability : ABILITY.values()) {
            if (ability.isEnabled() && ability.getType() == type) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    public static Optional<RTFAbility> getWithModel(RTFAbilityModel model) {
        return ABILITY.values().stream().filter(ability -> ability.getModel() == model).findFirst();
    }

    public static Optional<RTFAbility> getWithId(String id) {
        return ABILITY.values().stream().filter(ability -> ability.getId().equalsIgnoreCase(id)).findFirst();
    }

    public RTFAbilityModel getModel() {
        return this.model;
    }

    public String getId() {
        return this.id;
    }

    public String getName(Player player) {
        return this.name.getValue(player);
    }

    public List<String> getLore(Player player) {
        final String str = this.lore.getValue(player);
        final String[] splitLore = str.split("\n");

        return new ArrayList<>(Arrays.asList(splitLore));
    }

    public Material getIcon() {
        return this.icon;
    }

    public RTFAbilityType getType() {
        return this.type;
    }

    public int getPrice() {
        return this.price;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
