package com.fullskele.eruptionspawner.blocks;

import com.fullskele.eruptionspawner.blocks.tile.TileEntityEruptionSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockEruptionSpawner extends Block implements ITileEntityProvider {

    public BlockEruptionSpawner() {
        super(Material.ROCK); // Example material
        setHardness(2.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setRegistryName("eruptor_block");
        setUnlocalizedName("eruptor_block");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEruptionSpawner();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityEruptionSpawner();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        // Ensure the block starts updating as soon as it is placed
        worldIn.scheduleUpdate(pos, this, 1);
    }


    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityEruptionSpawner) {
            ((TileEntityEruptionSpawner) tileEntity).tick();
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityEruptionSpawner) {
            ((TileEntityEruptionSpawner) tileEntity).tick();
        }
        worldIn.scheduleUpdate(pos, this, 1);
    }
}
