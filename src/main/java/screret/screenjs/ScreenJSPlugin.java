package screret.screenjs;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.ModList;
import screret.screenjs.kubejs.*;

import java.awt.*;

import static dev.latvian.mods.kubejs.KubeJSRegistries.genericRegistry;
import static net.minecraft.core.Registry.MENU_REGISTRY;

public class ScreenJSPlugin extends KubeJSPlugin {
    public static final RegistryObjectBuilderTypes<MenuType<?>> MENU_TYPE = RegistryObjectBuilderTypes.add(MENU_REGISTRY, MenuType.class);

    public static Registrar<MenuType<?>> menus() {
        return genericRegistry(MENU_REGISTRY);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Rect2i", Rect2i.class);
        event.add("MenuUtils", MenuUtils.class);
    }

    @Override
    public void init() {
        MENU_TYPE.addType("basic", BasicMenuType.Builder.class, BasicMenuType.Builder::new);
        MENU_TYPE.addType("block", BlockMenuType.Builder.class, BlockMenuType.Builder::new);
        MENU_TYPE.addType("entity", EntityMenuType.Builder.class, EntityMenuType.Builder::new);
        if(Platform.isModLoaded("bejs")) MENU_TYPE.addType("block_entity", BlockEntityMenuType.Builder.class, BlockEntityMenuType.Builder::new);
    }
}
