package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

/**
 * Packet sent from client to server to add or remove a player from the Security Box whitelist.
 */
public record SecurityPlayerPacket(BlockPos pos, UUID playerId, String playerName, boolean add) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SecurityPlayerPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "security_player"));

    public static final StreamCodec<FriendlyByteBuf, SecurityPlayerPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeBlockPos(packet.pos);
            buf.writeUUID(packet.playerId);
            buf.writeUtf(packet.playerName);
            buf.writeBoolean(packet.add);
        },
        buf -> new SecurityPlayerPacket(
            buf.readBlockPos(),
            buf.readUUID(),
            buf.readUtf(256),
            buf.readBoolean()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
