package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record RecipeTransferPacket(List<ItemStack> items) implements CustomPacketPayload {

    public static final Type<RecipeTransferPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "recipe_transfer"));

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

}
