package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side packet handlers. Separated from {@link PacketHandlers} because
 * these import {@link Minecraft} which is stripped on dedicated servers.
 */
public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleStorageSync(StorageSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;
            if (level == null) return;

            BlockEntity blockEntity = level.getBlockEntity(packet.pos());
            if (blockEntity instanceof StorageCoreBlockEntity storageCore) {
                storageCore.getInventory().syncFromServer(
                    packet.items(), packet.maxCapacity(),
                    packet.hasSearchBox(), packet.hasSortBox(), packet.sortModeOrdinal());
            }
        });
    }

    public static void handleSecuritySync(SecuritySyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;
            if (level == null) return;

            if (level.getBlockEntity(packet.pos()) instanceof SecurityBoxBlockEntity securityBox) {
                securityBox.setAllowedPlayers(packet.players());
            }
        });
    }
}
