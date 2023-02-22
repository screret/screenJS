package screret.screenjs.kubejs;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.IContainerFactory;
import screret.screenjs.common.BlockEntityContainerMenu;
import screret.screenjs.client.BlockEntityContainerScreen;

public class BlockEntityMenuType extends MenuType<BlockEntityContainerMenu> {
    private final ResourceLocation id;

    public BlockEntityMenuType(BlockEntityMenuType.Builder builder) {
        super((IContainerFactory<BlockEntityContainerMenu>)((pContainerId, inventory, extraData) -> {
            BlockPos pos = extraData.readBlockPos();
            Level world = inventory.player.getLevel();
            BlockEntity tile = world.getBlockEntity(pos);
            return new BlockEntityContainerMenu(builder, pContainerId, inventory, tile);
        }));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BlockEntityContainerMenu> {
        public transient BlockEntityType<?> openingBlockEntity;
        public Builder(ResourceLocation i) {
            super(i);
            this.openingBlockEntity = KubeJSRegistries.blockEntityTypes().get(i);
        }

        @Override
        public ScreenConstructor getScreenConstructor() {
            return BlockEntityContainerScreen::new;
        }

        public Builder setBlockEntity(ResourceLocation blockEntity) {
            this.openingBlockEntity = KubeJSRegistries.blockEntityTypes().get(blockEntity);
            return this;
        }

        @Override
        public BlockEntityMenuType createObject() {
            return new BlockEntityMenuType(this);
        }
    }
}
