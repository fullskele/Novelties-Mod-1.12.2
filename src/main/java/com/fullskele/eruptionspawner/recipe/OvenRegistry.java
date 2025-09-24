package com.fullskele.eruptionspawner.recipe;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OvenRegistry {
    private static final List<OvenRecipe> RECIPES = new ArrayList<>();

    public static void add(OvenRecipe r) { RECIPES.add(r); }
    public static void clear() { RECIPES.clear(); }

    public static OvenRecipe match(ItemStack[] grid) {
        for (OvenRecipe r : RECIPES) {
            if (matches(r, grid)) return r;
        }
        return null;
    }

    private static boolean matches(OvenRecipe r, ItemStack[] grid) {
        IIngredient[] inputs = r.getInputs();

        //To fix the empty oredict bug
        for (IIngredient ing : inputs) {
            if (ing != null && ing.getItems().isEmpty()) {
                return false;
            }
        }

        if (r.isShapeless()) {
            List<IIngredient> required = new ArrayList<>();
            for (IIngredient ing : inputs) {
                if (ing != null && !ing.getItems().isEmpty()) required.add(ing);
            }

            List<ItemStack> available = new ArrayList<>();
            for (ItemStack stack : grid) if (!stack.isEmpty()) available.add(stack.copy());

            if (available.size() != required.size()) return false;

            for (IIngredient req : required) {
                boolean matched = false;
                for (Iterator<ItemStack> it = available.iterator(); it.hasNext();) {
                    ItemStack have = it.next();
                    if (req.matches(CraftTweakerMC.getIItemStack(have))) {
                        it.remove();
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }
            return true;
        }

        int minRow = 3, maxRow = -1, minCol = 3, maxCol = -1;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                IIngredient ing = inputs[row * 3 + col];
                if (ing != null && !ing.getItems().isEmpty()) {
                    minRow = Math.min(minRow, row);
                    maxRow = Math.max(maxRow, row);
                    minCol = Math.min(minCol, col);
                    maxCol = Math.max(maxCol, col);
                }
            }
        }

        int recipeHeight = maxRow - minRow + 1;
        int recipeWidth = maxCol - minCol + 1;

        for (int startRow = 0; startRow <= 3 - recipeHeight; startRow++) {
            for (int startCol = 0; startCol <= 3 - recipeWidth; startCol++) {
                if (matchesAtPosition(inputs, grid, startRow, startCol, minRow, minCol, recipeWidth, recipeHeight, false)) return true;
                if (matchesAtPosition(inputs, grid, startRow, startCol, minRow, minCol, recipeWidth, recipeHeight, true)) return true;
            }
        }

        return false;
    }

    private static boolean matchesAtPosition(IIngredient[] recipe, ItemStack[] grid,
                                             int startRow, int startCol,
                                             int minRow, int minCol,
                                             int recipeWidth, int recipeHeight,
                                             boolean mirrored) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int gridIndex = row * 3 + col;

                boolean insideRecipe = row >= startRow && row < startRow + recipeHeight
                        && col >= startCol && col < startCol + recipeWidth;

                if (insideRecipe) {
                    int recipeRow = minRow + (row - startRow);
                    int recipeCol = mirrored ? (minCol + recipeWidth - 1 - (col - startCol))
                            : (minCol + (col - startCol));
                    IIngredient ing = recipe[recipeRow * 3 + recipeCol];
                    ItemStack have = grid[gridIndex];

                    if (ing == null || ing.getItems().isEmpty()) {
                        if (!have.isEmpty()) return false;
                    } else {
                        if (have.isEmpty()) return false;
                        if (!ing.matches(CraftTweakerMC.getIItemStack(have))) return false;
                    }
                } else {
                    if (!grid[gridIndex].isEmpty()) return false;
                }
            }
        }
        return true;
    }

    public static List<OvenRecipe> getAllRecipes() {
        return new ArrayList<>(RECIPES);
    }
}