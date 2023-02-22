package screret.screenjs.common;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import screret.bejs.misc.IMultipleItemHandler;
import screret.bejs.misc.MultipleItemStackHandler;
import screret.screenjs.kubejs.BasicMenuType;
import screret.screenjs.misc.ChangedListenerStackHandler;
import screret.screenjs.misc.OutputSlotSupplier;

public class BasicContainerMenu extends AbstractContainerMenu<BasicContainerMenu> {

    private final IMultipleItemHandler itemHandlers;

    public BasicContainerMenu(BasicMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory) {
        super(builder, pContainerId, pPlayerInventory);
        this.itemHandlers = new MultipleItemStackHandler(1, builder.slots.size());
        if(builder.slotChanged != null) {
            for (int index : builder.inputSlotIndices) {
                this.itemHandlers.getAllContainers().set(index, new ChangedListenerStackHandler(itemHandlers.getSlotLimit(index, 0), this::slotsChanged));
            }
        }
        this.addSlots();
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
}
