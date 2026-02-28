package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity.SecurePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Packet sent from server to client to sync the full whitelist when GUI opens or whitelist changes.
 */
public record SecuritySyncPacket(BlockPos pos, List<SecurePlayer> players) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SecuritySyncPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "security_sync"));

    public static final StreamCodec<FriendlyByteBuf, SecuritySyncPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeBlockPos(packet.pos);
            buf.writeInt(packet.players.size());
            for (SecurePlayer sp : packet.players) {
                buf.writeUUID(sp.id());
                buf.writeUtf(sp.name());
            }
        },
        buf -> {
            BlockPos pos = buf.readBlockPos();
            int count = buf.readInt();
            List<SecurePlayer> players = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                UUID id = buf.readUUID();
                String name = buf.readUtf(256);
                players.add(new SecurePlayer(id, name));
            }
            return new SecuritySyncPacket(pos, players);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
