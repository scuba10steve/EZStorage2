package com.zerofall.ezstorage.network;

import com.zerofall.ezstorage.EZStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EZStorage.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EZNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToClient(
            StorageSyncPacket.TYPE,
            StorageSyncPacket.STREAM_CODEC,
            StorageSyncPacket::handle
        );
    }
}
