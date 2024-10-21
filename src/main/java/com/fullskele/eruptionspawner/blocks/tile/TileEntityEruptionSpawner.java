package com.fullskele.eruptionspawner.blocks.tile;

import com.fullskele.eruptionspawner.util.WeightedBlockEntry;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TileEntityEruptionSpawner extends TileEntity {

    private int tickCounter = 0;
    private int eruptionCooldown = 0;

    //NBT-reliant values
    private List<WeightedBlockEntry> weightedBlockList = new ArrayList<>();
    private int spawnrateMin = 20;  //in ticks
    private int spawnrateMax = 100;
    private int blocksSpawned = 1;
    private float velocity = 1.0F;
    private float yOffset = 0.0F;
    //0 = straight up, 180 = full semicircle spread
    private float angle = 90.0F;
    
    public void tick() {
        if (!world.isRemote) {
            tickCounter++;
            if (tickCounter >= eruptionCooldown) {
                eruptBlocks();
                eruptionCooldown = spawnrateMin + world.rand.nextInt(Math.max((spawnrateMax - spawnrateMin), 1));
                tickCounter = 0;
            }
            world.scheduleUpdate(pos, this.getBlockType(), 1);
        }
    }

    private void eruptBlocks() {
        if (!world.isRemote) {
            for (int i = 0; i < blocksSpawned; i++) {
                BlockPos spawnPos = pos.up();
                WeightedBlockEntry blockEntry = getRandomWeightedBlock();

                double yaw = world.rand.nextDouble() * 2.0 * Math.PI;
                double pitchMaxRadians = Math.toRadians(90.0);
                double pitchMinRadians = Math.toRadians(90.0 - angle);
                double pitch = pitchMinRadians + (world.rand.nextDouble() * (pitchMaxRadians - pitchMinRadians));


                double speed = velocity * (0.7F + world.rand.nextFloat() * 0.3F);

                double motionX = speed * Math.cos(pitch) * Math.cos(yaw);
                double motionY = speed * Math.sin(pitch);
                double motionZ = speed * Math.cos(pitch) * Math.sin(yaw);

                EntityFallingBlock fallingBlock = new EntityFallingBlock(world, spawnPos.getX(), spawnPos.getY() + yOffset, spawnPos.getZ(), blockEntry.toBlockState());
                fallingBlock.fallTime = 1;
                fallingBlock.shouldDropItem = false;
                fallingBlock.setHurtEntities(true);
                fallingBlock.fallDistance = 3.0F;

                fallingBlock.motionX = motionX;
                fallingBlock.motionY = motionY;
                fallingBlock.motionZ = motionZ;

                world.spawnEntity(fallingBlock);
            }
        }
    }


    private WeightedBlockEntry getRandomWeightedBlock() {
        if (weightedBlockList.isEmpty()) {
            return new WeightedBlockEntry(Blocks.AIR, 0, 1);
        }

        int totalWeight = weightedBlockList.stream()
                .filter(entry -> entry.getWeight() > 0)
                .mapToInt(WeightedBlockEntry::getWeight)
                .sum();

        if (totalWeight <= 0) {
            throw new IllegalStateException("Total weight of all blocks must be positive.");
        }

        int randomWeight = world.rand.nextInt(totalWeight);
        for (WeightedBlockEntry entry : weightedBlockList) {
            randomWeight -= entry.getWeight();
            if (randomWeight < 0) return entry;
        }

        return weightedBlockList.get(0);
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList nbtList = new NBTTagList();
        for (WeightedBlockEntry entry : weightedBlockList) {
            nbtList.appendTag(entry.toNBT());
        }
        compound.setTag("weightedListBlocks", nbtList);
        compound.setInteger("spawnrateMin", spawnrateMin);
        compound.setInteger("spawnrateMax", spawnrateMax);
        compound.setInteger("blocksSpawned", blocksSpawned);
        compound.setFloat("velocity", velocity);
        compound.setFloat("yOffset", yOffset);
        compound.setFloat("angle", angle);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagList nbtList = compound.getTagList("weightedListBlocks", 10);
        weightedBlockList.clear();
        for (int i = 0; i < nbtList.tagCount(); i++) {
            NBTTagCompound blockEntryNBT = nbtList.getCompoundTagAt(i);
            weightedBlockList.add(WeightedBlockEntry.fromNBT(blockEntryNBT));
        }
        spawnrateMin = compound.getInteger("spawnrateMin");
        spawnrateMax = compound.getInteger("spawnrateMax");
        blocksSpawned = compound.getInteger("blocksSpawned");
        velocity = compound.getFloat("velocity");
        yOffset = compound.getFloat("yOffset");
        angle = compound.getFloat("angle");
    }
}

/*
/setblock ~ ~ ~ eruptionblock:eruptor_block 0 replace {spawnrateMin:30, spawnrateMax:80, blocksSpawned:20, velocity:1.0f, yOffset:1.0f, angle:45.0f, weightedListBlocks:[{blockId:"minecraft:stone",weight:3},{blockId:"minecraft:cobblestone",meta:0,weight:1},{blockId:"minecraft:sand",meta:0,weight:2}]}
*/
