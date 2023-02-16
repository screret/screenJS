package screret.screenjs.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import screret.screenjs.common.BlockContainerMenu;

public class BlockContainerScreen extends AbstractContainerScreen<BlockContainerMenu> {
    public BlockContainerScreen(BlockContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
}
