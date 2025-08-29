package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntBotMissileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class AntBotMissileRenderer extends EntityRenderer<AntBotMissileEntity>{
    protected AntBotMissileModel model;

    public AntBotMissileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new AntBotMissileModel(ctx.getPart(AntBotMissileModel.ANT_BOT_MISSILE));
    }

    @Override
    public void render(AntBotMissileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0d, -1.25d, 0d);
        //Try to rotate to face correct direction
        //TODO: Make the missile face in the correct direction
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) + 180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));

        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/ant_bot_missile/ant_bot_missile.png")), false, false);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(AntBotMissileEntity entity) {
        return Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/ant_bot_missile/ant_bot_missile.png");
    }
}
