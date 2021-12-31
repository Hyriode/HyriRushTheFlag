package fr.hyriode.rushtheflag.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HyriRTFGamePlayerHotbar {

    public static final Map<UUID, HyriRTFGamePlayerHotbar> hotbars = new HashMap<>();

    private int ironSwordSlot;
    private int goldenAppleSlot;
    private int ironPickaxeSlot;

    public HyriRTFGamePlayerHotbar(UUID uuid) {
        HyriRTFGamePlayerHotbar.hotbars.put(uuid, this);
        this.ironSwordSlot = 0;
        this.goldenAppleSlot = 1;
        this.ironPickaxeSlot = 2;
    }

    public int getIronSwordSlot() {
        return ironSwordSlot;
    }

    public void setIronSwordSlot(int ironSwordSlot) {
        this.ironSwordSlot = ironSwordSlot;
    }

    public int getGoldenAppleSlot() {
        return goldenAppleSlot;
    }

    public void setGoldenAppleSlot(int goldenAppleSlot) {
        this.goldenAppleSlot = goldenAppleSlot;
    }

    public int getIronPickaxeSlot() {
        return ironPickaxeSlot;
    }

    public void setIronPickaxeSlot(int ironPickaxeSlot) {
        this.ironPickaxeSlot = ironPickaxeSlot;
    }
}
