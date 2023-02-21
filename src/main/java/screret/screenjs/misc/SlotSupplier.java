package screret.screenjs.misc;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Supplier;

public class SlotSupplier {
    public int index, containerIndex, xPos, yPos;

    public SlotSupplier(int index, int containerIndex, int xPos, int yPos) {
        this.index = index;
        this.containerIndex = containerIndex;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public SlotItemHandler create(IItemHandler itemHandler) {
        return new SlotItemHandler(itemHandler, index, xPos, yPos);
    }
}
