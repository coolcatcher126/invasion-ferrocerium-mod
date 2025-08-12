package io.github.coolcatcher126.ferrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.client.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import io.github.coolcatcher126.ferrocerium.network.InvasionStateChangedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class InvasionFerroceriumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        EntityModelLayerRegistry.registerModelLayer(AntBotMissileModel.ANT_BOT_MISSILE, AntBotMissileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ANT_BOT_MISSILE, AntBotMissileRenderer::new);


        EntityModelLayerRegistry.registerModelLayer(AntScoutBotModel.ANT_SCOUT_BOT, AntScoutBotModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ANT_SCOUT_BOT, AntScoutBotRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(AntSoldierBotModel.ANT_SOLDIER_BOT, AntSoldierBotModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ANT_SOLDIER_BOT, AntSoldierBotRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(InvasionStateChangedPayload.ID, InvasionFerroceriumClient::handleInvasionStateChangedPayload);
    }

    private static void handleInvasionStateChangedPayload(InvasionStateChangedPayload payload, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.client().player;
        assert player != null;
        player.sendMessage(Text.literal("Invasion state is now level: " + payload.invasionState()), false);
    }
}
