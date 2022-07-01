package fr.hyriode.rtf.game.ablity;

import fr.hyriode.api.transaction.IHyriTransactionContent;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.rtf.HyriRTF;
import fr.hyriode.rtf.api.ability.HyriRTFAbilityModel;
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
    protected static final Map<Class<? extends RTFAbility>, RTFAbility> abilityMap = new HashMap<>();

    private final HyriRTFAbilityModel model;
    private final String id;
    private final Material icon;
    private final RTFAbilityType type;
    private final int price;
    private final int cooldown;

    private final HyriLanguageMessage name;
    private final HyriLanguageMessage lore;

    public RTFAbility(HyriRTFAbilityModel model, String id, Material icon, RTFAbilityType type, int price, int cooldown) {
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

    public static void register(HyriRTF pl) {
        HyriRTF.log("Registering challenges...");

        /*  Add challenges here  */
        new RTFAstronautAbility(pl);
        new RTFShooterAbility(pl);
        new RTFRunnerAbility(pl);
        new RTFKangarooAbility(pl);
        new RTFGhostAbility(pl);
        new RTFBarbarianAbility(pl);
        new RTFTankAbility(pl);

        if(!abilityMap.isEmpty()) {
            abilityMap.values().forEach(ability -> HyriRTF.log("Registered ability:" +ability.getModel().name()));

            HyriRTF.log("Registered " +abilityMap.size()+ " abilities!");
        }
    }

    public static Map<Class<? extends RTFAbility>, RTFAbility> getAbilityMap() {
        return abilityMap;
    }

    public static List<RTFAbility> getAbilities() {
        return new ArrayList<>(abilityMap.values());
    }

    public static List<RTFAbility> getAbilities(RTFAbilityType type) {
        return getAbilities().stream().filter(rtfAbility -> rtfAbility.getType() == type).collect(Collectors.toList());
    }

    public static Optional<RTFAbility> getWithModel(HyriRTFAbilityModel model) {
        return abilityMap.values().stream().filter(ability -> ability.getModel() == model).findFirst();
    }

    public static Optional<RTFAbility> getWithId(String id) {
        return abilityMap.values().stream().filter(ability -> ability.getId().equalsIgnoreCase(id)).findFirst();
    }

    public HyriRTFAbilityModel getModel() {
        return this.model;
    }

    public String getId() {
        return this.id;
    }

    public String getName(Player player) {
        return this.name.getForPlayer(player);
    }

    public List<String> getLore(Player player) {
        final String str = this.lore.getForPlayer(player);
        final String[] splitLore = str.split("\n");

        return new ArrayList<>(Arrays.asList(splitLore));
    }

    public Material getIcon() {
        return this.icon;
    }

    public int getPrice() {
        return this.price;
    }

    public RTFAbilityType getType() {
        return this.type;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
