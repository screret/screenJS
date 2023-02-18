package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.IContainerFactory;
import screret.screenjs.common.BlockContainerMenu;
import screret.screenjs.client.BlockContainerScreen;
import screret.screenjs.common.EntityContainerMenu;

public class BlockMenuType extends MenuType<BlockContainerMenu> {
    private final ResourceLocation id;

    public BlockMenuType(BlockMenuType.Builder builder) {
        super((IContainerFactory<BlockContainerMenu>)((pContainerId, inventory, extraData) -> {
            BlockPos pos = extraData.readBlockPos();
            return new BlockContainerMenu(builder, pContainerId, inventory, ContainerLevelAccess.create(inventory.player.getLevel(), pos), builder.openingBlock);
        }));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BlockContainerMenu> {
        public transient Block openingBlock;

        public Builder(ResourceLocation i) {
            super(i);
            openingBlock = null;
        }

        @Override
        public ScreenConstructor getScreenConstructor() {
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
