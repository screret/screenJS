package screret.screenjs;

import dev.latvian.mods.kubejs.BuilderBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screret.screenjs.common.BlockContainerMenu;
import screret.screenjs.common.EntityContainerMenu;
import screret.screenjs.kubejs.BlockMenuType;
import screret.screenjs.kubejs.EntityMenuType;

@Mod(ScreenJS.MODID)
public class ScreenJS {
    public static final String MODID = "screenjs";

    public static final Logger LOGGER = LogManager.getLogger();

    public ScreenJS() {
        //IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickEntity);
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
                if(type instanceof BlockMenuType.Builder blockBuilder) {
                    Block block = entity.level.getBlockState(event.getPos()).getBlock();
                    if(block == blockBuilder.openingBlock) {
                        NetworkHooks.openScreen((ServerPlayer) entity,
                                new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                                        new BlockContainerMenu(blockBuilder, pContainerId, pPlayerInventory, ContainerLevelAccess.create(entity.level, event.getPos()), block), blockBuilder.openingBlock.getName()));
                        event.setCanceled(true);
                    }
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
