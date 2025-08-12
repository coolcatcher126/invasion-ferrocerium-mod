package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntBotMissileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AntBotMissileModel extends EntityModel<AntBotMissileEntity> {
    public static final EntityModelLayer ANT_BOT_MISSILE = new EntityModelLayer(Identifier.of(InvasionFerrocerium.MOD_ID, "ant_bot_missile"), "main");

    private final ModelPart bb_main;
    public AntBotMissileModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(0.0F, -4.0F, 5.0F, 0.0F, 8.0F, 3.0F, new Dilation(0.0F))
                .uv(12, 18).cuboid(-1.0F, -1.0F, 6.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData HorizontalFin_r1 = bb_main.addChild("HorizontalFin_r1", ModelPartBuilder.create().uv(6, 18).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 8.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 7.0F, 0.0F, 0.0F, -1.5708F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        bb_main.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public void setAngles(AntBotMissileEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}