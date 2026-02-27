package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.gui.server.ExtractPortMenu;
import io.github.scuba10steve.s3.gui.server.SecurityBoxMenu;
import io.github.scuba10steve.s3.gui.server.StorageCoreMenu;
import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(BuiltInRegistries.MENU, RefStrings.MODID);

    public static final Supplier<MenuType<StorageCoreMenu>> STORAGE_CORE = 
        MENU_TYPES.register("storage_core", () -> 
            IMenuTypeExtension.create((windowId, inv, data) -> 
                new StorageCoreMenu(windowId, inv, data.readBlockPos())));
                
    public static final Supplier<MenuType<StorageCoreCraftingMenu>> STORAGE_CORE_CRAFTING =
        MENU_TYPES.register("storage_core_crafting", () ->
            IMenuTypeExtension.create((windowId, inv, data) ->
                new StorageCoreCraftingMenu(windowId, inv, data.readBlockPos())));

    public static final Supplier<MenuType<ExtractPortMenu>> EXTRACT_PORT =
        MENU_TYPES.register("extract_port", () ->
            IMenuTypeExtension.create(ExtractPortMenu::new));

    public static final Supplier<MenuType<SecurityBoxMenu>> SECURITY_BOX =
        MENU_TYPES.register("security_box", () ->
            IMenuTypeExtension.create(SecurityBoxMenu::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
