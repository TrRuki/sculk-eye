package com.trruki.sculk_eye.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SculkEyeBlockEntity extends BlockEntity {
    public enum EntityMode {
        PLAYER("player"), MOB("mob"), CUSTOM("custom");

        private final String modeString;

        EntityMode(String  modeString) {
            this.modeString = modeString;
        }

        public String getModeString() {
            return modeString;
        }

        public static EntityMode fromCode(String modeString) {
            for (EntityMode em : EntityMode.values()) {
                if (Objects.equals(em.modeString, modeString)) {
                    return em;
                }
            }
            return PLAYER;
        }
    }


    EntityMode entityMode = EntityMode.PLAYER;
    int radius = 20;
    String customEntityType = "player";

    public SculkEyeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCULK_EYE_BLOCK_ENTITY, pos, state);
    }

    public void setEntityMode(EntityMode newEntityType){
        this.entityMode = newEntityType;
    }

    private Predicate<? super LivingEntity> getEntityPredicate() {
        return switch (this.entityMode) {
            case PLAYER -> e -> {return e.getType() == EntityType.PLAYER;};
            case MOB -> e -> true;
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
        output.putInt("radius", radius);
        output.putString("customEntityType", customEntityType);
        output.putString("entityMode", entityMode.getModeString());

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        radius = input.getIntOr("radius", radius);
        customEntityType = input.getStringOr("customEntityType", customEntityType);
        entityMode = EntityMode.fromCode(input.getStringOr("entityMode", entityMode.getModeString()));
    }
}
