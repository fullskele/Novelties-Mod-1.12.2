package com.fullskele.novelties.integration.jei;

import com.fullskele.novelties.Novelties;
import com.fullskele.novelties.recipe.OvenRecipe;
import com.fullskele.novelties.recipe.OvenRegistry;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class OvenJEIPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new OvenRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(OvenRecipe.class, OvenRecipeWrapper::new, OvenRecipeCategory.UID);
        registry.addRecipes(OvenRegistry.getAllRecipes(), OvenRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Novelties.OVEN_BLOCK), OvenRecipeCategory.UID);
    }
}