package io.github.scuba10steve.ezstorage.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EZConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    // Storage Capacities
    public static final ModConfigSpec.IntValue BASIC_CAPACITY;
    public static final ModConfigSpec.IntValue CONDENSED_CAPACITY;
    public static final ModConfigSpec.IntValue COMPRESSED_CAPACITY;
    public static final ModConfigSpec.IntValue SUPER_CAPACITY;
    public static final ModConfigSpec.IntValue ULTRA_CAPACITY;
    public static final ModConfigSpec.IntValue HYPER_CAPACITY;
    
    // Feature Toggles
    public static final ModConfigSpec.BooleanValue ENABLE_SECURITY;
    public static final ModConfigSpec.BooleanValue ENABLE_TERMINAL;
    public static final ModConfigSpec.BooleanValue ENABLE_DOLLY;
    public static final ModConfigSpec.BooleanValue ENABLE_SEARCH_MODES;
    public static final ModConfigSpec.BooleanValue ENABLE_OP_OVERRIDE;
    
    // Recipe Options
    public static final ModConfigSpec.BooleanValue CLASSIC_RECIPES;
    public static final ModConfigSpec.BooleanValue TOUGH_HYPER;
    
    // Integration
    public static final ModConfigSpec.BooleanValue JEI_INTEGRATION;
    
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
    }
    
    public static final ModConfigSpec SPEC = BUILDER.build();
}
