package com.fullskele.eruptionspawner.integration.jei;

import com.fullskele.eruptionspawner.EruptionSpawner;
import com.fullskele.eruptionspawner.recipe.SmeltCraftRecipe;
import com.fullskele.eruptionspawner.recipe.SmeltCraftRegistry;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SmeltCraftJEIPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new SmeltCraftRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(SmeltCraftRecipe.class, SmeltCraftRecipeWrapper::new, SmeltCraftRecipeCategory.UID);
        registry.addRecipes(SmeltCraftRegistry.getAllRecipes(), SmeltCraftRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(EruptionSpawner.SMELT_CRAFTER_BLOCK), SmeltCraftRecipeCategory.UID);
    }
}