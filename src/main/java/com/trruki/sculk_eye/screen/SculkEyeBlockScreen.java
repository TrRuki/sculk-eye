package com.trruki.sculk_eye.screen;

import com.google.common.collect.ImmutableList;
import com.trruki.sculk_eye.block.SculkEyeBlockEntity;
import com.trruki.sculk_eye.network.ModPackets;
import com.trruki.sculk_eye.util.EntityMode;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SculkEyeBlockScreen extends Screen {
    private double initialRadius;
    private EntityMode entityMode;
    private final SculkEyeBlockEntity blockEntity;

    private static final ImmutableList<EntityMode> ENTITY_MODES = ImmutableList.copyOf(EntityMode.values());

    private EditBox customEntityTypeEdit;
    private EditBox radiusEdit;
    private CycleButton<EntityMode> entityModeCycleButton;

    public SculkEyeBlockScreen(SculkEyeBlockEntity sculkEyeBlockEntity) {
        super(Component.translatable("block.sculk_eye.sculk_eye_block"));
        blockEntity = sculkEyeBlockEntity;
    }

    private void onDone() {
        this.sendToServer();
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
        this.entityMode = blockEntity.getEntityMode();
        String initialCustomEntityType = blockEntity.getCustomEntityType();
        this.initialRadius = blockEntity.getRadius();

        this.entityModeCycleButton = CycleButton.builder(newEntityMode -> Component.translatable("gui.sculk_eye.entity_mode_"+newEntityMode.getName()), this.entityMode)
                .withValues(ENTITY_MODES)
                .displayOnlyValue()
                .create(this.width / 2 - 100, this.height / 2 - 80, 60, 20, Component.literal("entity mode"), (cycleButton, newEntityMode) -> {
                    this.entityMode = newEntityMode;
                    updateEntityMode(newEntityMode);
                });

        this.addRenderableWidget(
                this.entityModeCycleButton
        );

        this.customEntityTypeEdit = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 25, 200, 20, Component.literal("custom entity type"));
        this.customEntityTypeEdit.setMaxLength(100);
        this.customEntityTypeEdit.setValue(initialCustomEntityType);
        this.addRenderableWidget(this.customEntityTypeEdit);

        this.radiusEdit = new EditBox(this.font, this.width / 2 + 100 - 60, this.height / 2 - 80, 60, 20, Component.literal("radius"));
        this.radiusEdit.setMaxLength(7);
        this.radiusEdit.setValue(String.valueOf(initialRadius));
        this.addRenderableWidget(this.radiusEdit);


        this.updateEntityMode(this.entityMode);
    }

    private void updateEntityMode(EntityMode newEntityMode) {
        this.customEntityTypeEdit.setVisible(false);
        switch (newEntityMode) {
            case CUSTOM -> {
                this.customEntityTypeEdit.setVisible(true);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(this.font, Component.translatable("gui.sculk_eye.entity_mode"), this.width / 2 - 100, this.height / 2 - 95, -6250336);
        guiGraphics.drawString(this.font, Component.translatable("gui.sculk_eye.radius"), this.width / 2 + 100 - 60, this.height / 2 - 95, -6250336);

        switch (this.entityMode) {
            case CUSTOM -> {
                guiGraphics.drawString(this.font, Component.translatable("gui.sculk_eye.custom_entity_type"), this.width / 2 - 100, this.height / 2 - 40, -6250336);
            }
        }
    }

    private void sendToServer() {
        double radius;

        try {
            radius = Double.parseDouble(this.radiusEdit.getValue());
        } catch (NumberFormatException ignored) {
            radius = this.initialRadius;
        }

        if (radius > 100) {
            radius = this.initialRadius;
        }

        ModPackets.SculkEyeBlockPayload payload = new ModPackets.SculkEyeBlockPayload(
                this.blockEntity.getBlockPos(),
                this.entityMode.getName(),
                radius,
                this.customEntityTypeEdit.getValue()
        );
        ClientPlayNetworking.send(payload);
    }
}
