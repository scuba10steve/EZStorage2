package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.StevesSimpleStorage;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.storage.StorageInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record RecipeTransferPacket(List<ItemStack> items) implements CustomPacketPayload {

    public static final Type<RecipeTransferPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StevesSimpleStorage.MODID, "recipe_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTransferPacket> STREAM_CODEC = StreamCodec.of(
        RecipeTransferPacket::encode,
        RecipeTransferPacket::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, RecipeTransferPacket packet) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = i < packet.items.size() ? packet.items.get(i) : ItemStack.EMPTY;
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }
    }

    private static RecipeTransferPacket decode(RegistryFriendlyByteBuf buf) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            items.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }
        return new RecipeTransferPacket(items);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.containerMenu instanceof StorageCoreCraftingMenu menu)) {
                return;
            }

            menu.handleRecipeTransfer(items);

            // Sync storage changes to clients
            if (player.level() instanceof ServerLevel serverLevel) {
                if (serverLevel.getBlockEntity(menu.getPos()) instanceof StorageCoreBlockEntity core) {
                    StorageInventory inventory = core.getInventory();
                    if (inventory != null) {
                        core.setChanged();
                        PacketDistributor.sendToPlayersTrackingChunk(
                            serverLevel,
                            serverLevel.getChunkAt(menu.getPos()).getPos(),
                            new StorageSyncPacket(
                                menu.getPos(),
                                inventory.getStoredItems(),
                                inventory.getMaxItems(),
                                core.hasSearchBox(),
                                core.hasSortBox(),
                                core.getSortMode().ordinal()
                            )
                        );
                    }
                }
            }
        });
    }
}
