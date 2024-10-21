package com.fullskele.eruptionspawner;

import com.fullskele.eruptionspawner.blocks.BlockEruptionSpawner;
import com.fullskele.eruptionspawner.blocks.tile.TileEntityEruptionSpawner;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid = EruptionSpawner.MODID, name = EruptionSpawner.NAME, version = EruptionSpawner.VERSION)
public class EruptionSpawner {
    public static final String MODID = "eruptionspawner";
    public static final String NAME = "Eruption Spawner";
    public static final String VERSION = "1.0.0";

    public static final Block ERUPTOR_BLOCK = new BlockEruptionSpawner();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ERUPTOR_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemBlock(ERUPTOR_BLOCK).setRegistryName(ERUPTOR_BLOCK.getRegistryName()));

    }

    @Mod.EventHandler
    public void preinit(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityEruptionSpawner.class, MODID + ":" + "tile_entity_eruptor");
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ERUPTOR_BLOCK), 0, new ModelResourceLocation(ERUPTOR_BLOCK.getRegistryName(), "inventory"));
    }
}
