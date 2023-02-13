package screret.screenjs.basic;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import screret.screenjs.kubejs.BasicMenuType;
import screret.screenjs.kubejs.MenuTypeBuilder;

import javax.annotation.Nonnull;

public abstract class AbstractContainerMenu<T extends AbstractContainerMenu<T>> extends net.minecraft.world.inventory.AbstractContainerMenu {
    public final MenuTypeBuilder<T> builder;
    public int containerSlotCount = 0;

    public final Level level;
    public final Player player;

    public AbstractContainerMenu(MenuTypeBuilder<T> builder, int pContainerId, Inventory pPlayerInventory, Object... params) {
        super(ForgeRegistries.MENU_TYPES.getValue(builder.id), pContainerId);
        this.builder = builder;
        this.level = pPlayerInventory.player.level;
        this.player = pPlayerInventory.player;

        this.addSlots(params);

        if(builder.addInventorySlots) {
            for(int y = 0; y < 3; ++y) {
                for(int x = 0; x < 9; ++x) {
                    this.addSlot(new Slot(pPlayerInventory, x + y * 9 + 9, 8 + x * 18, y * 18 + builder.playerInvYStart));
                }
            }

            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(pPlayerInventory, x, 8 + x * 18, builder.playerInvYStart + 58));
            }
        }
    }

    public abstract void addSlots(Object[] params);

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return builder.quickMoveFunction.quickMoveStack(pPlayer, pIndex, this);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
