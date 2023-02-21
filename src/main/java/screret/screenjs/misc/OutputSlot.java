package screret.screenjs.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import screret.bejs.misc.IMultipleItemHandler;

import javax.annotation.Nullable;

public class OutputSlot extends SlotItemHandler {

    private final IItemHandlerModifiable inputItemHandler;

    private final RecipeType<? extends Recipe<Container>> recipeType;

    public OutputSlot(IItemHandlerModifiable inputItemHandler, IItemHandler output, int index, int xPosition, int yPosition, @Nullable RecipeType<? extends Recipe<Container>> recipeType) {
        super(output, index, xPosition, yPosition);
        this.inputItemHandler = inputItemHandler;
        this.recipeType = recipeType;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    public void onTake(Player pPlayer, ItemStack pStack) {
        if (this.recipeType != null) {
            this.checkTakeAchievements(pStack);
            net.minecraftforge.common.ForgeHooks.setCraftingPlayer(pPlayer);
            NonNullList<ItemStack> items = pPlayer.level.getRecipeManager().getRemainingItemsFor(recipeType, new CraftingWrapper(null, 3, 3, this.inputItemHandler), pPlayer.level);
            net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
            for(int i = 0; i < items.size(); ++i) {
                ItemStack handlerStack = this.inputItemHandler.getStackInSlot(i);
                ItemStack recipeStack = items.get(i);
                if (!handlerStack.isEmpty()) {
                    this.inputItemHandler.extractItem(i, 1, false);
                    handlerStack = this.inputItemHandler.getStackInSlot(i);
                }

                if (!recipeStack.isEmpty()) {
                    if (handlerStack.isEmpty()) {
                        this.inputItemHandler.setStackInSlot(i, recipeStack);
                    } else if (ItemStack.isSame(handlerStack, recipeStack) && ItemStack.tagMatches(handlerStack, recipeStack)) {
                        recipeStack.grow(handlerStack.getCount());
                        this.inputItemHandler.setStackInSlot(i, recipeStack);
                    } else if (!pPlayer.getInventory().add(recipeStack)) {
                        pPlayer.drop(recipeStack, false);
                    }
                }
            }
        }

    }
}
