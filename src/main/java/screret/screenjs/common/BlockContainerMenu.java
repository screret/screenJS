package screret.screenjs.common;

import dev.architectury.platform.Platform;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import screret.bejs.kubejs.BlockEntityTypeBuilder;
import screret.bejs.misc.IMultipleItemHandler;
import screret.bejs.misc.MultipleItemStackHandler;
import screret.screenjs.ScreenJS;
import screret.screenjs.kubejs.BlockMenuType;
import screret.screenjs.misc.ChangedListenerStackHandler;
import screret.screenjs.misc.OutputItemStackHandler;
import screret.screenjs.misc.OutputSlot;
import screret.screenjs.misc.OutputSlotSupplier;

import java.util.Optional;
import java.util.stream.Collectors;

public class BlockContainerMenu extends AbstractContainerMenu<BlockContainerMenu> {
    private final ContainerLevelAccess access;
    private final Block block;

    protected final IMultipleItemHandler itemHandlers;

    public BlockContainerMenu(BlockMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess access, Block block) {
        super(builder, pContainerId, pPlayerInventory);

        this.access = access;
        this.block = block;

        this.itemHandlers = new MultipleItemStackHandler(builder.itemHandlers);
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
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((level, pos) -> {
            itemHandlers.getAllContainers().forEach((modifiable) -> {
                this.clearContainer(pPlayer, new RecipeWrapper(modifiable));
            });
        });
    }

    @Override
    public void slotsChanged(Container pContainer) {
        this.access.execute((level, pos) -> {
            if(this.builder.slotChanged != null) {
                this.builder.slotChanged.changed(this, level, this.player, itemHandlers);
            }
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return !(pSlot instanceof OutputSlot) && super.canTakeItemForPickAll(pStack, pSlot);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, block);
    }

}
