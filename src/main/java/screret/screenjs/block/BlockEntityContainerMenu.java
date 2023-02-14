package screret.screenjs.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import screret.bejs.kubejs.BlockEntityJS;
import screret.screenjs.basic.AbstractContainerMenu;
import screret.screenjs.kubejs.BlockEntityMenuType;

public class BlockEntityContainerMenu extends AbstractContainerMenu<BlockEntityContainerMenu> {
    public final BlockEntityJS blockEntity;
    public final boolean doesTick;

    public BlockEntityContainerMenu(BlockEntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory) {
        this(builder, pContainerId, pPlayerInventory, null);
    }

    public BlockEntityContainerMenu(BlockEntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, BlockEntityJS blockEntity) {
        super(builder, pContainerId, pPlayerInventory, blockEntity);
        this.blockEntity = blockEntity;
        this.doesTick = this.blockEntity.getBlockState().getBlock() instanceof EntityBlock;
    }

    @Override
    public void addSlots(Object[] params) {
        BlockEntity blockEntity = (BlockEntity) params[0];
        IItemHandler beItemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalStateException("BlockEntityJS didn't have an IItemHandler capability."));
        for(var slot : builder.slots) {
            this.addSlot(slot.create(beItemHandler));
            this.containerSlotCount++;
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return blockEntity != null && player.distanceToSqr(Vec3.atCenterOf(blockEntity.getBlockPos())) < 8 * 8;
    }
}
