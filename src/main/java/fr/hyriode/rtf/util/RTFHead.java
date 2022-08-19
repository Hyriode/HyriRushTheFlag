package fr.hyriode.rtf.util;

import fr.hyriode.hyrame.item.ItemHead;

/**
 * Created by AstFaster
 * on 19/08/2022 at 15:55
 */
public enum RTFHead implements ItemHead {

    DEATH("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI2YWM0NjY4ZjI3YzNkZDM2N2NhMjg4ZTMyNjBjOTg3MjAxMzVhZDI4Njc4NWY2ZDFiNWY0ZjU2YjkxZGUxIn19fQ=="),

    ;

    private final String texture;

    RTFHead(String texture) {
        this.texture = texture;
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

}
