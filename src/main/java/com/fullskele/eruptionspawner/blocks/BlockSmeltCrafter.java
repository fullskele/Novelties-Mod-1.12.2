package com.fullskele.eruptionspawner.blocks;

import com.fullskele.eruptionspawner.EruptionSpawner;
import com.fullskele.eruptionspawner.blocks.tile.TileSmeltCrafter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSmeltCrafter extends Block {

    public static final PropertyDirection FACING;
    static {
        FACING = BlockHorizontal.FACING;
    }
    public static final int GUI_ID = 1;
    public static final PropertyBool BURNING = PropertyBool.create("burning");


    public BlockSmeltCrafter() {
        super(Material.ROCK);
        setHardness(3.5F);
        setUnlocalizedName("smelt_crafter");
        setRegistryName("smelt_crafter");

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(BURNING, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, BURNING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
        boolean burning = (meta & 4) != 0;
        return this.getDefaultState()
                .withProperty(FACING, facing)
                .withProperty(BURNING, burning);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(BURNING)) {
            meta |= 4;
        }
        return meta;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer) {
        return this.getDefaultState()
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
                .withProperty(BURNING, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }


    @Override
    public TileEntity createTileEntity(World world, IBlockState state) { return new TileSmeltCrafter(); }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(EruptionSpawner.INSTANCE, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileSmeltCrafter) {
            TileSmeltCrafter smelter = (TileSmeltCrafter) tile;
            for (int i = 0; i < smelter.items.getSlots(); i++) {
                ItemStack stack = smelter.items.getStackInSlot(i);
                if (!stack.isEmpty() && !worldIn.isRemote) {
                    EntityItem entityItem = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
                    worldIn.spawnEntity(entityItem);
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(BURNING) ? 13 : 0;
    }

    public static void setBurningState(boolean active, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        TileEntity tile = world.getTileEntity(pos);

        if (state.getBlock() instanceof BlockSmeltCrafter) {
            IBlockState newState = state.withProperty(BURNING, active);
            world.setBlockState(pos, newState, 3);

            if (tile != null) {
                tile.validate();
                world.setTileEntity(pos, tile);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(BURNING)) {

            EnumFacing facing = state.getValue(FACING);

            double x = pos.getX() + 0.5D;
            double y = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double z = pos.getZ() + 0.5D;
            double offset = 0.52D;
            double randOffset = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D) {
                world.playSound(x, pos.getY(), z,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                        SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            switch (facing) {
                case WEST:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                            x - offset, y, z + randOffset, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME,
                            x - offset, y, z + randOffset, 0.0D, 0.0D, 0.0D);
                    break;
                case EAST:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                            x + offset, y, z + randOffset, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME,
                            x + offset, y, z + randOffset, 0.0D, 0.0D, 0.0D);
                    break;
                case NORTH:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                            x + randOffset, y, z - offset, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME,
                            x + randOffset, y, z - offset, 0.0D, 0.0D, 0.0D);
                    break;
                case SOUTH:
                default:
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                            x + randOffset, y, z + offset, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME,
                            x + randOffset, y, z + offset, 0.0D, 0.0D, 0.0D);
                    break;
            }
        }
    }
}