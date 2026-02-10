package com.trruki.sculk_eye.block;

import com.trruki.sculk_eye.util.EntityMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class SculkEyeBlockEntity extends BlockEntity {
    EntityMode entityMode = EntityMode.PLAYER;
    double radius = 20;
    String customEntityType = "minecraft:player";

    public SculkEyeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCULK_EYE_BLOCK_ENTITY, pos, state);
    }

    public EntityMode getEntityMode(){
        return this.entityMode;
    }

    public void setEntityMode(EntityMode newEntityType){
        this.entityMode = newEntityType;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getCustomEntityType() {
        return this.customEntityType;
    }

    public void setCustomEntityType(String customEntityType) {
        try {
            Optional<Holder.Reference<EntityType<?>>> entityTypeOptional = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(customEntityType));
            EntityType<? extends LivingEntity > entityType = (EntityType<? extends LivingEntity>) entityTypeOptional.get().value();

            this.customEntityType = customEntityType;
        } catch (Exception ignored){
        }

    }

    private Predicate<? super LivingEntity> getEntityPredicate() {
        return switch (this.entityMode) {
            case PLAYER -> e -> {return e.getType() == EntityType.PLAYER;};
            case MOB -> e -> {return e.getType() != EntityType.PLAYER;};
            case CUSTOM -> {
                Optional<Holder.Reference<EntityType<?>>> entityTypeOptional = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(customEntityType));
                if (entityTypeOptional.isEmpty()) {
                    yield e -> false;
                } else {
                    EntityType<? extends LivingEntity > entityType = (EntityType<? extends LivingEntity>) entityTypeOptional.get().value();
                    yield e -> {
                        return e.getType() == entityType;
                    };
                }
            }
            case HOSTILE -> e -> {return !e.getType().getCategory().isFriendly();};
            case FRIENDLY -> e -> {return e.getType().getCategory().isFriendly();};
        };
    }

    private int getEntityCount(ServerLevel level, BlockPos pos, double radius, Predicate<? super LivingEntity> predicate) {
        AABB box = new AABB(pos).inflate(radius);

        List<? extends LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, predicate);
        return entities.size();
    }

    private int getSignal(ServerLevel level, BlockPos pos) {
        int entityCount = getEntityCount(level, pos, radius, getEntityPredicate());
        return Math.min(15, entityCount);
    }

    public static void serverTick(SculkEyeBlockEntity sculkEyeBlockEntity, ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        if (serverLevel == null || serverLevel.isClientSide()) return;
        if (serverLevel.getGameTime() % 4 != 0) return;

        serverLevel.setBlock(blockPos, blockState.setValue(SculkEyeBlock.POWER, sculkEyeBlockEntity.getSignal(serverLevel, blockPos)), 3);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putDouble("radius", radius);
        output.putString("custom_entity_type", customEntityType);
        output.putString("entity_mode", entityMode.getName());

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        radius = input.getDoubleOr("radius", radius);
        customEntityType = input.getStringOr("custom_entity_type", customEntityType);
        entityMode = EntityMode.fromName(input.getStringOr("entity_mode", entityMode.getName()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}