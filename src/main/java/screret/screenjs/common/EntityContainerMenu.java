package screret.screenjs.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkDirection;
import screret.screenjs.ScreenJS;
import screret.screenjs.kubejs.EntityMenuType;
import screret.screenjs.packets.S2CSyncEntity;

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
        this.itemHandler = ent.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalStateException("Entity didn't have an IItemHandler capability."));
        for (var slot : builder.slots) {
            this.addSlot(slot.create(this.itemHandler));
            this.containerSlotCount++;
        }
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.builder.slotChanged.changed(this, level, this.player, this.itemHandler);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer sp) {
            ScreenJS.CHANNEL.sendTo(new S2CSyncEntity(this.entity.getId(), this.entity.saveWithoutId(new CompoundTag())),
                    sp.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return entity != null && !entity.isRemoved() && player.distanceToSqr(entity.position()) < 8 * 8;
    }
}
