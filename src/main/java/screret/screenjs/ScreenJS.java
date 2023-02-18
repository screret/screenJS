package screret.screenjs;

import dev.latvian.mods.kubejs.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screret.screenjs.common.BlockContainerMenu;
import screret.screenjs.common.BlockEntityContainerMenu;
import screret.screenjs.common.EntityContainerMenu;
import screret.screenjs.kubejs.BlockEntityMenuType;
import screret.screenjs.kubejs.BlockMenuType;
import screret.screenjs.kubejs.EntityMenuType;
import screret.screenjs.packets.*;

import java.util.Optional;

@Mod(ScreenJS.MODID)
public class ScreenJS {
    public static final String MODID = "screenjs";

    public static final Logger LOGGER = LogManager.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public ScreenJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickEntity);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        int index = 0;
        CHANNEL.registerMessage(index++, C2SRequestCapabilities.class, C2SRequestCapabilities::encode, C2SRequestCapabilities::new, C2SRequestCapabilities::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(index++, S2CSyncBlockEntityCapability.class, S2CSyncBlockEntityCapability::encode, S2CSyncBlockEntityCapability::new, S2CSyncBlockEntityCapability::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, S2CSyncEntityCapability.class, S2CSyncEntityCapability::encode, S2CSyncEntityCapability::new, S2CSyncEntityCapability::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static BuilderBase[] builders = null;

    private static BuilderBase[] getOrCreateMenuTypeBuilders() {
        if(builders == null) {
            builders = ScreenJSPlugin.MENU_TYPE.objects.values().toArray(BuilderBase[]::new);
        }
        return builders;
    }

    private void rightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        var menuTypes = getOrCreateMenuTypeBuilders();
        Player entity = event.getEntity();
        if(!entity.level.isClientSide) {
            for (var type : menuTypes) {
                BlockEntity be = entity.level.getBlockEntity(event.getPos());
                Block block = entity.level.getBlockState(event.getPos()).getBlock();

                if(type instanceof BlockEntityMenuType.Builder beBuilder && be != null && be.getType() == beBuilder.openingBlockEntity) {
                    NetworkHooks.openScreen((ServerPlayer) entity,
                            new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                                    new BlockEntityContainerMenu(beBuilder, pContainerId, pPlayerInventory, be), be instanceof Nameable nameable ? nameable.getName() : be.getBlockState().getBlock().getName()),
                            be.getBlockPos());
                    event.setCanceled(true);
                } else if(type instanceof BlockMenuType.Builder blockBuilder && block == blockBuilder.openingBlock) {
                    NetworkHooks.openScreen((ServerPlayer) entity,
                            new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                                    new BlockContainerMenu(blockBuilder, pContainerId, pPlayerInventory, ContainerLevelAccess.create(entity.level, event.getPos()), block), blockBuilder.openingBlock.getName()),
                            event.getPos());
                    event.setCanceled(true);
                }
            }
        }

    }

    private void rightClickEntity(final PlayerInteractEvent.EntityInteract event) {
        var menuTypes = getOrCreateMenuTypeBuilders();
        Player player = event.getEntity();
        if(!player.level.isClientSide) {
            for (var type : menuTypes) {
                if(type instanceof EntityMenuType.Builder entityBuilder) {
                    Entity target = event.getTarget();
                    EntityType<?> entity = target.getType();
                    if(entity == entityBuilder.openingEntity) {
                        NetworkHooks.openScreen((ServerPlayer) player,
                                new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                                        new EntityContainerMenu(entityBuilder, pContainerId, pPlayerInventory, target), target.getName()), buf -> buf.writeVarInt(target.getId()));
                        event.setCancellationResult(InteractionResult.CONSUME);
                    }
                }
            }
        }

    }
}
