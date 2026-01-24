package com.trruki.sculk_eye.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SculkEyeBlockEntity extends BlockEntity {
    public enum EntityType {
        PLAYER, ANY
    }


    private EntityType entityType = EntityType.PLAYER;
    private int RADIUS = 20;

    public SculkEyeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCULK_EYE_BLOCK_ENTITY, pos, state);
    }

    public void setEntityType(EntityType newEntityType){
        this.entityType = newEntityType;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    private int getEntityCount(ServerLevel level, BlockPos pos, double radius, Class<? extends LivingEntity> mobClass) {
        AABB box = new AABB(pos).inflate(radius);

        List<? extends LivingEntity> entities = level.getEntitiesOfClass(mobClass, box, LivingEntity::isAlive);
        return entities.size();
    }

    private int getSignal(ServerLevel level, BlockPos pos) {
        int entityCount = getEntityCount(level, pos, RADIUS, Zombie.class);
        return Math.min(15, entityCount);
    }

    public static void serverTick(SculkEyeBlockEntity sculkEyeBlockEntity, ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        if (serverLevel == null || serverLevel.isClientSide()) return;
        if (serverLevel.getGameTime() % 4 != 0) return;

        serverLevel.setBlock(blockPos, blockState.setValue(SculkEyeBlock.POWER, sculkEyeBlockEntity.getSignal(serverLevel, blockPos)), 3);
    }
}
