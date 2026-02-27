package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.StevesSimpleStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = StevesSimpleStorage.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToClient(
            StorageSyncPacket.TYPE,
            StorageSyncPacket.STREAM_CODEC,
            StorageSyncPacket::handle
        );
        
        registrar.playToServer(
            StorageClickPacket.TYPE,
            StorageClickPacket.STREAM_CODEC,
            StorageClickPacket::handle
        );

        registrar.playToServer(
            SortModePacket.TYPE,
            SortModePacket.STREAM_CODEC,
            SortModePacket::handle
        );

        registrar.playToServer(
            ExtractPortConfigPacket.TYPE,
            ExtractPortConfigPacket.STREAM_CODEC,
            ExtractPortConfigPacket::handle
        );

        registrar.playToServer(
            SecurityPlayerPacket.TYPE,
            SecurityPlayerPacket.STREAM_CODEC,
            SecurityPlayerPacket::handle
        );

        registrar.playToServer(
            RecipeTransferPacket.TYPE,
            RecipeTransferPacket.STREAM_CODEC,
            RecipeTransferPacket::handle
        );

        registrar.playToClient(
            SecuritySyncPacket.TYPE,
            SecuritySyncPacket.STREAM_CODEC,
            SecuritySyncPacket::handle
        );
    }
}
