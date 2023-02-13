package screret.screenjs.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import screret.screenjs.basic.AbstractContainerMenu;
import screret.screenjs.kubejs.BlockMenuType;

public class BlockContainerMenu extends AbstractContainerMenu<BlockContainerMenu> {
    private final ContainerLevelAccess access;
    private final Block block;

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
        ItemStackHandler slots = (ItemStackHandler) params[0];
        for(var slot : builder.slots) {
            this.addSlot(slot.create(slots));
            this.containerSlotCount++;
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, block);
    }
}
