package screret.screenjs.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import screret.screenjs.common.BasicContainerMenu;
import screret.screenjs.misc.AbstractContainerMenu;

public class BasicContainerScreen extends AbstractContainerScreen<BasicContainerMenu> {
    public BasicContainerScreen(BasicContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public BasicContainerScreen(AbstractContainerMenu<?> abstractContainerMenu, Inventory inventory, Component component) {
        super((BasicContainerMenu) abstractContainerMenu, inventory, component);
    }
}
