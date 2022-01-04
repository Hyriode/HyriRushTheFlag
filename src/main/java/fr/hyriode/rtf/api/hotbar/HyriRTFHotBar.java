package fr.hyriode.rtf.api.hotbar;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:35
 */
public class HyriRTFHotBar {

    public static Map<Item, Material> HOTBAR_ITEMS;

    static {
        HOTBAR_ITEMS = new HashMap<>();
        HOTBAR_ITEMS.put(Item.SWORD, Material.IRON_SWORD);
        HOTBAR_ITEMS.put(Item.PICKAXE, Material.IRON_PICKAXE);
        HOTBAR_ITEMS.put(Item.GOLDEN_APPLE, Material.GOLDEN_APPLE);
    }

    private final Map<Item, Integer> items;

    public HyriRTFHotBar() {
        this.items = new HashMap<>();
        
        this.items.put(Item.SWORD, 0);
        this.items.put(Item.PICKAXE, 2);
        this.items.put(Item.GOLDEN_APPLE, 3);
    }

    public void setItem(Item item, int slot) {
        this.items.put(item, slot);
    }

    public Integer getSlot(Item item) {
        return this.items.get(item);
    }

    public Map<Item, Integer> getItems() {
        return this.items;
    }

    public enum Item {
        SWORD,
        PICKAXE,
        GOLDEN_APPLE
    }

}
