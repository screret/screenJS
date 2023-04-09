package screret.screenjs.kubejs.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import screret.screenjs.client.AbstractContainerScreen;
import screret.screenjs.common.AbstractContainerMenu;

public interface ScreenConstructor {
    default void fromPacket(Component pTitle, MenuType<AbstractContainerMenu<?>> pType, Minecraft pMc, int pWindowId) {
        AbstractContainerScreen<?> u = this.create(pType.create(pWindowId, pMc.player.getInventory()), pMc.player.getInventory(), pTitle);
        pMc.player.containerMenu = u.getMenu();
        pMc.setScreen(u);
    }

    AbstractContainerScreen<? extends AbstractContainerMenu<?>> create(AbstractContainerMenu<?> pMenu, Inventory pInventory, Component pTitle);
}
