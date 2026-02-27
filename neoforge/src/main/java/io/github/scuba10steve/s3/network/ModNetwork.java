package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RefStrings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
            StorageSyncPacket.TYPE,
            StorageSyncPacket.STREAM_CODEC,
            ClientPacketHandlers::handleStorageSync
        );

        registrar.playToServer(
            StorageClickPacket.TYPE,
            StorageClickPacket.STREAM_CODEC,
            PacketHandlers::handleStorageClick
        );

        registrar.playToServer(
            SortModePacket.TYPE,
            SortModePacket.STREAM_CODEC,
            PacketHandlers::handleSortMode
        );

        registrar.playToServer(
            ExtractPortConfigPacket.TYPE,
            ExtractPortConfigPacket.STREAM_CODEC,
            PacketHandlers::handleExtractPortConfig
        );

        registrar.playToServer(
            SecurityPlayerPacket.TYPE,
            SecurityPlayerPacket.STREAM_CODEC,
            PacketHandlers::handleSecurityPlayer
        );

        registrar.playToServer(
            RecipeTransferPacket.TYPE,
            RecipeTransferPacket.STREAM_CODEC,
            PacketHandlers::handleRecipeTransfer
        );

        registrar.playToClient(
            SecuritySyncPacket.TYPE,
            SecuritySyncPacket.STREAM_CODEC,
            ClientPacketHandlers::handleSecuritySync
        );
    }
}
