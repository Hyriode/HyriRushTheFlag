package fr.hyriode.rtf.api.hotbar;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:35
 */
public class HyriRTFHotBar {

    private final Map<Item, Integer> items;

    public HyriRTFHotBar() {
        this.items = new HashMap<>();

        this.items.put(Item.SWORD, 0);
        this.items.put(Item.PICKAXE, 2);
        this.items.put(Item.GOLDEN_APPLE, 3);
        this.items.put(Item.ABILITY_ITEM, 4);
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
        SWORD("IRON_SWORD"),
        PICKAXE("IRON_PICKAXE"),
        GOLDEN_APPLE("GOLDEN_APPLE"),
        ABILITY_ITEM("NETHER_STAR"),
        ;

        private final String name;

        Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}