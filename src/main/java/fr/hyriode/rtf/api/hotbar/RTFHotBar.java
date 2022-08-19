package fr.hyriode.rtf.api.hotbar;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:35
 */
public class RTFHotBar {

    private final Map<Item, Integer> items;

    public RTFHotBar() {
        this.items = new HashMap<>();

        for (Item item : Item.values()) {
            this.items.put(item, item.getDefaultSlot());
        }
    }

    public void setItem(Item item, int slot) {
        this.items.put(item, slot);
    }

    public Integer getSlot(Item item) {
        final Object o = this.items.get(item);

        return o == null ? item.getDefaultSlot() : (int) o;
    }

    public Map<Item, Integer> getItems() {
        return this.items;
    }

    public enum Item {

        SWORD("IRON_SWORD", 0),
        PICKAXE("IRON_PICKAXE", 2),
        GOLDEN_APPLE("GOLDEN_APPLE", 3),
        ABILITY_ITEM("NETHER_STAR", 4),
        ;

        private final String name;
        private final int defaultSlot;

        Item(String name, int defaultSlot) {
            this.name = name;
            this.defaultSlot = defaultSlot;
        }

        public String getName() {
            return name;
        }

        public int getDefaultSlot() {
            return this.defaultSlot;
        }

    }

}