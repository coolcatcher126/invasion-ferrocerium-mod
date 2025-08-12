package io.github.coolcatcher126.ferrocerium.entity.client;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntSoldierBotEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class AntSoldierBotRenderer extends MobEntityRenderer<AntSoldierBotEntity, AntSoldierBotModel<AntSoldierBotEntity>> {

    public AntSoldierBotRenderer(EntityRendererFactory.Context context) {
        super(context, new AntSoldierBotModel<>(context.getPart(AntSoldierBotModel.ANT_SOLDIER_BOT)), 0.75F);
    }

    @Override
    public Identifier getTexture(AntSoldierBotEntity entity) {
        return Identifier.of(InvasionFerrocerium.MOD_ID, "textures/entity/ant_soldier_bot/ant_soldier_bot.png");
    }
}