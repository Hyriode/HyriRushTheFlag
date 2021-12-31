package fr.hyriode.rushtheflag.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HyriRTFGamePlayerHotbar {

    public static final Map<UUID, HyriRTFGamePlayerHotbar> hotbars = new HashMap<>();

    private int swordSlot;
    private int gapSlot;
    private int pickSlot;

    public HyriRTFGamePlayerHotbar(UUID uuid) {
        HyriRTFGamePlayerHotbar.hotbars.put(uuid, this);
        this.swordSlot = 0;
        this.gapSlot = 1;
        this.pickSlot = 2;
    }

    public int getSwordSlot() {
        return swordSlot;
    }

    public void setSwordSlot(int swordSlot) {
        this.swordSlot = swordSlot;
    }

    public int getGapSlot() {
        return gapSlot;
    }

    public void setGapSlot(int gapSlot) {
        this.gapSlot = gapSlot;
    }

    public int getPickSlot() {
        return pickSlot;
    }

    public void setPickSlot(int pickSlot) {
        this.pickSlot = pickSlot;
    }
}
