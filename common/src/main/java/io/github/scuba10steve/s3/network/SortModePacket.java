package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from client to server to toggle the sort mode.
 */
public record SortModePacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SortModePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "sort_mode"));

    public static final StreamCodec<FriendlyByteBuf, SortModePacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> buf.writeBlockPos(packet.pos),
        buf -> new SortModePacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
