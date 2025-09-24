package com.fullskele.eruptionspawner.world.container.gui;

import com.fullskele.eruptionspawner.blocks.BlockOven;
import com.fullskele.eruptionspawner.blocks.tile.TileOven;
import com.fullskele.eruptionspawner.world.container.ContainerOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class OvenGuiHandler implements IGuiHandler {


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == BlockOven.GUI_ID && te instanceof TileOven) {
            return new ContainerOven(player.inventory, (TileOven) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (ID == BlockOven.GUI_ID && te instanceof TileOven) {
            TileOven tile = (TileOven) te;
            return new GuiOven(
                    new ContainerOven(player.inventory, tile),
                    player.inventory,
                    tile
            );
        }
        return null;
    }
}