package io.github.scuba10steve.s3.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Platform-agnostic network helper for sending packets.
 * Implemented by NeoForge using PacketDistributor.
 */
public interface S3NetworkHelper {

    void sendToPlayersTrackingChunk(ServerLevel level, BlockPos pos, CustomPacketPayload payload);

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    void sendToServer(CustomPacketPayload payload);
}
