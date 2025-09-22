package com.fullskele.eruptionspawner.blocks.tile;

import com.fullskele.eruptionspawner.blocks.BlockSmeltCrafter;
import com.fullskele.eruptionspawner.recipe.SmeltCraftRecipe;
import com.fullskele.eruptionspawner.recipe.SmeltCraftRegistry;
import com.fullskele.eruptionspawner.util.ModUtils;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

//TODO: mod rebrand to 'Novelty Blocks'
public class TileSmeltCrafter extends TileEntity implements ITickable, ISidedInventory {
    public static final int SLOT_FUEL = 9;
    public static final int SLOT_OUTPUT = 10;
    private float storedXp = 0f;
    public int burnTime;
    public int currentItemBurnTime;
    public int cookTime;
    public int cookTimeTotal = 200;

    private SmeltCraftRecipe cachedRecipe = null;

    private ItemStack[] lastGrid = new ItemStack[9];

    public TileSmeltCrafter() {
        Arrays.fill(lastGrid, ItemStack.EMPTY);
    }


    public final ItemStackHandler items = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    private EnumFacing facing = EnumFacing.NORTH;

    public EnumFacing getFacing() {
        return facing != null ? facing : EnumFacing.NORTH;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
    }

    @Override
    public int getSizeInventory() {
        return items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < items.getSlots(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = items.getStackInSlot(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result;
        if (stack.getCount() <= count) {
            result = stack;
            items.setStackInSlot(index, ItemStack.EMPTY);
        } else {
            result = stack.splitStack(count);
            items.setStackInSlot(index, stack);
        }

        markDirty();
        return result;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = items.getStackInSlot(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        items.setStackInSlot(index, ItemStack.EMPTY);
        markDirty();
        return stack;
    }

    @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
        items.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void update() {
        boolean dirty = false;

        if (!world.isRemote) {
            ItemStack[] currentGrid = getGrid();

            // update cached recipe only if the grid changed
            if (!gridsEqual(lastGrid, currentGrid)) {
                cachedRecipe = SmeltCraftRegistry.match(currentGrid);
                cookTimeTotal = cachedRecipe != null ? cachedRecipe.getCookTime() : 0;
                lastGrid = copyGrid(currentGrid);

            }

            SmeltCraftRecipe match = cachedRecipe;
            boolean canWork = match != null && canOutput(match.getOutput());

            //Handle fuel
            if (!isBurning() && canWork) {
                ItemStack fuel = items.getStackInSlot(SLOT_FUEL);
                int burn = TileEntityFurnace.getItemBurnTime(fuel);
                if (burn > 0) {
                    burnTime = currentItemBurnTime = burn;
                    dirty = true;
                    fuel.shrink(1);
                    if (fuel.getCount() <= 0 && fuel.getItem().hasContainerItem(fuel)) {
                        items.setStackInSlot(SLOT_FUEL, fuel.getItem().getContainerItem(fuel));
                    }
                }
            }

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof BlockSmeltCrafter) {
                BlockSmeltCrafter.setBurningState(isBurning(), world, pos);
            }

            if (burnTime > 0) {
                burnTime--;
            } else {
                currentItemBurnTime = 0;
            }

            world.checkLightFor(EnumSkyBlock.BLOCK, pos);


            //Crafting process
            if (isBurning() && canWork) {
                cookTime++;
                if (cookTime >= cookTimeTotal) {
                    cookTime = 0;

                    craft(match);
                    storedXp += match.getExperience();
                    dirty = true;

                    ItemStack[] replacements = match.getTransformReplacements();

                    if (!match.isShapeless()) {
                        IIngredient[] inputs = match.getInputs();

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

                        int startRow = -1, startCol = -1;
                        boolean mirrored = false;
                        outer:
                        for (int r = 0; r <= 3 - recipeHeight; r++) {
                            for (int c = 0; c <= 3 - recipeWidth; c++) {
                                if (matchesAtPosition(inputs, currentGrid, r, c, minRow, minCol, recipeWidth, recipeHeight, false)) {
                                    startRow = r;
                                    startCol = c;
                                    mirrored = false;
                                    break outer;
                                }
                                if (matchesAtPosition(inputs, currentGrid, r, c, minRow, minCol, recipeWidth, recipeHeight, true)) {
                                    startRow = r;
                                    startCol = c;
                                    mirrored = true;
                                    break outer;
                                }
                            }
                        }

                        if (startRow >= 0 && startCol >= 0) {
                            for (int row = 0; row < recipeHeight; row++) {
                                for (int col = 0; col < recipeWidth; col++) {
                                    int recipeCol = mirrored ? (minCol + recipeWidth - 1 - col) : (minCol + col);
                                    int recipeRow = minRow + row;

                                    int index = recipeRow * 3 + recipeCol;
                                    if (!replacements[index].isEmpty()) {
                                        int slotIndex = (startRow + row) * 3 + (startCol + col);
                                        ItemStack current = items.getStackInSlot(slotIndex);

                                        ItemStack repl = replacements[index];
                                        if (current.isEmpty()) {
                                            items.setStackInSlot(slotIndex, repl.copy());
                                        } else if (ItemStack.areItemsEqual(current, repl) && ItemStack.areItemStackTagsEqual(current, repl)) {
                                            current.grow(repl.getCount());
                                        } else {
                                            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), repl.copy()));
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        for (int i = 0; i < 9; i++) {
                            ItemStack repl = replacements[i];
                            if (!repl.isEmpty()) {
                                ItemStack current = items.getStackInSlot(i);
                                if (current.isEmpty()) {
                                    items.setStackInSlot(i, repl.copy());
                                } else if (ItemStack.areItemsEqual(current, repl) && ItemStack.areItemStackTagsEqual(current, repl)) {
                                    current.grow(repl.getCount());
                                } else {
                                    world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), repl.copy()));
                                }
                            }
                        }
                    }
                }
            } else {
                cookTime = 0;
            }
        }

        if (dirty) markDirty();
    }

    private boolean gridsEqual(ItemStack[] a, ItemStack[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            ItemStack stackA = (a[i] == null) ? ItemStack.EMPTY : a[i];
            ItemStack stackB = (b[i] == null) ? ItemStack.EMPTY : b[i];
            if (!ItemStack.areItemStacksEqual(stackA, stackB)) return false;
        }
        return true;
    }

    private ItemStack[] copyGrid(ItemStack[] grid) {
        if (grid == null) return new ItemStack[9];
        ItemStack[] copy = new ItemStack[grid.length];
        for (int i = 0; i < grid.length; i++) {
            ItemStack s = grid[i];
            copy[i] = (s == null || s.isEmpty()) ? ItemStack.EMPTY : s.copy();
        }
        return copy;
    }

    public boolean isBurning() { return burnTime > 0; }

    private ItemStack[] getGrid() {
        ItemStack[] grid = new ItemStack[9];
        for (int i = 0; i < 9; i++) grid[i] = items.getStackInSlot(i).copy();
        return grid;
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) return false;
        ItemStack out = items.getStackInSlot(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        if (!ItemStack.areItemsEqual(out, result) || !ItemStack.areItemStackTagsEqual(out, result)) return false;
        int newSize = out.getCount() + result.getCount();
        return newSize <= out.getMaxStackSize();
    }

    private void craft(SmeltCraftRecipe recipe) {
        if (recipe == null) return;

        ItemStack[] grid = getGrid();

        if (recipe.isShapeless()) {
            List<IIngredient> required = new ArrayList<>();
            for (IIngredient ingredient : recipe.getInputs()) {
                if (ingredient != null && !ingredient.getItems().isEmpty()) {
                    required.add(ingredient);
                }
            }

            boolean[] usedSlots = new boolean[9];

            for (IIngredient req : required) {
                for (int i = 0; i < 9; i++) {
                    ItemStack have = items.getStackInSlot(i);
                    if (!usedSlots[i] && !have.isEmpty() && req.matches(CraftTweakerMC.getIItemStack(have))) {
                        have.shrink(1);
                        usedSlots[i] = true;
                        break;
                    }
                }
            }
        } else {
            IIngredient[] inputs = recipe.getInputs();

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

            int startRow = -1, startCol = -1;
            boolean mirrored = false;
            outer:
            for (int r = 0; r <= 3 - recipeHeight; r++) {
                for (int c = 0; c <= 3 - recipeWidth; c++) {
                    if (matchesAtPosition(inputs, grid, r, c, minRow, minCol, recipeWidth, recipeHeight, false)) {
                        startRow = r;
                        startCol = c;
                        mirrored = false;
                        break outer;
                    }
                    if (matchesAtPosition(inputs, grid, r, c, minRow, minCol, recipeWidth, recipeHeight, true)) {
                        startRow = r;
                        startCol = c;
                        mirrored = true;
                        break outer;
                    }
                }
            }

            if (startRow >= 0 && startCol >= 0) {
                for (int row = 0; row < recipeHeight; row++) {
                    for (int col = 0; col < recipeWidth; col++) {
                        int recipeCol = mirrored ? (minCol + recipeWidth - 1 - col) : (minCol + col);
                        int recipeRow = minRow + row;
                        IIngredient ing = inputs[recipeRow * 3 + recipeCol];
                        if (ing != null && !ing.getItems().isEmpty()) {
                            int slotIndex = (startRow + row) * 3 + (startCol + col);
                            ItemStack have = items.getStackInSlot(slotIndex);
                            if (!have.isEmpty()) {
                                have.shrink(1);
                            }
                        }
                    }
                }
            }
        }

        ItemStack out = items.getStackInSlot(SLOT_OUTPUT);
        ItemStack result = recipe.getOutput().copy();
        if (out.isEmpty()) {
            items.setStackInSlot(SLOT_OUTPUT, result);
        } else {
            out.grow(result.getCount());
        }
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

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, side));
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Facing", getFacing().getIndex());
        nbt.setInteger("BurnTime", burnTime);
        nbt.setInteger("CookTime", cookTime);
        nbt.setInteger("CookTimeTotal", cookTimeTotal);
        nbt.setInteger("CurrentItemBurnTime", currentItemBurnTime);
        nbt.setTag("Items", items.serializeNBT());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        facing = EnumFacing.getFront(nbt.getInteger("Facing"));
        burnTime = nbt.getInteger("BurnTime");
        cookTime = nbt.getInteger("CookTime");
        cookTimeTotal = nbt.getInteger("CookTimeTotal");
        currentItemBurnTime = nbt.getInteger("CurrentItemBurnTime");
        items.deserializeNBT(nbt.getCompoundTag("Items"));
    }


    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        if (index >= 0 && index <= 8) {
            //Inputs top only
            return direction == EnumFacing.UP;
        }
        if (index == SLOT_FUEL) {
            //Fuel only from sides
            return direction != EnumFacing.UP && TileEntityFurnace.isItemFuel(stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (index == SLOT_OUTPUT) {
            //output from bottom only
            return direction == EnumFacing.DOWN;
        }
        if (index == SLOT_FUEL) {
            return direction == EnumFacing.DOWN && stack.getItem() == Items.BUCKET;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            //Fuel + output
            return new int[]{SLOT_FUEL, SLOT_OUTPUT};
        } else if (side == EnumFacing.UP) {
            //Input slots
            return IntStream.range(0, 9).toArray();
        } else {
            //Fuel for sides
            return new int[]{SLOT_FUEL};
        }
    }

    public float takeStoredXp() {
        float oldXp = storedXp;
        storedXp = 0;
        return oldXp;
    }
}
