package screret.screenjs.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkDirection;
import screret.screenjs.ScreenJS;
import screret.screenjs.kubejs.BlockEntityMenuType;
import screret.screenjs.packets.S2CSyncBlockEntity;

public class BlockEntityContainerMenu extends AbstractContainerMenu<BlockEntityContainerMenu> {
    public final BlockEntity blockEntity;
    public final boolean doesTick;

    public final Int2ObjectMap<CompoundTag> tag = new Int2ObjectOpenHashMap<>();
    private IItemHandler itemHandler;

    public BlockEntityContainerMenu(BlockEntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity) {
        super(builder, pContainerId, pPlayerInventory, blockEntity);
        this.blockEntity = blockEntity;
        this.doesTick = this.blockEntity.getBlockState().getBlock() instanceof EntityBlock entityBlock && entityBlock.getTicker(blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getType()) != null;
    }

    @Override
    public void addSlots(Object[] params) {
        BlockEntity blockEntity = (BlockEntity) params[0];
        this.itemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalStateException("BlockEntityJS didn't have an IItemHandler capability."));
        for(var slot : builder.slots) {
            this.addSlot(slot.create(this.itemHandler));
            this.containerSlotCount++;
        }
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.builder.slotChanged.changed(this, level, this.player, this.itemHandler);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer sp) {
            ScreenJS.CHANNEL.sendTo(new S2CSyncBlockEntity(this.blockEntity.getBlockPos(), this.blockEntity.saveWithoutMetadata()),
                    sp.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity != null && player.distanceToSqr(Vec3.atCenterOf(blockEntity.getBlockPos())) < 8 * 8;
    }
}
