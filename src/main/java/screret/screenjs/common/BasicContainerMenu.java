package screret.screenjs.common;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.ItemStackHandler;
import screret.screenjs.kubejs.BasicMenuType;

public class BasicContainerMenu extends AbstractContainerMenu<BasicContainerMenu> {

    private final ItemStackHandler slots;

    public BasicContainerMenu(BasicMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory) {
        super(builder, pContainerId, pPlayerInventory);
        slots = new ItemStackHandler(builder.slots.size());
    }

    @Override
    public void slotsChanged(Container pInventory) {
        if(this.builder.slotChanged != null) {
            this.builder.slotChanged.changed(this, level, this.player, this.slots);
        }
    }



    @Override
    public void addSlots(Object[] params) {
        for(var slot : builder.slots) {
            this.addSlot(slot.create(slots));
            this.containerSlotCount++;
        }
    }
}
