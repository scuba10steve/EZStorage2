package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.storage.StoredItemStack;
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

public record StorageSyncPacket(BlockPos pos, List<StoredItemStack> items, long maxCapacity, boolean hasSearchBox, boolean hasSortBox, int sortModeOrdinal) implements CustomPacketPayload {
    
    public static final Type<StorageSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("ezstorage", "storage_sync"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, StorageSyncPacket> STREAM_CODEC = StreamCodec.of(
        StorageSyncPacket::encode,
        StorageSyncPacket::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, StorageSyncPacket packet) {
        BlockPos.STREAM_CODEC.encode(buf, packet.pos());
        writeItems(buf, packet.items());
        buf.writeLong(packet.maxCapacity());
        buf.writeBoolean(packet.hasSearchBox());
        buf.writeBoolean(packet.hasSortBox());
        buf.writeInt(packet.sortModeOrdinal());
    }

    private static StorageSyncPacket decode(RegistryFriendlyByteBuf buf) {
        BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
        List<StoredItemStack> items = readItems(buf);
        long maxCapacity = buf.readLong();
        boolean hasSearchBox = buf.readBoolean();
        boolean hasSortBox = buf.readBoolean();
        int sortModeOrdinal = buf.readInt();
        return new StorageSyncPacket(pos, items, maxCapacity, hasSearchBox, hasSortBox, sortModeOrdinal);
    }

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
                Minecraft mc = Minecraft.getInstance();
                BlockEntity blockEntity = mc.level.getBlockEntity(packet.pos());
                if (blockEntity instanceof StorageCoreBlockEntity storageCore) {
                    // Update client-side storage data
                    storageCore.getInventory().syncFromServer(packet.items(), packet.maxCapacity(), packet.hasSearchBox(), packet.hasSortBox(), packet.sortModeOrdinal());

                    // Force screen refresh if open
                    if (mc.screen != null) {
                        mc.screen.init(mc, mc.screen.width, mc.screen.height);
                    }
                }
            }
        });
    }
}
