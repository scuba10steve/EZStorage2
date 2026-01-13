package com.zerofall.ezstorage;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.events.CoreEvents;
import com.zerofall.ezstorage.events.SecurityEvents;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZBlockEntities;
import com.zerofall.ezstorage.init.EZItems;
import com.zerofall.ezstorage.init.EZMenuTypes;
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
	public static final String MODID = RefStrings.MODID;

	public static EZStorage instance;

	public EZStorage(IEventBus modEventBus, ModContainer modContainer) {
		instance = this;
		
		// Register deferred registers
		EZBlocks.register(modEventBus);
		EZItems.register(modEventBus);
		EZBlockEntities.register(modEventBus);
		EZMenuTypes.register(modEventBus);
		
		modEventBus.addListener(this::commonSetup);
		modContainer.registerConfig(ModConfig.Type.COMMON, EZConfig.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		EZStorageUtils.getModNameFromID(RefStrings.MODID);
		Log.logger.info("Loading complete.");
	}
}
