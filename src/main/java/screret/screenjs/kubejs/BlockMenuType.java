package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import screret.screenjs.common.BlockContainerMenu;
import screret.screenjs.client.BlockContainerScreen;

public class BlockMenuType extends MenuType<BlockContainerMenu> {
    private final ResourceLocation id;

    public BlockMenuType(BlockMenuType.Builder builder) {
        super((pContainerId, pPlayerInventory) -> new BlockContainerMenu(builder, pContainerId, pPlayerInventory));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BlockContainerMenu> {
        public transient Block openingBlock;

        public Builder(ResourceLocation i) {
            super(i);
            openingBlock = null;
        }

        @Override
        public MenuScreens.ScreenConstructor<BlockContainerMenu, BlockContainerScreen> getScreenConstructor() {
            return BlockContainerScreen::new;
        }

        public Builder setBlock(Block block) {
            this.openingBlock = block;
            return this;
        }

        @Override
        public BlockMenuType createObject() {
            return new BlockMenuType(this);
        }
    }
}
