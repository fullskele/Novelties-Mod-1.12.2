package com.fullskele.novelties;

import com.fullskele.novelties.blocks.BlockEruptionSpawner;
import com.fullskele.novelties.blocks.BlockOven;
import com.fullskele.novelties.blocks.tile.TileEntityEruptionSpawner;
import com.fullskele.novelties.blocks.tile.TileOven;
import com.fullskele.novelties.proxy.CommonProxy;
import com.fullskele.novelties.world.container.gui.OvenGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid = Novelties.MODID, name = Novelties.NAME, version = Novelties.VERSION, dependencies = "required-after:crafttweaker@[4.1.20,);")
public class Novelties {
    public static final String MODID = "novelties";
    public static final String NAME = "Novelties";
    public static final String VERSION = "1.1.0";
    public static final Block ERUPTOR_BLOCK = new BlockEruptionSpawner();
    public static final Block OVEN_BLOCK = new BlockOven();


    @Mod.Instance
    public static Novelties INSTANCE;


    @SidedProxy(clientSide = "com.fullskele.novelties.proxy.ClientProxy", serverSide = "com.fullskele.novelties.proxy.CommonProxy")
    public static CommonProxy proxy;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ERUPTOR_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemBlock(ERUPTOR_BLOCK).setRegistryName(ERUPTOR_BLOCK.getRegistryName()));
        event.getRegistry().register(OVEN_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemBlock(OVEN_BLOCK).setRegistryName(OVEN_BLOCK.getRegistryName()));

    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Novelties.INSTANCE, new OvenGuiHandler());
        GameRegistry.registerTileEntity(TileEntityEruptionSpawner.class, MODID + ":" + "tile_entity_eruptor");
        GameRegistry.registerTileEntity(TileOven.class, MODID + ":" + "tile_oven");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ERUPTOR_BLOCK), 0, new ModelResourceLocation(ERUPTOR_BLOCK.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(OVEN_BLOCK), 0, new ModelResourceLocation(OVEN_BLOCK.getRegistryName(), "inventory"));
    }
}
