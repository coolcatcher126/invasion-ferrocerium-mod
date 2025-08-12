package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AntScoutBotRenderer extends MobEntityRenderer<AntScoutBotEntity, AntScoutBotModel<AntScoutBotEntity>> {

    public AntScoutBotRenderer(EntityRendererFactory.Context context) {
        super(context, new AntScoutBotModel<>(context.getPart(AntScoutBotModel.ANT_SCOUT_BOT)), 0.75F);
    }

    @Override
    public Identifier getTexture(AntScoutBotEntity entity) {
        return Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/ant_scout_bot/ant_scout_bot.png");
    }
}