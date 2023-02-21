package screret.screenjs.misc;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import screret.screenjs.common.AbstractContainerMenu;

import javax.annotation.Nonnull;

public class CraftingWrapper extends CraftingContainer {
    protected final IItemHandlerModifiable inv;
    public final AbstractContainerMenu<?> menu;

    public CraftingWrapper(AbstractContainerMenu<?> pMenu, int pWidth, int pHeight, IItemHandlerModifiable inv) {
        super(pMenu, pWidth, pHeight);
        this.inv = inv;
        this.menu = pMenu;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int slot) {
        return this.inv.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = inv.getStackInSlot(slot);
        stack = stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
        if (!stack.isEmpty()) {
            if (this.menu != null) {
                this.menu.slotsChanged(this);
            }
        }

        return stack;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.inv.setStackInSlot(pIndex, pStack);
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = getItem(index);
        if(stack.isEmpty()) return ItemStack.EMPTY;
        this.inv.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void clearContent() {
        for(int slot = 0; slot < this.inv.getSlots(); ++slot) {
            this.inv.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public void fillStackedContents(StackedContents pHelper) {
        for(int slot = 0; slot < this.inv.getSlots(); ++slot) {
            pHelper.accountSimpleStack(this.inv.getStackInSlot(slot));
        }
    }

}
