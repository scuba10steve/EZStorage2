package io.github.scuba10steve.s3.jei;

import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;

public class JEIIntegration {
    private static IJeiRuntime jeiRuntime;
    
    public static void setJeiRuntime(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }
    
    public static boolean isJeiAvailable() {
        return jeiRuntime != null;
    }
    
    public static void showRecipe(ItemStack itemStack) {
        if (jeiRuntime != null) {
            jeiRuntime.getRecipesGui().show(jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, mezz.jei.api.constants.VanillaTypes.ITEM_STACK, itemStack));
        }
    }
}
