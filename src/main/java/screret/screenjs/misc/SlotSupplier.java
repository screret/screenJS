package screret.screenjs.misc;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Supplier;

public class SlotSupplier {
    public Supplier<SlotItemHandler> supplier;
    public int index, xPos, yPos;

    public SlotSupplier(int index, int xPos, int yPos) {
        this.index = index;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public SlotItemHandler create(IItemHandler itemHandler) {
        return new SlotItemHandler(itemHandler, index, xPos, yPos);
    }
}
