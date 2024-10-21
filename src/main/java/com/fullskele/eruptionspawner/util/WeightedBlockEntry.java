package com.fullskele.eruptionspawner.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class WeightedBlockEntry {

    private final Block block;
    private final int meta;
    private final int weight;

    public WeightedBlockEntry(Block block, int meta, int weight) {
        this.block = block;
        this.meta = meta;
        this.weight = weight;
    }

    public IBlockState toBlockState() {
        return block.getStateFromMeta(meta);
    }

    public int getWeight() {
        return weight;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("blockId", Block.REGISTRY.getNameForObject(block).toString());
        nbt.setInteger("meta", meta);
        nbt.setInteger("weight", weight);
        return nbt;
    }

    public static WeightedBlockEntry fromNBT(NBTTagCompound nbt) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("blockId")));
        int meta = nbt.getInteger("meta");
        int weight = nbt.getInteger("weight");
        return new WeightedBlockEntry(block, meta, weight);
    }
}
