package io.github.scuba10steve.s3;

import io.github.scuba10steve.s3.config.StorageConfig;
import io.github.scuba10steve.s3.init.ModBlockEntities;
import io.github.scuba10steve.s3.init.ModBlocks;
import io.github.scuba10steve.s3.init.ModCreativeTabs;
import io.github.scuba10steve.s3.init.ModItems;
import io.github.scuba10steve.s3.init.ModMenuTypes;
import io.github.scuba10steve.s3.platform.NeoForgeConfig;
import io.github.scuba10steve.s3.platform.NeoForgeNetworkHelper;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.ref.Log;
import io.github.scuba10steve.s3.ref.RefStrings;
import io.github.scuba10steve.s3.util.StorageUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(RefStrings.MODID)
public class StevesSimpleStorage {
	public static final String MODID = RefStrings.MODID;

	public static StevesSimpleStorage instance;

	@SuppressWarnings("unchecked")
	public StevesSimpleStorage(IEventBus modEventBus, ModContainer modContainer) {
		instance = this;

		// Initialize platform abstraction layer
		S3Platform.setConfig(new NeoForgeConfig());
		S3Platform.setNetworkHelper(new NeoForgeNetworkHelper());

		// Register deferred registers
		ModBlocks.register(modEventBus);
		ModItems.register(modEventBus);
		ModBlockEntities.register(modEventBus);
		ModMenuTypes.register(modEventBus);
		ModCreativeTabs.register(modEventBus);

		// Wire platform holders for block entity types, menu types, and items
		S3Platform.setBlockEntityTypes(
			() -> ModBlockEntities.STORAGE_CORE.get(),
			() -> ModBlockEntities.CRAFTING_BOX.get(),
			() -> ModBlockEntities.SEARCH_BOX.get(),
			() -> ModBlockEntities.SORT_BOX.get(),
			() -> ModBlockEntities.INPUT_PORT.get(),
			() -> ModBlockEntities.EXTRACT_PORT.get(),
			() -> ModBlockEntities.EJECT_PORT.get(),
			() -> ModBlockEntities.SECURITY_BOX.get(),
			() -> ModBlockEntities.STORAGE_INTERFACE.get()
		);

		S3Platform.setMenuTypes(
			() -> ModMenuTypes.STORAGE_CORE.get(),
			() -> ModMenuTypes.STORAGE_CORE_CRAFTING.get(),
			() -> ModMenuTypes.EXTRACT_PORT.get(),
			() -> ModMenuTypes.SECURITY_BOX.get()
		);

		S3Platform.setKeyItem(ModItems.KEY::get);
		S3Platform.setMenuOpener((player, provider, pos) -> player.openMenu(provider, pos));

		modEventBus.addListener(this::commonSetup);
		modContainer.registerConfig(ModConfig.Type.COMMON, StorageConfig.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		StorageUtils.getModNameFromID(RefStrings.MODID);
		Log.logger.info("Loading complete.");
	}
}
