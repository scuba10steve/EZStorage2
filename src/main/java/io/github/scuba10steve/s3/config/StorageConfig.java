package io.github.scuba10steve.s3.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class StorageConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    // Storage Capacities
    public static final ModConfigSpec.IntValue BASIC_CAPACITY;
    public static final ModConfigSpec.IntValue CONDENSED_CAPACITY;
    public static final ModConfigSpec.IntValue COMPRESSED_CAPACITY;
    public static final ModConfigSpec.IntValue SUPER_CAPACITY;
    public static final ModConfigSpec.IntValue ULTRA_CAPACITY;
    public static final ModConfigSpec.IntValue HYPER_CAPACITY;
    public static final ModConfigSpec.IntValue ULTIMATE_CAPACITY;
    
    // Feature Toggles
    public static final ModConfigSpec.BooleanValue ENABLE_SECURITY;
    public static final ModConfigSpec.BooleanValue ENABLE_TERMINAL;
    public static final ModConfigSpec.BooleanValue ENABLE_DOLLY;
    public static final ModConfigSpec.BooleanValue ENABLE_SEARCH_MODES;
    public static final ModConfigSpec.BooleanValue ENABLE_OP_OVERRIDE;
    public static final ModConfigSpec.BooleanValue SEARCH_AUTO_FOCUS;

    // Recipe Options
    public static final ModConfigSpec.BooleanValue CLASSIC_RECIPES;
    public static final ModConfigSpec.BooleanValue TOUGH_HYPER;
    
    // Integration
    public static final ModConfigSpec.BooleanValue JEI_INTEGRATION;

    // Crafting
    public static final ModConfigSpec.IntValue CRAFT_SHIFT_CLICK_LIMIT;

    // Automation
    public static final ModConfigSpec.IntValue EXTRACT_PORT_INTERVAL;
    public static final ModConfigSpec.IntValue MIN_SYNC_INTERVAL;

    static {
        BUILDER.comment("Storage Capacities").push("capacities");
        
        BASIC_CAPACITY = BUILDER
            .comment("Storage capacity for basic Storage Box")
            .defineInRange("basicCapacity", 10000, 1, Integer.MAX_VALUE);
        
        CONDENSED_CAPACITY = BUILDER
            .comment("Storage capacity for Condensed Storage Box")
            .defineInRange("condensedCapacity", 40000, 1, Integer.MAX_VALUE);
        
        COMPRESSED_CAPACITY = BUILDER
            .comment("Storage capacity for Compressed Storage Box")
            .defineInRange("compressedCapacity", 80000, 1, Integer.MAX_VALUE);
        
        SUPER_CAPACITY = BUILDER
            .comment("Storage capacity for Super Storage Box")
            .defineInRange("superCapacity", 160000, 1, Integer.MAX_VALUE);
        
        ULTRA_CAPACITY = BUILDER
            .comment("Storage capacity for Ultra Storage Box")
            .defineInRange("ultraCapacity", 640000, 1, Integer.MAX_VALUE);
        
        HYPER_CAPACITY = BUILDER
            .comment("Storage capacity for Hyper Storage Box")
            .defineInRange("hyperCapacity", 2560000, 1, Integer.MAX_VALUE);
        
        ULTIMATE_CAPACITY = BUILDER
            .comment("Storage capacity for Ultimate Storage Box")
            .defineInRange("ultimateCapacity", 10240000, 1, Integer.MAX_VALUE);
        
        BUILDER.pop();
        
        BUILDER.comment("Feature Toggles").push("features");
        
        ENABLE_SECURITY = BUILDER
            .comment("Enable Security Box and Key item")
            .define("enableSecurity", true);
        
        ENABLE_TERMINAL = BUILDER
            .comment("Enable Access Terminal block")
            .define("enableTerminal", true);
        
        ENABLE_DOLLY = BUILDER
            .comment("Enable Dolly items for moving blocks")
            .define("enableDolly", true);
        
        ENABLE_SEARCH_MODES = BUILDER
            .comment("Enable advanced search modes ($oredict, @mod, %tab)")
            .define("enableSearchModes", true);
        
        ENABLE_OP_OVERRIDE = BUILDER
            .comment("Allow operators to override security restrictions")
            .define("enableOpOverride", true);

        SEARCH_AUTO_FOCUS = BUILDER
            .comment("Automatically focus the search box when opening a storage GUI with a Search Box attached")
            .define("searchAutoFocus", false);

        BUILDER.pop();
        
        BUILDER.comment("Recipe Options").push("recipes");
        
        CLASSIC_RECIPES = BUILDER
            .comment("Use classic (easier) recipes instead of modern recipes")
            .define("classicRecipes", false);
        
        TOUGH_HYPER = BUILDER
            .comment("Make Hyper Storage Box recipe more expensive")
            .define("toughHyper", false);
        
        BUILDER.pop();
        
        BUILDER.comment("Mod Integration").push("integration");

        JEI_INTEGRATION = BUILDER
            .comment("Enable JEI integration features")
            .define("jeiIntegration", true);

        BUILDER.pop();

        BUILDER.comment("Crafting Settings").push("crafting");

        CRAFT_SHIFT_CLICK_LIMIT = BUILDER
            .comment("Maximum number of items crafted per shift-click operation")
            .defineInRange("craftShiftClickLimit", 64, 1, 1024);

        BUILDER.pop();

        BUILDER.comment("Automation Settings").push("automation");

        EXTRACT_PORT_INTERVAL = BUILDER
            .comment("Ticks between Extract Port extraction attempts (lower = faster, higher = less server load)")
            .defineInRange("extractPortInterval", 8, 1, 100);

        MIN_SYNC_INTERVAL = BUILDER
            .comment("Minimum ticks between storage inventory sync packets (helps prevent visual flicker)")
            .defineInRange("minSyncInterval", 2, 0, 20);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
