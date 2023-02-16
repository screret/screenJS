package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.IContainerFactory;
import screret.bejs.kubejs.BlockEntityJS;
import screret.screenjs.common.BlockEntityContainerMenu;
import screret.screenjs.client.BlockEntityContainerScreen;

public class BlockEntityMenuType extends MenuType<BlockEntityContainerMenu> {
    private final ResourceLocation id;

    public BlockEntityMenuType(BlockEntityMenuType.Builder builder) {
        super((IContainerFactory<BlockEntityContainerMenu>)((pContainerId, inventory, extraData) -> {
            BlockPos pos = extraData.readBlockPos();
            Level world = inventory.player.getLevel();
            BlockEntityJS tile = (BlockEntityJS) world.getBlockEntity(pos);
            return new BlockEntityContainerMenu(builder, pContainerId, inventory, tile);
        }));
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
