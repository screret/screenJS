package screret.screenjs.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import screret.screenjs.basic.AbstractContainerMenu;
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
        if (!pPlayer.level.isClientSide) {
            for (var slot : this.slots) {
                ItemStack itemstack = slot.remove(slot.getMaxStackSize());
                if (!itemstack.isEmpty()) {
                    pPlayer.drop(itemstack, false);
                }
            }

        }
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
