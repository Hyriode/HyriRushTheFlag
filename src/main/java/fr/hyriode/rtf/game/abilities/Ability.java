package fr.hyriode.rtf.game.abilities;

import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.abilities.HyriRTFAbilityModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Project: HyriRushTheFlag
 * Created by Akkashi
 * on 11/03/2022 at 17:17
 */
public abstract class Ability {

    protected static final Map<Class<? extends Ability>, Ability> abilityMap = new HashMap<>();

    private final HyriRTFAbilityModel model;
    private final String nameKey;
    private final String[] loreKey;
    private final Material icon;
    private final int cooldown;

    public Ability(HyriRTFAbilityModel model, String nameKey, String[] loreKey, Material icon, int cooldown) {
        this.model = model;
        this.nameKey = nameKey;
        this.loreKey = loreKey;
        this.icon = icon;
        this.cooldown = cooldown;
    }

    public abstract void use(Player player);

    public static void register(HyriRTF pl) {
        HyriRTF.log("Registering challenges...");

        /*  Add challenges here  */
        new AstronautAbility(pl);
        new ShooterAbility(pl);
        new RunnerAbility(pl);
       // new BuilderAbility(pl);

        if(!abilityMap.isEmpty()) {
            abilityMap.values().forEach(ability -> HyriRTF.log("Registered ability:" +ability.getModel().name()));

            HyriRTF.log("Registered " +abilityMap.size()+ " abilities!");
        }
    }

    public static Optional<Ability> getWithModel(HyriRTFAbilityModel model) {
        return abilityMap.values().stream().filter(ability -> ability.getModel() == model).findFirst();
    }

    public HyriRTFAbilityModel getModel() {
        return this.model;
    }

    public String getNameKey() {
        return this.nameKey;
    }

    public String[] getLoreKey() {
        return this.loreKey;
    }

    public Material getIcon() {
        return this.icon;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
