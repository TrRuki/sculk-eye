package com.trruki.sculk_eye.block;

import com.trruki.sculk_eye.SculkEye;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModBlocks {
    public static final Block SCULK_EYE = register(
            "sculk_eye_block",
            SculkEyeBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.SCULK).strength(4f),
            true
    );


    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean registerItem) {
        ResourceKey<@NotNull Block> blockResourceKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockResourceKey));

        if (registerItem) {
            ResourceKey<@NotNull Item> itemResourceKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemResourceKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemResourceKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block);
    }

    private static ResourceKey<@NotNull Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SculkEye.MOD_ID, name));
    }

    private static ResourceKey<@NotNull Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SculkEye.MOD_ID, name));
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemGroup) -> {
            itemGroup.accept(ModBlocks.SCULK_EYE.asItem());
        });
    }
}
