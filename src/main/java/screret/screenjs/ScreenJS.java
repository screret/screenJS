package screret.screenjs;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import screret.screenjs.block.BlockContainerMenu;
import screret.screenjs.kubejs.BlockMenuType;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ScreenJS.MODID)
public class ScreenJS {
    public static final String MODID = "screenjs";

    public ScreenJS() {
        //IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
    }

    private void rightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        var menuTypes = ScreenJSPlugin.MENU_TYPE.objects.values().toArray(BuilderBase[]::new);
        Player entity = event.getEntity();
        if(!entity.level.isClientSide) {
            for (var type : menuTypes) {
                if(type instanceof BlockMenuType.Builder blockBuilder) {
                    if(entity.level.getBlockState(event.getPos()).getBlock() == blockBuilder.openingBlock) {
                        NetworkHooks.openScreen((ServerPlayer) entity,
                                new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                                        new BlockContainerMenu(blockBuilder, pContainerId, pPlayerInventory), blockBuilder.openingBlock.getName()));
                        event.setCanceled(true);
                    }
                }
            }
        }

    }
}
