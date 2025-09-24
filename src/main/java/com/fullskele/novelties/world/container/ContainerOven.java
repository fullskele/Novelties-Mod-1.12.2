package com.fullskele.novelties.world.container;

import com.fullskele.novelties.blocks.tile.TileOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerOven extends Container {


    private final TileOven tile;


    public ContainerOven(InventoryPlayer playerInventory, TileOven tile) {
        this.tile = tile;


        int startX = 8;
        int startY = 88;
        //Main player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18 - 4));
            }
        }
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, startX + col * 18, startY + 58 - 4));
        }


        //Custom slots
        int craftStartX = startX + 2 * 18;
        int craftStartY = startY - 2 * 36;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(tile, col + row * 3, craftStartX + col * 18, craftStartY + row * 18));
            }
        }

        int fuelSlotX = startX + 9;
        int fuelSlotY = startY - 5 * 9;

        this.addSlotToContainer(new SlotFurnaceFuel(tile, 9, fuelSlotX, fuelSlotY));

        int outputSlotX = startX + 134;
        int outputSlotY = startY - 53;

        this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, tile,10, outputSlotX, outputSlotY) {
            @Override
            public ItemStack onTake(EntityPlayer player, ItemStack stack) {
                super.onTake(player, stack);

                float xp = tile.takeStoredXp();
                if (xp > 0) {
                    player.addExperience((int) xp);
                }

                return stack;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
    }


    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();

            //Output slot
            if (index == 46) {
                if (!this.mergeItemStack(stackInSlot, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stackInSlot, originalStack);
            }
            //Player inventory / hotbar
            else if (index >= 0 && index < 36) {
                if (!this.mergeItemStack(stackInSlot, 36, 45, false)) {
                    if (index >= 0 && index < 27) {
                        if (!this.mergeItemStack(stackInSlot, 27, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 27 && index < 36) {
                        if (!this.mergeItemStack(stackInSlot, 0, 27, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            else if (index >= 36 && index <= 45) {
                if (!this.mergeItemStack(stackInSlot, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return originalStack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            listener.sendWindowProperty(this, 0, tile.cookTime);
            listener.sendWindowProperty(this, 1, tile.cookTimeTotal);
            listener.sendWindowProperty(this, 2, tile.burnTime);
            listener.sendWindowProperty(this, 3, tile.currentItemBurnTime);
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0: tile.cookTime = data; break;
            case 1: tile.cookTimeTotal = data; break;
            case 2: tile.burnTime = data; break;
            case 3: tile.currentItemBurnTime = data; break;
        }
    }

}