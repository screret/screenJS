package screret.screenjs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.ModList;
import screret.bejs.kubejs.BlockEntityTypeBuilder;
import screret.screenjs.kubejs.BasicMenuType;
import screret.screenjs.kubejs.BlockEntityMenuType;
import screret.screenjs.kubejs.BlockMenuType;

public class ScreenJSPlugin extends KubeJSPlugin {
    public static final RegistryObjectBuilderTypes<MenuType<?>> MENU_TYPE = RegistryObjectBuilderTypes.add(Registry.MENU_REGISTRY, MenuType.class);

    @Override
    public void init() {
        MENU_TYPE.addType("basic", BasicMenuType.Builder.class, BasicMenuType.Builder::new);
        MENU_TYPE.addType("block", BlockMenuType.Builder.class, BlockMenuType.Builder::new);
        if(ModList.get().isLoaded("bejs")) MENU_TYPE.addType("block_entity", BlockEntityMenuType.Builder.class, BlockEntityMenuType.Builder::new);
    }
}