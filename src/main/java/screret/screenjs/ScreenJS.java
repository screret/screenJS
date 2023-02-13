package screret.screenjs;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import screret.screenjs.kubejs.BasicMenuType;
import screret.screenjs.kubejs.BlockEntityMenuType;
import screret.screenjs.kubejs.BlockMenuType;
import screret.screenjs.kubejs.MenuTypeBuilder;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ScreenJS.MODID)
public class ScreenJS {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "screenjs";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ScreenJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        //modEventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    /*
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var menu : ScreenJSPlugin.MENU_TYPE.objects.entrySet()) {
                if(menu.getValue() instanceof BasicMenuType.Builder builder) {
                    MenuScreens.register(builder.get(), builder.getScreenConstructor());
                } else if(menu.getValue() instanceof BlockMenuType.Builder builder) {
                    MenuScreens.register(builder.get(), builder.getScreenConstructor());
                } else if(menu.getValue() instanceof BlockEntityMenuType.Builder builder) {
                    MenuScreens.register(builder.get(), builder.getScreenConstructor());
                }
            }
        });
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }
}
