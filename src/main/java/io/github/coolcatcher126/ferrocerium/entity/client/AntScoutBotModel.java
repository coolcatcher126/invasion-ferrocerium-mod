package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AntScoutBotModel<T extends AntScoutBotEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer ANT_SCOUT_BOT = new EntityModelLayer(Identifier.of(InvasionFerrocerium.MOD_ID, "ant_scout_bot"), "main");
    private final ModelPart ant_bot;
    private final ModelPart head;

    public AntScoutBotModel(ModelPart root) {
        this.ant_bot = root.getChild("ant_bot");
        this.head = this.ant_bot.getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData ant_bot = modelPartData.addChild("ant_bot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData abdomen = ant_bot.addChild("abdomen", ModelPartBuilder.create().uv(0, 0).cuboid(-11.0F, -11.0F, -4.0F, 12.0F, 9.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 0.0F, 0.0F));

        ModelPartData thorax = ant_bot.addChild("thorax", ModelPartBuilder.create().uv(36, 33).cuboid(-8.0F, -8.0F, -10.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 0.0F, 0.0F));

        ModelPartData head = ant_bot.addChild("head", ModelPartBuilder.create().uv(0, 33).cuboid(-7.0F, -6.0F, -1.0F, 8.0F, 6.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.0F, -19.0F));

        ModelPartData left_mandible = ant_bot.addChild("left_mandible", ModelPartBuilder.create().uv(36, 59).cuboid(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -2.0F, -20.0F));

        ModelPartData left_mandible_2_r1 = left_mandible.addChild("left_mandible_2_r1", ModelPartBuilder.create().uv(46, 59).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(-0.001F)), ModelTransform.of(-2.1728F, 0.001F, -6.2958F, 0.0F, 0.4363F, 0.0F));

        ModelPartData right_mandible = ant_bot.addChild("right_mandible", ModelPartBuilder.create().uv(56, 59).cuboid(0.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -2.0F, -20.0F));

        ModelPartData right_mandible_3_r1 = right_mandible.addChild("right_mandible_3_r1", ModelPartBuilder.create().uv(60, 33).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(-0.001F)), ModelTransform.of(2.1728F, 0.001F, -6.2958F, 0.0F, -0.4363F, 0.0F));

        ModelPartData left_rear_leg = ant_bot.addChild("left_rear_leg", ModelPartBuilder.create().uv(36, 45).cuboid(0.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.0F, -6.0F));

        ModelPartData left_rear_leg_mid = left_rear_leg.addChild("left_rear_leg_mid", ModelPartBuilder.create().uv(40, 53).cuboid(0.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData left_rear_leg_tip = left_rear_leg_mid.addChild("left_rear_leg_tip", ModelPartBuilder.create().uv(36, 57).cuboid(0.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData right_rear_leg = ant_bot.addChild("right_rear_leg", ModelPartBuilder.create().uv(0, 53).cuboid(-8.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -2.0F, -6.0F));

        ModelPartData right_rear_leg_mid = right_rear_leg.addChild("right_rear_leg_mid", ModelPartBuilder.create().uv(0, 57).cuboid(-8.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));

        ModelPartData right_rear_leg_tip = right_rear_leg_mid.addChild("right_rear_leg_tip", ModelPartBuilder.create().uv(0, 59).cuboid(-8.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));

        ModelPartData left_mid_leg = ant_bot.addChild("left_mid_leg", ModelPartBuilder.create().uv(0, 49).cuboid(0.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.0F, -8.0F));

        ModelPartData left_mid_leg_mid = left_mid_leg.addChild("left_mid_leg_mid", ModelPartBuilder.create().uv(40, 55).cuboid(0.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData left_mid_leg_tip = left_mid_leg_mid.addChild("left_mid_leg_tip", ModelPartBuilder.create().uv(54, 57).cuboid(0.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData right_mid_leg = ant_bot.addChild("right_mid_leg", ModelPartBuilder.create().uv(20, 53).cuboid(-8.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -2.0F, -8.0F));

        ModelPartData right_mid_leg_mid = right_mid_leg.addChild("right_mid_leg_mid", ModelPartBuilder.create().uv(18, 57).cuboid(-8.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));

        ModelPartData right_mid_leg_tip = right_mid_leg_mid.addChild("right_mid_leg_tip", ModelPartBuilder.create().uv(18, 59).cuboid(-8.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));

        ModelPartData left_front_leg = ant_bot.addChild("left_front_leg", ModelPartBuilder.create().uv(20, 49).cuboid(0.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.0F, -10.0F));

        ModelPartData left_front_leg_mid = left_front_leg.addChild("left_front_leg_mid", ModelPartBuilder.create().uv(56, 45).cuboid(0.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData left_front_leg_tip = left_front_leg_mid.addChild("left_front_leg_tip", ModelPartBuilder.create().uv(58, 53).cuboid(0.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 0.0F));

        ModelPartData right_front_leg = ant_bot.addChild("right_front_leg", ModelPartBuilder.create().uv(40, 49).cuboid(-8.0F, -1.5F, 0.0F, 8.0F, 1.5F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -2.0F, -10.0F));

        ModelPartData right_front_leg_mid = right_front_leg.addChild("right_front_leg_mid", ModelPartBuilder.create().uv(56, 47).cuboid(-8.0F, -1.375F, 0.125F, 8.0F, 1.25F, 1.25F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));

        ModelPartData right_front_leg_tip = right_front_leg_mid.addChild("right_front_leg_tip", ModelPartBuilder.create().uv(58, 55).cuboid(-8.0F, -1.25F, 0.25F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(AntScoutBotEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.animateMovement(AntScoutBotAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, AntScoutBotAnimations.IDLE, ageInTicks, 1f);
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        ant_bot.render(matrices, vertexConsumer, light, overlay, color);
    }
    @Override
    public ModelPart getPart() {
        return ant_bot;
    }
}