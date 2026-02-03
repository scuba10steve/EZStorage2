package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.EZStorage;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.util.SortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server to toggle the sort mode.
 */
public record SortModePacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SortModePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EZStorage.MODID, "sort_mode"));

    public static final StreamCodec<FriendlyByteBuf, SortModePacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> buf.writeBlockPos(packet.pos),
        buf -> new SortModePacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(pos) instanceof StorageCoreBlockEntity core) {
                // Rotate to next sort mode
                SortMode currentMode = core.getSortMode();
                SortMode newMode = currentMode.rotateMode();
                core.setSortMode(newMode);
                core.setChanged();

                // Sync to all clients tracking this chunk
                if (player.level() instanceof ServerLevel serverLevel) {
                    PacketDistributor.sendToPlayersTrackingChunk(
                        serverLevel,
                        serverLevel.getChunkAt(pos).getPos(),
                        new StorageSyncPacket(
                            pos,
                            core.getInventory().getStoredItems(),
                            core.getInventory().getMaxItems(),
                            core.hasSearchBox(),
                            core.hasSortBox(),
                            newMode.ordinal()
                        )
                    );
                }
            }
        });
    }
}
