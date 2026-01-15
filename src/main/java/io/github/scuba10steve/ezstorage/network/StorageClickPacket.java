package io.github.scuba10steve.ezstorage.network;

import io.github.scuba10steve.ezstorage.EZStorage;
import io.github.scuba10steve.ezstorage.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.ezstorage.storage.EZInventory;
import io.github.scuba10steve.ezstorage.storage.StoredItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record StorageClickPacket(BlockPos pos, int slot, int button, boolean shift) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<StorageClickPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EZStorage.MODID, "storage_click"));
    
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
    
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(pos) instanceof StorageCoreBlockEntity core) {
                EZInventory inventory = core.getInventory();
                if (inventory != null) {
                    List<StoredItemStack> items = inventory.getStoredItems();
                    if (slot >= 0 && slot < items.size()) {
                        StoredItemStack stored = items.get(slot);
                        ItemStack heldStack = player.containerMenu.getCarried();
                        
                        // If player is holding nothing, extract from storage
                        if (heldStack.isEmpty()) {
                            int maxStack = stored.getItemStack().getMaxStackSize();
                            int amount;
                            
                            if (shift) {
                                // Shift-click: try to move full stack to inventory
                                amount = maxStack;
                                ItemStack extracted = inventory.extractItem(stored.getItemStack(), amount);
                                if (!extracted.isEmpty()) {
                                    if (!player.getInventory().add(extracted)) {
                                        // Couldn't add, put back
                                        inventory.insertItem(extracted);
                                    }
                                }
                            } else {
                                // Normal click: pick up stack
                                if (button == 0) {
                                    amount = maxStack; // Left click = full stack
                                } else {
                                    amount = maxStack / 2; // Right click = half stack
                                    if (amount == 0) amount = 1;
                                }
                                ItemStack extracted = inventory.extractItem(stored.getItemStack(), amount);
                                player.containerMenu.setCarried(extracted);
                            }
                        } else {
                            // Player is holding something, insert into storage
                            ItemStack remainder = inventory.insertItem(heldStack);
                            player.containerMenu.setCarried(remainder);
                        }
                        
                        // Mark changed and sync to clients
                        core.setChanged();
                        // Trigger sync by calling extractItem/insertItem methods which handle sync
                        // Or manually sync here
                        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(
                                serverLevel,
                                serverLevel.getChunkAt(pos).getPos(),
                                new StorageSyncPacket(pos, inventory.getStoredItems())
                            );
                        }
                    }
                }
            }
        });
    }
}
