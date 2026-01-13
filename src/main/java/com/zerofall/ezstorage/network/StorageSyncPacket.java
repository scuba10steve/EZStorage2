package com.zerofall.ezstorage.network;

import com.zerofall.ezstorage.blockentity.StorageCoreBlockEntity;
import com.zerofall.ezstorage.storage.StoredItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record StorageSyncPacket(BlockPos pos, List<StoredItemStack> items) implements CustomPacketPayload {
    
    public static final Type<StorageSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("ezstorage", "storage_sync"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, StorageSyncPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, StorageSyncPacket::pos,
        StreamCodec.of(StorageSyncPacket::writeItems, StorageSyncPacket::readItems), StorageSyncPacket::items,
        StorageSyncPacket::new
    );

    private static void writeItems(RegistryFriendlyByteBuf buf, List<StoredItemStack> items) {
        buf.writeInt(items.size());
        for (StoredItemStack item : items) {
            ItemStack.STREAM_CODEC.encode(buf, item.getItemStack());
            buf.writeLong(item.getCount());
        }
    }

    private static List<StoredItemStack> readItems(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        List<StoredItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
            long count = buf.readLong();
            items.add(new StoredItemStack(stack, count));
        }
        return items;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StorageSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.pos());
                if (blockEntity instanceof StorageCoreBlockEntity storageCore) {
                    // Update client-side storage data
                    storageCore.getInventory().syncFromServer(packet.items());
                }
            }
        });
    }
}
