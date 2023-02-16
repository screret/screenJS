package screret.screenjs.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import screret.screenjs.kubejs.EntityMenuType;
import screret.screenjs.kubejs.MenuTypeBuilder;
import screret.screenjs.misc.AbstractContainerMenu;

public class EntityContainerMenu extends AbstractContainerMenu<EntityContainerMenu> {

    public final Entity entity;

    IItemHandler itemHandler;

    public EntityContainerMenu(EntityMenuType.Builder builder, int pContainerId, Inventory pPlayerInventory, Entity entity) {
        super(builder, pContainerId, pPlayerInventory, entity);
        this.entity = entity;
    }

    @Override
    public void addSlots(Object[] params) {
        Entity ent = (Entity) params[0];
        IItemHandler cap = ent.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalStateException("Entity didn't have an IItemHandler capability."));
        this.itemHandler = cap;
        for (var slot : builder.slots) {
            this.addSlot(slot.create(this.itemHandler));
            this.containerSlotCount++;
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return entity != null && !entity.isRemoved() && player.distanceToSqr(entity.position()) < 8 * 8;
    }
}
