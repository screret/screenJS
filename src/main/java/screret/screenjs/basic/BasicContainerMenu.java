package screret.screenjs.basic;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraftforge.items.ItemStackHandler;
import screret.screenjs.kubejs.BasicMenuType;

public class BasicContainerMenu extends AbstractContainerMenu<BasicContainerMenu> {

    private final ItemStackHandler slots;

    public BasicContainerMenu(BasicMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory) {
        super(builder, pContainerId, pPlayerInventory);
        slots = new ItemStackHandler(builder.slots.size());
    }

    @Override
    public void addSlots() {
        for(var slot : builder.slots) {
            this.addSlot(slot.create(slots));
            this.containerSlotCount++;
        }
    }
}