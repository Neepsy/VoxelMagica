package com.neepsy.voxelmagica;

import com.neepsy.voxelmagica.blocks.ModBlocks;
import com.neepsy.voxelmagica.blocks.TestBlock;
import com.neepsy.voxelmagica.blocks.TestBlockContainer;
import com.neepsy.voxelmagica.blocks.TestBlockTile;
import com.neepsy.voxelmagica.effects.SmartcastEffect;
import com.neepsy.voxelmagica.entity.InfuseProjectileEntity;
import com.neepsy.voxelmagica.entity.JoltProjectileEntity;
import com.neepsy.voxelmagica.entity.ModEntities;
import com.neepsy.voxelmagica.items.GriefShardItem;
import com.neepsy.voxelmagica.items.IconItem;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.items.SoulGemItem;
import com.neepsy.voxelmagica.items.spells.SpellInfuseItem;
import com.neepsy.voxelmagica.items.spells.SpellJoltItem;
import com.neepsy.voxelmagica.proxy.ClientProxy;
import com.neepsy.voxelmagica.proxy.IProxy;
import com.neepsy.voxelmagica.proxy.ServerProxy;
import com.neepsy.voxelmagica.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("voxelmagica")
public class VoxelMagica
{
    public static IProxy proxy = DistExecutor.runForDist( () ->() -> new ClientProxy(), () -> () -> new ServerProxy());
    public static final String MODID = "voxelmagica";

    public static ItemGroup creativeTab = new ItemGroup("voxelmagica") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.ICONITEM);
        }
    };

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public VoxelMagica() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doModelBake);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voxelmagica-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("voxelmagica-common.toml"));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        ManaStorage manaStorage = new ManaStorage();
        CapabilityManager.INSTANCE.register(IMana.class, manaStorage, Mana::new);

        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        proxy.init();
        Constants.getInstance();
    }

    private void doModelBake(final ModelBakeEvent event){
        //Have Constants singleton setup as it is needed for baking models.
        Constants.getInstance();
        proxy.onModelBake(event);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        proxy.loadModels();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new blocks here
            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().register(new TestBlock());
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent){
            LOGGER.info("Registering items");
            Item.Properties common = new Item.Properties().group(creativeTab);
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.TESTBLOCK, common).setRegistryName("testblock"));
            itemRegistryEvent.getRegistry().register(new IconItem());
            itemRegistryEvent.getRegistry().register(new SoulGemItem());
            itemRegistryEvent.getRegistry().register(new SpellInfuseItem());
            itemRegistryEvent.getRegistry().register(new SpellJoltItem());
            itemRegistryEvent.getRegistry().register(new GriefShardItem());


        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> TERegistryEvent){
            TileEntityType<?> TestBlockType = TileEntityType.Builder.create(TestBlockTile::new, ModBlocks.TESTBLOCK).build(null);
            TestBlockType.setRegistryName("testblock");
            TERegistryEvent.getRegistry().register(TestBlockType);
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> ContRegistryEvent){
            ContRegistryEvent.getRegistry().register(IForgeContainerType.create((windowID, inv, data) ->
                {   BlockPos pos = data.readBlockPos();
                    return new TestBlockContainer(windowID, proxy.getClientWorld(), pos, inv);}).setRegistryName("testblock"));
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> EntityRegistryEvent){
            EntityRegistryEvent.getRegistry().register(EntityType.Builder.<InfuseProjectileEntity>create(EntityClassification.MISC)
                    .setShouldReceiveVelocityUpdates(false)
                    .size(.1f,.1f)
                    .setCustomClientFactory(((spawnEntity, world) -> new InfuseProjectileEntity(world)))
                    .build("infuseprojectile")
                    .setRegistryName("infuseprojectile"));
            EntityRegistryEvent.getRegistry().register(EntityType.Builder.<JoltProjectileEntity>create(EntityClassification.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .size(.1f,.1f)
                    .setCustomClientFactory(((spawnEntity, world) -> new JoltProjectileEntity(world)))
                    .setUpdateInterval(2)
                    .build("joltprojectile")
                    .setRegistryName("joltprojectile"));
        }

        @SubscribeEvent
        public static void onEffectRegistry(final RegistryEvent.Register<Effect> EffectRegistryEvent){
            EffectRegistryEvent.getRegistry().register(new SmartcastEffect().setRegistryName("smartcast"));
        }
    }
}
