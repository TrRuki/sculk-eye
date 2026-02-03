package com.trruki.sculk_eye;

import com.trruki.sculk_eye.block.ModBlockEntities;
import com.trruki.sculk_eye.block.ModBlocks;
import com.trruki.sculk_eye.network.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SculkEye implements ModInitializer {
    public static final String MOD_ID = "sculk_eye";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModBlockEntities.initialize();

        PayloadTypeRegistry.playC2S().register(
                ModPackets.SculkEyeBlockPayload.TYPE,
                ModPackets.SculkEyeBlockPayload.STREAM_CODEC
        );

        ModPackets.registerServer();
    }
}
