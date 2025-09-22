package com.fullskele.eruptionspawner.recipe;

import crafttweaker.api.item.IIngredient;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class SmeltCraftRecipe {
    private final IIngredient[] inputs;
    private final ItemStack output;
    private final int cookTime;
    private final boolean shapeless;


    private float experience = 0f;
    private final ItemStack[] transformReplacements = new ItemStack[9];

    public SmeltCraftRecipe(IIngredient[] inputs, ItemStack output, int cookTime, boolean shapeless) {
        if (inputs.length != 9) throw new IllegalArgumentException("inputs must be length 9");
        this.inputs = inputs;
        this.output = output.copy();
        this.cookTime = Math.max(1, cookTime);
        this.shapeless = shapeless;
        Arrays.fill(this.transformReplacements, ItemStack.EMPTY);
    }

    public IIngredient[] getInputs() { return inputs; }
    public ItemStack getOutput() { return output.copy(); }
    public int getCookTime() { return cookTime; }
    public boolean isShapeless() { return shapeless; }

    public float getExperience() { return experience; }
    public void setExperience(float experience) { this.experience = experience; }

    public ItemStack[] getTransformReplacements() {
        ItemStack[] copy = new ItemStack[9];
        for (int i = 0; i < 9; i++) copy[i] = transformReplacements[i].copy();
        return copy;
    }

    public void setTransformReplacements(ItemStack[] replacements) {
        if (replacements.length != 9) throw new IllegalArgumentException("replacements must be length 9");
        for (int i = 0; i < 9; i++) this.transformReplacements[i] = replacements[i] == null ? ItemStack.EMPTY : replacements[i].copy();
    }
}