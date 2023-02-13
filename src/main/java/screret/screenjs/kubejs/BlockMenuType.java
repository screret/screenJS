package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import screret.screenjs.block.BlockContainerMenu;
import screret.screenjs.client.BlockContainerScreen;

public class BlockMenuType extends MenuType<BlockContainerMenu> {
    private final ResourceLocation id;

    public BlockMenuType(BlockMenuType.Builder builder) {
        super((pContainerId, pPlayerInventory) -> new BlockContainerMenu(builder, pContainerId, pPlayerInventory));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BlockContainerMenu> {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public MenuScreens.ScreenConstructor<BlockContainerMenu, BlockContainerScreen> getScreenConstructor() {
            return BlockContainerScreen::new;
        }

        @Override
        public BlockMenuType createObject() {
            return new BlockMenuType(this);
        }
    }
}
