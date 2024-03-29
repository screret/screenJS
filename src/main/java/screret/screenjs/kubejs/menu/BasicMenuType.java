package screret.screenjs.kubejs.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import screret.bejs.kubejs.BlockEntityTypeBuilder;
import screret.screenjs.common.BasicContainerMenu;
import screret.screenjs.client.BasicContainerScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BasicMenuType extends MenuType<BasicContainerMenu> {
    private final ResourceLocation id;

    public BasicMenuType(BasicMenuType.Builder builder) {
        super((pContainerId, pPlayerInventory) -> new BasicContainerMenu(builder, pContainerId, pPlayerInventory));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BasicContainerMenu> {
        public transient List<BlockEntityTypeBuilder.ItemHandler> itemHandlers;

        public Builder(ResourceLocation i) {
            super(i);
            itemHandlers = new ArrayList<>();
        }

        public Builder addItemHandler(int size) {
            itemHandlers.add(new BlockEntityTypeBuilder.ItemHandler(size, true));
            return this;
        }

        public Builder addItemHandler(int size, boolean canInput) {
            itemHandlers.add(new BlockEntityTypeBuilder.ItemHandler(size, canInput));
            return this;
        }

        @Override
        public ScreenConstructor getScreenConstructor() {
            return BasicContainerScreen::new;
        }



        @Override
        public BasicMenuType createObject() {
            return new BasicMenuType(this);
        }
    }
}
