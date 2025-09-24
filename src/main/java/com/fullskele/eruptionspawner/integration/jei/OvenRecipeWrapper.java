package com.fullskele.eruptionspawner.integration.jei;

import com.fullskele.eruptionspawner.recipe.OvenRecipe;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OvenRecipeWrapper implements IRecipeWrapper {
    private final OvenRecipe recipe;

    public OvenRecipeWrapper(OvenRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(recipe.getOutput()));

        List<List<ItemStack>> inputLists = new ArrayList<>();
        for (IIngredient ing : recipe.getInputs()) {
            if (ing != null) {
                List<ItemStack> stacks = new ArrayList<>();
                for (IItemStack iis : ing.getItems()) {
                    stacks.add(CraftTweakerMC.getItemStack(iis));
                }
                inputLists.add(stacks);
            } else {
                inputLists.add(Collections.emptyList());
            }
        }
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
    }

    public OvenRecipe getRecipe() {
        return recipe;
    }
}