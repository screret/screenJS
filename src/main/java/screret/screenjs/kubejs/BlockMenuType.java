package screret.screenjs.kubejs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.IContainerFactory;
import screret.bejs.kubejs.BlockEntityTypeBuilder;
import screret.screenjs.common.BlockContainerMenu;
import screret.screenjs.client.BlockContainerScreen;

import java.util.ArrayList;
import java.util.List;

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
        public transient List<BlockEntityTypeBuilder.ItemHandler> itemHandlers;

        public Builder(ResourceLocation i) {
            super(i);
            openingBlock = KubeJSRegistries.blocks().get(i);
            itemHandlers = new ArrayList<>();
        }

        @Override
        public ScreenConstructor getScreenConstructor() {
            return BlockContainerScreen::new;
        }

        public Builder setBlock(Block block) {
            this.openingBlock = block;
            return this;
        }

        public Builder addItemHandler(int size) {
            itemHandlers.add(new BlockEntityTypeBuilder.ItemHandler(size));
            return this;
        }

        @Override
        public BlockMenuType createObject() {
            return new BlockMenuType(this);
        }
    }
}
