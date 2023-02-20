package screret.screenjs.common;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import screret.screenjs.kubejs.BlockMenuType;
import screret.screenjs.misc.OutputSlot;

public class BlockContainerMenu extends AbstractContainerMenu<BlockContainerMenu> {
    private final ContainerLevelAccess access;
    private final Block block;

    private ItemStackHandler itemHandler;

    public BlockContainerMenu(BlockMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory) {
        this(builder, pContainerId, pPlayerInventory, ContainerLevelAccess.NULL, null);
    }

    public BlockContainerMenu(BlockMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess access, Block block) {
        super(builder, pContainerId, pPlayerInventory, new ItemStackHandler(builder.slots.size()));
        this.access = access;
        this.block = block;
    }

    @Override
    public void addSlots(Object[] params) {
        this.itemHandler = (ItemStackHandler) params[0];
        for(var slot : builder.slots) {
            this.addSlot(slot.create(this.itemHandler));
            this.containerSlotCount++;
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((level, pos) -> {
            this.clearContainer(pPlayer, new RecipeWrapper(itemHandler));
        });
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((level, pos) -> {
            this.builder.slotChanged.changed(this, level, this.player, this.itemHandler);
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
