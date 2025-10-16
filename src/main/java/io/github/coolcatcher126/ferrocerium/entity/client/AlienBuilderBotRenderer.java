package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class AlienBuilderBotRenderer extends BipedEntityRenderer<AlienBuilderBotEntity, AlienBuilderBotModel<AlienBuilderBotEntity>> {

    public AlienBuilderBotRenderer(EntityRendererFactory.Context context) {
        super(context, new AlienBuilderBotModel<>(context.getPart(AlienBuilderBotModel.ALIEN_BUILDER_BOT)), 0.5F);
    }

    @Override
    public Identifier getTexture(AlienBuilderBotEntity entity) {
        return Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/alien_builder_bot/alien_builder_bot.png");
    }
}