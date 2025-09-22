package com.fullskele.eruptionspawner.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModUtils {

    //match via ore dictionary if A has oredicts
    public static boolean itemMatches(ItemStack a, ItemStack b) {
        if (ItemStack.areItemsEqual(a, b)) return true;
        int[] ores = OreDictionary.getOreIDs(a);
        if (ores.length == 0) return false;
        int[] oresB = OreDictionary.getOreIDs(b);
        for (int x : ores) for (int y : oresB) if (x == y) return true;
        return false;
    }
}
