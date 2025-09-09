package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AlienBuilderBotModel<T extends AlienBuilderBotEntity> extends BipedEntityModel<T> {
    public static final EntityModelLayer ALIEN_BUILDER_BOT = new EntityModelLayer(Identifier.of(InvasionFerrocerium.MOD_ID, "alien_builder_bot"), "main");

    public AlienBuilderBotModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = getModelData(Dilation.NONE, 0);
        return TexturedModelData.of(modelData, 64, 64);
    }
}