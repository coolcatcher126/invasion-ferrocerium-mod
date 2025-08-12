package io.github.coolcatcher126.ferrocerium.network;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InvasionStateChangedPayload(int invasionState) implements CustomPayload {
    public static final Identifier DIRT_BROKEN_ID = Identifier.of(InvasionFerrocerium.MOD_ID, "invasion_state_changed");
    public static final CustomPayload.Id<InvasionStateChangedPayload> ID = new CustomPayload.Id<>(DIRT_BROKEN_ID);
    public static final PacketCodec<PacketByteBuf, InvasionStateChangedPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, InvasionStateChangedPayload::invasionState,
            InvasionStateChangedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
