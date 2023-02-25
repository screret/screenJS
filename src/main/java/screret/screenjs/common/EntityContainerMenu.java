package screret.screenjs.common;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkDirection;
import screret.bejs.misc.IMultipleItemHandler;
import screret.screenjs.ScreenJS;
import screret.screenjs.kubejs.menu.EntityMenuType;
import screret.screenjs.misc.ChangedListenerStackHandler;
import screret.screenjs.misc.OutputSlotSupplier;
import screret.screenjs.packets.S2CSyncEntity;

public class EntityContainerMenu extends AbstractContainerMenu<EntityContainerMenu> {

    public final Entity entity;

    public final IItemHandler itemHandler;

    public EntityContainerMenu(EntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, Entity entity) {
        super(builder, pContainerId, pPlayerInventory);
        this.entity = entity;
        var cap = this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        this.itemHandler = cap;
        if(cap instanceof IMultipleItemHandler itemHandler) {
            if(builder.slotChanged != null) {
                for (int index : builder.inputSlotIndices) {
                    itemHandler.getAllContainers().set(index, new ChangedListenerStackHandler(collectItems(itemHandler.getContainer(index)), this::slotsChanged));
                }
            }
            this.addSlots();
        }
        this.addInventory(builder, pPlayerInventory);
    }

    @Override
    public void addSlots() {
        for (var slot : builder.slots) {
            if(this.itemHandler instanceof IMultipleItemHandler itemHandlers) {
                if(slot instanceof OutputSlotSupplier output) {
                    this.addSlot(output.create(itemHandlers));
                } else {
                    this.addSlot(slot.create(itemHandlers.getContainer(slot.containerIndex)));
                }
            } else {
                if(slot instanceof OutputSlotSupplier output) {
                    this.addSlot(output.create(itemHandler));
                } else {
                    this.addSlot(slot.create(itemHandler));
                }
            }
            this.containerSlotCount++;
        }
    }

    @Override
    public void slotsChanged(Container pContainer) {
        if(this.builder.slotChanged != null) {
            this.builder.slotChanged.changed(this, level, this.player, this.itemHandler);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer sp) {
            ScreenJS.CHANNEL.sendTo(new S2CSyncEntity(this.entity.getId(), this.entity.saveWithoutId(new CompoundTag())),
                    sp.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return entity != null && !entity.isRemoved() && player.distanceToSqr(entity.position()) < 8 * 8;
    }

    public static NonNullList<ItemStack> collectItems(IItemHandler itemHandler) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);

        for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            stacks.set(slot, itemHandler.getStackInSlot(slot));
        }
        return stacks;
    }
}
