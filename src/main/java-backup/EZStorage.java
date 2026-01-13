package com.zerofall.ezstorage;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.events.CoreEvents;
import com.zerofall.ezstorage.events.SecurityEvents;
import com.zerofall.ezstorage.network.EZNetwork;
import com.zerofall.ezstorage.ref.EZTab;
import com.zerofall.ezstorage.ref.Log;
import com.zerofall.ezstorage.ref.RefStrings;
import com.zerofall.ezstorage.util.EZStorageUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/** EZStorage main mod class */
@Mod(RefStrings.MODID)
public class EZStorage {

	public static EZStorage instance;
	public static EZTab creativeTab;

	public EZStorage(IEventBus modEventBus, ModContainer modContainer) {
		instance = this;
		
		modEventBus.addListener(this::commonSetup);
		modContainer.registerConfig(ModConfig.Type.COMMON, EZConfig.SPEC);
		
		this.creativeTab = new EZTab();
		
		NeoForge.EVENT_BUS.register(new CoreEvents());
		NeoForge.EVENT_BUS.register(new SecurityEvents());
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		EZNetwork.registerNetwork();
		EZStorageUtils.getModNameFromID(RefStrings.MODID);
		Log.logger.info("Loading complete.");
	}
}
