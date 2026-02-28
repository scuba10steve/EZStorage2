package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from client to server to configure the Extract Port.
 */
public record ExtractPortConfigPacket(BlockPos pos, boolean cycleMode, boolean roundRobin) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExtractPortConfigPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "extract_port_config"));

    public static final StreamCodec<FriendlyByteBuf, ExtractPortConfigPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeBlockPos(packet.pos);
            buf.writeBoolean(packet.cycleMode);
            buf.writeBoolean(packet.roundRobin);
        },
        buf -> new ExtractPortConfigPacket(
            buf.readBlockPos(),
            buf.readBoolean(),
            buf.readBoolean()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
