package com.trruki.sculk_eye;

import com.trruki.sculk_eye.block.ModBlockEntities;
import com.trruki.sculk_eye.block.ModBlocks;
import net.fabricmc.api.ModInitializer;

public class SculkEye implements ModInitializer {
    public static final String MOD_ID = "sculk_eye";

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModBlockEntities.initialize();
    }
}
