package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import screret.screenjs.block.BlockEntityContainerMenu;
import screret.screenjs.client.BlockEntityContainerScreen;

public class BlockEntityMenuType extends MenuType<BlockEntityContainerMenu> {
    private final ResourceLocation id;

    public BlockEntityMenuType(BlockEntityMenuType.Builder builder) {
        super((pContainerId, pPlayerInventory) -> new BlockEntityContainerMenu(builder, pContainerId, pPlayerInventory));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BlockEntityContainerMenu> {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public MenuScreens.ScreenConstructor<BlockEntityContainerMenu, BlockEntityContainerScreen> getScreenConstructor() {
            return BlockEntityContainerScreen::new;
        }

        @Override
        public BlockEntityMenuType createObject() {
            return new BlockEntityMenuType(this);
        }
    }
}
