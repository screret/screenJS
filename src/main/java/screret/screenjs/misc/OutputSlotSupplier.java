package screret.screenjs.misc;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import screret.bejs.misc.IMultipleItemHandler;

import javax.annotation.Nullable;

public class OutputSlotSupplier extends SlotSupplier {
    public RecipeType<? extends Recipe<Container>> recipeType;

    public final int outputIndex;

    public OutputSlotSupplier(int index, int containerIndex, int outputIndex, int xPos, int yPos, @Nullable RecipeType<? extends Recipe<Container>> recipeType) {
        super(index, containerIndex, xPos, yPos);
        this.recipeType = recipeType;
        this.outputIndex = outputIndex;
    }

    @Override
    public SlotItemHandler create(IItemHandler itemHandler) {
        return new OutputSlot((IMultipleItemHandler) itemHandler, itemHandler, index, xPos, yPos, recipeType);
    }

    public SlotItemHandler create(IMultipleItemHandler itemHandler) {
        return new OutputSlot((IItemHandlerModifiable) itemHandler.getContainer(containerIndex), itemHandler.getContainer(outputIndex), index, xPos, yPos, recipeType);
    }
}
