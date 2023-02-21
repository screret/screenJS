package screret.screenjs.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ChangedListenerStackHandler extends ItemStackHandler {

    private final ChangedCallback callback;

    public ChangedListenerStackHandler(int size, ChangedCallback callback) {
        super(size);
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged(int slot) {
        callback.slotsChanged(null);
    }

    @FunctionalInterface
    public interface ChangedCallback {
        void slotsChanged(Container container);
    }
}
