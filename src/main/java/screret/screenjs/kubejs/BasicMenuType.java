package screret.screenjs.kubejs;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import screret.screenjs.common.BasicContainerMenu;
import screret.screenjs.client.BasicContainerScreen;

public class BasicMenuType extends MenuType<BasicContainerMenu> {
    private final ResourceLocation id;

    public BasicMenuType(BasicMenuType.Builder builder) {
        super((pContainerId, pPlayerInventory) -> new BasicContainerMenu(builder, pContainerId, pPlayerInventory));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<BasicContainerMenu> {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public MenuScreens.ScreenConstructor<BasicContainerMenu, BasicContainerScreen> getScreenConstructor() {
            return BasicContainerScreen::new;
        }

        @Override
        public BasicMenuType createObject() {
            return new BasicMenuType(this);
        }
    }
}
