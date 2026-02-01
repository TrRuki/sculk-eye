package com.trruki.sculk_eye.screen;

import com.google.common.collect.ImmutableList;
import com.trruki.sculk_eye.block.SculkEyeBlockEntity;
import com.trruki.sculk_eye.util.EntityMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SculkEyeBlockScreen extends Screen {
    private int radius;
    private String customEntityType;
    private EntityMode initialEntityMode;
    private final SculkEyeBlockEntity blockEntity;

    private static final ImmutableList<EntityMode> ENTITY_MODES = ImmutableList.copyOf(EntityMode.values());

    private EditBox customEntityTypeEdit;

    public SculkEyeBlockScreen(SculkEyeBlockEntity sculkEyeBlockEntity) {
        super(Component.translatable("block.sculk_eye.sculk_eye_block"));
        blockEntity = sculkEyeBlockEntity;
    }

    private void onDone() {
        // TODO: sent stuff to server
        this.minecraft.setScreen(null);
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).bounds(this.width / 2 - 20 - 80, this.height / 2 + 10, 80, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onCancel()).bounds(this.width / 2 + 20, this.height / 2 + 10, 80, 20).build());
        this.initialEntityMode = blockEntity.getEntityMode();
        this.customEntityType = blockEntity.getCustomEntityType();
        this.radius = blockEntity.getRadius();

        this.addRenderableWidget(
                CycleButton.builder(entityMode -> Component.translatable("gui.sculk_eye.entity_mode_"+entityMode.getName()), this.initialEntityMode)
                        .withValues(ENTITY_MODES)
                        .displayOnlyValue()
                        .create(this.width / 2 - 100, this.height / 2 - 80, 60, 20, Component.literal("ENTITY MODE"), (cycleButton, entityMode) -> {
                            this.blockEntity.setEntityMode(entityMode);
                            updateEntityMode(entityMode);
                        })
        );

        this.customEntityTypeEdit = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 25, 200, 20, Component.literal("custom entity type"));
        this.customEntityTypeEdit.setMaxLength(100);
        this.customEntityTypeEdit.setValue(this.customEntityType);
        this.addRenderableWidget(this.customEntityTypeEdit);

        this.updateEntityMode(initialEntityMode);
    }

    private void updateEntityMode(EntityMode entityMode) {
        this.customEntityTypeEdit.setVisible(false);
        switch (entityMode) {
            case CUSTOM -> {
                this.customEntityTypeEdit.setVisible(true);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        EntityMode entityMode = this.blockEntity.getEntityMode();

        guiGraphics.drawString(this.font, Component.translatable("gui.sculk_eye.entity_mode"), this.width / 2 - 100, this.height / 2 - 95, -6250336);

        switch (entityMode) {
            case CUSTOM -> {
                guiGraphics.drawString(this.font, Component.translatable("gui.sculk_eye.custom_entity_type"), this.width / 2 - 100, this.height / 2 - 40, -6250336);
            }
        }
    }
}
