package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienHelicopterBotEntity;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AlienHelicopterBotModel<T extends AlienHelicopterBotEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer ALIEN_HELICOPTER_BOT = new EntityModelLayer(Identifier.of(InvasionFerrocerium.MOD_ID, "alien_helicopter_bot"), "main");
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tail_rotor;
    private final ModelPart main_rotor;

    public AlienHelicopterBotModel(ModelPart root) {
        this.body = root.getChild("body");
        this.tail = this.body.getChild("tail");
        this.tail_rotor = this.tail.getChild("tail_rotor");
        this.main_rotor = this.body.getChild("main_rotor");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-24.0F, -32.0F, -8.0F, 48.0F, 32.0F, 48.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = body.addChild("cube_r1", ModelPartBuilder.create().uv(124, 80).cuboid(-24.0F, -11.0F, 0.0F, 48.0F, 22.0F, 23.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, -8.0F, -16.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData tail = body.addChild("tail", ModelPartBuilder.create().uv(0, 80).cuboid(-1.0F, -32.0F, 40.0F, 2.0F, 8.0F, 60.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r2 = tail.addChild("cube_r2", ModelPartBuilder.create().uv(124, 139).cuboid(-1.0F, -17.0F, -1.0F, 2.0F, 17.0F, 17.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, -31.0F, 86.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData tail_rotor = tail.addChild("tail_rotor", ModelPartBuilder.create().uv(28, 148).cuboid(-1.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -32.0F, 98.0F));

        ModelPartData rotor_blade2_r1 = tail_rotor.addChild("rotor_blade2_r1", ModelPartBuilder.create().uv(14, 148).cuboid(0.0F, -11.0F, -1.0F, 2.0F, 11.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 0.0F, 3.1416F, 0.1745F, 0.0F));

        ModelPartData rotor_blade1_r1 = tail_rotor.addChild("rotor_blade1_r1", ModelPartBuilder.create().uv(0, 148).cuboid(0.0F, -11.0F, -1.0F, 2.0F, 11.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        ModelPartData main_rotor = body.addChild("main_rotor", ModelPartBuilder.create().uv(40, 148).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -32.0F, 16.0F));

        ModelPartData rotor_blade2_r2 = main_rotor.addChild("rotor_blade2_r2", ModelPartBuilder.create().uv(124, 132).cuboid(0.0F, -2.0F, -1.0F, 48.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, 0.0F, -0.1745F, 3.1416F, 0.0F));

        ModelPartData rotor_blade1_r2 = main_rotor.addChild("rotor_blade1_r2", ModelPartBuilder.create().uv(124, 125).cuboid(0.0F, -2.0F, -1.0F, 48.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, 0.0F, -0.1745F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void setAngles(AlienHelicopterBotEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

//        this.animateMovement(AntSoldierBotAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
//        this.updateAnimation(entity.idleAnimationState, AntSoldierBotAnimations.IDLE, ageInTicks, 1f);
//        this.updateAnimation(entity.rangedAttackAnimationState, AntSoldierBotAnimations.ATTACK, ageInTicks, 1f);//Do attack animation.
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        body.render(matrices, vertexConsumer, light, overlay, color);
    }
    @Override
    public ModelPart getPart() {
        return body;
    }
}
