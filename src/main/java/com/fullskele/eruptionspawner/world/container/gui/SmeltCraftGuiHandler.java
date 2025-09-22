package com.fullskele.eruptionspawner.world.container.gui;

import com.fullskele.eruptionspawner.blocks.BlockSmeltCrafter;
import com.fullskele.eruptionspawner.blocks.tile.TileSmeltCrafter;
import com.fullskele.eruptionspawner.world.container.ContainerSmeltCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class SmeltCraftGuiHandler implements IGuiHandler {


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == BlockSmeltCrafter.GUI_ID && te instanceof TileSmeltCrafter) {
            return new ContainerSmeltCrafter(player.inventory, (TileSmeltCrafter) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == BlockSmeltCrafter.GUI_ID && te instanceof TileSmeltCrafter) {
            TileSmeltCrafter tile = (TileSmeltCrafter) te;
            return new GuiSmeltCrafter(
                    new ContainerSmeltCrafter(player.inventory, tile),
                    player.inventory,
                    tile
            );
        }
        return null;
    }
}