package com.trruki.sculk_eye.network;

import com.trruki.sculk_eye.SculkEye;
import com.trruki.sculk_eye.block.SculkEyeBlockEntity;
import com.trruki.sculk_eye.util.EntityMode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModPackets {
    public record SculkEyeBlockPayload(
            BlockPos pos,
            String entityMode,
            double radius,
            String customEntityType

    ) implements CustomPacketPayload {
        public static final Type<SculkEyeBlockPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(SculkEye.MOD_ID, "sculk_eyy_block_payload"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<FriendlyByteBuf, SculkEyeBlockPayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, payload) -> {
                            buf.writeBlockPos(payload.pos());
                            buf.writeUtf(payload.entityMode);
                            buf.writeDouble(payload.radius);
                            buf.writeUtf(payload.customEntityType);
                        },
                        buf -> new SculkEyeBlockPayload(
                                buf.readBlockPos(),
                                buf.readUtf(),
                                buf.readDouble(),
                                buf.readUtf()
                        )
                );
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(SculkEyeBlockPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                Player player = context.player();
                BlockPos pos = payload.pos();

                BlockEntity blockEntity = player.level().getBlockEntity(pos);
                if (!(blockEntity instanceof SculkEyeBlockEntity sculkEyeBlockEntity)) return;
                if (!player.blockPosition().closerThan(pos, 10)) return;
                assert sculkEyeBlockEntity.getLevel() != null;

                sculkEyeBlockEntity.setEntityMode(EntityMode.fromName(payload.entityMode));
                sculkEyeBlockEntity.setRadius(payload.radius);
                sculkEyeBlockEntity.setCustomEntityType(payload.customEntityType);
                sculkEyeBlockEntity.setChanged();
                sculkEyeBlockEntity.getLevel().sendBlockUpdated(
                        pos,
                        sculkEyeBlockEntity.getBlockState(),
                        sculkEyeBlockEntity.getBlockState(),
                        Block.UPDATE_ALL
                );
            });
        });
    }
}
