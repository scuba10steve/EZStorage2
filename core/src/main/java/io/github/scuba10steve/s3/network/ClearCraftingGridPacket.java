package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from client to server to clear the crafting grid,
 * returning items to storage.
 */
public record ClearCraftingGridPacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClearCraftingGridPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "clear_crafting_grid"));

    public static final StreamCodec<FriendlyByteBuf, ClearCraftingGridPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> buf.writeBlockPos(packet.pos),
        buf -> new ClearCraftingGridPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
