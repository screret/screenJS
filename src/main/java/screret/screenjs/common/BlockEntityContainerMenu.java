package screret.screenjs.common;

import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkDirection;
import screret.bejs.misc.BeJSCapabilities;
import screret.bejs.misc.IMultipleItemHandler;
import screret.screenjs.ScreenJS;
import screret.screenjs.kubejs.BlockEntityMenuType;
import screret.screenjs.misc.ChangedListenerStackHandler;
import screret.screenjs.misc.OutputItemStackHandler;
import screret.screenjs.misc.OutputSlotSupplier;
import screret.screenjs.packets.S2CSyncBlockEntity;

import java.util.Optional;

public class BlockEntityContainerMenu extends AbstractContainerMenu<BlockEntityContainerMenu> {
    public final BlockEntity blockEntity;

    public final IMultipleItemHandler itemHandlers;

    public BlockEntityContainerMenu(BlockEntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, BlockEntity blockEntity) {
        super(builder, pContainerId, pPlayerInventory);
        this.blockEntity = blockEntity;

        var cap = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(cap instanceof IMultipleItemHandler itemHandler) {
            this.itemHandlers = itemHandler;
            if(builder.slotChanged != null) {
                for (int index : builder.inputSlotIndices) {
                    this.itemHandlers.getAllContainers().set(index, new ChangedListenerStackHandler(itemHandlers.getSlotLimit(index, 0), this::slotsChanged));
                }
            }
            this.addSlots();
        } else {
            this.itemHandlers = null;
        }
        this.addInventory(builder, pPlayerInventory);
    }

    @Override
    public void addSlots() {
        for(var slot : builder.slots) {
            if(slot instanceof OutputSlotSupplier output) {
                this.addSlot(output.create(itemHandlers));
            } else {
                this.addSlot(slot.create(itemHandlers.getContainer(slot.containerIndex)));
            }
            this.containerSlotCount++;
        }
    }

    @Override
    public void slotsChanged(Container pContainer) {
        if(this.builder.slotChanged != null) {
            this.builder.slotChanged.changed(this, level, this.player, this.itemHandlers);
        }
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
