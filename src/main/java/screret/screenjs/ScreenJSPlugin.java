package screret.screenjs;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import screret.screenjs.kubejs.key.KeybindingRegisterEventJS;
import screret.screenjs.kubejs.menu.BasicMenuType;
import screret.screenjs.kubejs.menu.BlockEntityMenuType;
import screret.screenjs.kubejs.menu.BlockMenuType;
import screret.screenjs.kubejs.menu.EntityMenuType;
import screret.screenjs.misc.CraftingWrapper;

import java.awt.*;

import static dev.latvian.mods.kubejs.KubeJSRegistries.genericRegistry;
import static net.minecraft.core.Registry.MENU_REGISTRY;

public class ScreenJSPlugin extends KubeJSPlugin {
    public static final RegistryObjectBuilderTypes<MenuType<?>> MENU_TYPE = RegistryObjectBuilderTypes.add(MENU_REGISTRY, MenuType.class);
    public static Registrar<MenuType<?>> menus() {
        return genericRegistry(MENU_REGISTRY);
    }


    EventGroup GROUP = EventGroup.of("KeybindEvents");
    EventHandler REGISTER_KEY_BIND = GROUP.client("register", () -> KeybindingRegisterEventJS.class);


    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Rectangle", Rectangle.class);
        event.add("MenuUtils", MenuUtils.class);
        event.add("CraftingWrapper", CraftingWrapper.class);
        event.add("RecipeWrapper", RecipeWrapper.class);

        event.add("KeyBind", KeybindingRegisterEventJS.KeyBind.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            event.add("InputConstants", InputConstants.class);
            event.add("Minecraft", Minecraft.class);
        }
    }

    @Override
    public void init() {
        MENU_TYPE.addType("basic", BasicMenuType.Builder.class, BasicMenuType.Builder::new);
        MENU_TYPE.addType("block", BlockMenuType.Builder.class, BlockMenuType.Builder::new);
        MENU_TYPE.addType("entity", EntityMenuType.Builder.class, EntityMenuType.Builder::new);
        MENU_TYPE.addType("block_entity", BlockEntityMenuType.Builder.class, BlockEntityMenuType.Builder::new);

    }

    @Override
    public void registerEvents() {
        GROUP.register();
    }

    @Override
    public void clientInit() {
        REGISTER_KEY_BIND.post(new KeybindingRegisterEventJS());
    }
}
