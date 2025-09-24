package com.fullskele.novelties.recipe;


import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

@ZenRegister
@ZenClass("mods.novelties.Oven")
public class OvenCT {


    @ZenMethod
    public static void clear() {
        CraftTweakerAPI.apply(new IAction() {
            @Override public void apply() { OvenRegistry.clear(); }
            @Override public String describe() { return "Cleared Oven recipes"; }
        });
    }

    @ZenMethod
    public static void addShaped(IItemStack output, IIngredient[] inputs, int cookTime, @Optional float xp, @Optional IItemStack[] replacements) {
        if (inputs.length != 9) throw new IllegalArgumentException("inputs must be length 9");

        final ItemStack outMC = CraftTweakerMC.getItemStack(output).copy();
        final float experience = xp >= 0 ? xp : 0f;

        final ItemStack[] replacementsMC = new ItemStack[9];
        Arrays.fill(replacementsMC, ItemStack.EMPTY);
        if (replacements != null) {
            for (int i = 0; i < Math.min(replacements.length, 9); i++) {
                if (replacements[i] != null)
                    replacementsMC[i] = CraftTweakerMC.getItemStack(replacements[i]).copy();
            }
        }

        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                OvenRecipe recipe = new OvenRecipe(inputs, outMC, cookTime <= 0 ? 200 : cookTime, false);
                recipe.setExperience(experience);
                recipe.setTransformReplacements(replacementsMC);
                OvenRegistry.add(recipe);
            }

            @Override
            public String describe() {
                return "Added Oven recipe for " + outMC.getDisplayName();
            }
        });
    }

    @ZenMethod
    public static void addShapeless(IItemStack output, IIngredient[] inputs, int cookTime, @Optional float xp, @Optional IItemStack[] replacements) {
        if (inputs.length == 0 || inputs.length > 9)
            throw new IllegalArgumentException("Shapeless Oven recipe must have between 1 and 9 ingredients");

        final int ct = cookTime <= 0 ? 200 : cookTime;
        final ItemStack outMC = CraftTweakerMC.getItemStack(output).copy();
        final float experience = xp >= 0 ? xp : 0f;

        IIngredient[] paddedInputs = new IIngredient[9];
        Arrays.fill(paddedInputs, null);
        System.arraycopy(inputs, 0, paddedInputs, 0, inputs.length);

        final ItemStack[] replacementsMC = new ItemStack[9];
        Arrays.fill(replacementsMC, ItemStack.EMPTY);
        if (replacements != null) {
            for (int i = 0; i < Math.min(replacements.length, 9); i++) {
                if (replacements[i] != null)
                    replacementsMC[i] = CraftTweakerMC.getItemStack(replacements[i]).copy();
            }
        }

        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                OvenRecipe recipe = new OvenRecipe(paddedInputs, outMC, ct, true);
                recipe.setExperience(experience);
                recipe.setTransformReplacements(replacementsMC);
                OvenRegistry.add(recipe);
            }

            @Override
            public String describe() {
                return "Added Shapeless Oven recipe for " + outMC.getDisplayName();
            }
        });
    }


    @ZenMethod
    public static void addShapedMirrored(IItemStack output, IIngredient[] inputs, int cookTime, @Optional float xp, @Optional IItemStack[] replacements) {
        if (inputs.length > 9)
            throw new IllegalArgumentException("Shaped Oven recipe cannot have more than 9 ingredients");

        IIngredient[] paddedInputs = new IIngredient[9];
        Arrays.fill(paddedInputs, null);
        System.arraycopy(inputs, 0, paddedInputs, 0, inputs.length);

        final int ct = cookTime <= 0 ? 200 : cookTime;
        final ItemStack outMC = CraftTweakerMC.getItemStack(output).copy();
        final float experience = xp >= 0 ? xp : 0f;

        final ItemStack[] replacementsMC = new ItemStack[9];
        Arrays.fill(replacementsMC, ItemStack.EMPTY);
        if (replacements != null) {
            for (int i = 0; i < Math.min(replacements.length, 9); i++) {
                if (replacements[i] != null)
                    replacementsMC[i] = CraftTweakerMC.getItemStack(replacements[i]).copy();
            }
        }

        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                OvenRecipe recipeNormal = new OvenRecipe(paddedInputs, outMC, ct, false);
                recipeNormal.setExperience(experience);
                recipeNormal.setTransformReplacements(replacementsMC);
                OvenRegistry.add(recipeNormal);

                IIngredient[] mirroredInputs = new IIngredient[9];
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        mirroredInputs[row * 3 + col] = paddedInputs[row * 3 + (2 - col)];
                    }
                }
                OvenRecipe recipeMirrored = new OvenRecipe(mirroredInputs, outMC, ct, false);
                recipeMirrored.setExperience(experience);
                recipeMirrored.setTransformReplacements(replacementsMC);
                OvenRegistry.add(recipeMirrored);
            }

            @Override
            public String describe() {
                return "Added shaped mirrored Oven recipe for " + outMC.getDisplayName();
            }
        });
    }
}