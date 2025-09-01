package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienHelicopterBotEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AlienHelicopterBotRenderer  extends MobEntityRenderer<AlienHelicopterBotEntity, AlienHelicopterBotModel<AlienHelicopterBotEntity>> {
    public AlienHelicopterBotRenderer(EntityRendererFactory.Context context) {
        super(context, new AlienHelicopterBotModel<>(context.getPart(AlienHelicopterBotModel.ALIEN_HELICOPTER_BOT)), 0.75F);
    }

    @Override
    public Identifier getTexture(AlienHelicopterBotEntity entity) {
        return Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/alien_helicopter_bot/alien_helicopter_bot.png");
    }
}
