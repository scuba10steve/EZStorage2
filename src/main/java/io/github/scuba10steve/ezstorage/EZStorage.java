package io.github.scuba10steve.ezstorage;

import io.github.scuba10steve.ezstorage.config.EZConfig;
import io.github.scuba10steve.ezstorage.init.EZBlockEntities;
import io.github.scuba10steve.ezstorage.init.EZBlocks;
import io.github.scuba10steve.ezstorage.init.EZCreativeTabs;
import io.github.scuba10steve.ezstorage.init.EZItems;
import io.github.scuba10steve.ezstorage.init.EZMenuTypes;
import io.github.scuba10steve.ezstorage.ref.Log;
import io.github.scuba10steve.ezstorage.ref.RefStrings;
import io.github.scuba10steve.ezstorage.util.EZStorageUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

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
		EZCreativeTabs.register(modEventBus);
		
		modEventBus.addListener(this::commonSetup);
		modContainer.registerConfig(ModConfig.Type.COMMON, EZConfig.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		EZStorageUtils.getModNameFromID(RefStrings.MODID);
		Log.logger.info("Loading complete.");
	}
}
