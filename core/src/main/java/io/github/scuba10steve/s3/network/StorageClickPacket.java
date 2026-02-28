package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StorageClickPacket(BlockPos pos, int slot, int button, boolean shift) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<StorageClickPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "storage_click"));
    
    public static final StreamCodec<FriendlyByteBuf, StorageClickPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeBlockPos(packet.pos);
            buf.writeInt(packet.slot);
            buf.writeInt(packet.button);
            buf.writeBoolean(packet.shift);
        },
        buf -> new StorageClickPacket(
            buf.readBlockPos(),
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean()
        )
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
