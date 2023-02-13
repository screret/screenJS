package screret.screenjs.misc;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class OutputSlotSupplier extends SlotSupplier {
    public OutputSlotSupplier(int index, int xPos, int yPos) {
        super(index, xPos, yPos);
    }

    @Override
    public SlotItemHandler create(IItemHandler itemHandler) {
        return new OutputSlot(itemHandler, index, xPos, yPos);
    }
}
