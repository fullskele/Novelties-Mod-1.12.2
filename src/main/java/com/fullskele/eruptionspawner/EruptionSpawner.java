package com.fullskele.eruptionspawner;

import com.blamejared.ctgui.client.GuiHandler;
import com.fullskele.eruptionspawner.blocks.BlockEruptionSpawner;
import com.fullskele.eruptionspawner.blocks.BlockSmeltCrafter;
import com.fullskele.eruptionspawner.blocks.tile.TileEntityEruptionSpawner;
import com.fullskele.eruptionspawner.blocks.tile.TileSmeltCrafter;
import com.fullskele.eruptionspawner.proxy.CommonProxy;
import com.fullskele.eruptionspawner.world.container.gui.SmeltCraftGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
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
@Mod(modid = EruptionSpawner.MODID, name = EruptionSpawner.NAME, version = EruptionSpawner.VERSION, dependencies = "required-after:crafttweaker@[4.1.20,);")
public class EruptionSpawner {
    public static final String MODID = "eruptionspawner";
    public static final String NAME = "Eruption Spawner";
    public static final String VERSION = "1.1.0";
    public static final Block ERUPTOR_BLOCK = new BlockEruptionSpawner();
    public static final Block SMELT_CRAFTER_BLOCK = new BlockSmeltCrafter();


    @Mod.Instance
    public static EruptionSpawner INSTANCE;


    @SidedProxy(clientSide = "com.fullskele.eruptionspawner.proxy.ClientProxy", serverSide = "com.fullskele.eruptionspawner.proxy.CommonProxy")
    public static CommonProxy proxy;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ERUPTOR_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemBlock(ERUPTOR_BLOCK).setRegistryName(ERUPTOR_BLOCK.getRegistryName()));
        event.getRegistry().register(SMELT_CRAFTER_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemBlock(SMELT_CRAFTER_BLOCK).setRegistryName(SMELT_CRAFTER_BLOCK.getRegistryName()));

    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(EruptionSpawner.INSTANCE, new SmeltCraftGuiHandler());
        GameRegistry.registerTileEntity(TileEntityEruptionSpawner.class, MODID + ":" + "tile_entity_eruptor");
        GameRegistry.registerTileEntity(TileSmeltCrafter.class, MODID + ":" + "tile_smelt_crafter");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ERUPTOR_BLOCK), 0, new ModelResourceLocation(ERUPTOR_BLOCK.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SMELT_CRAFTER_BLOCK), 0, new ModelResourceLocation(SMELT_CRAFTER_BLOCK.getRegistryName(), "inventory"));
    }
}
