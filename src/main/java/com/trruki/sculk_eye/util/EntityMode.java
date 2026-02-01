package com.trruki.sculk_eye.util;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;

public enum EntityMode implements StringRepresentable {
    PLAYER("player"), MOB("mob"), HOSTILE("hostile"), FRIENDLY("friendly"), CUSTOM("custom");

    private final String name;
    private final Component displayName;

    private EntityMode(String modeString) {
        this.name = modeString;
        this.displayName = Component.translatable("gui.sculk_eye.entity_mode_"+modeString);
    }

    public String getName() {
        return name;
    }


    public static EntityMode fromName(String modeString) {
        for (EntityMode em : EntityMode.values()) {
            if (Objects.equals(em.name, modeString)) {
                return em;
            }
        }
        return PLAYER;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }
}