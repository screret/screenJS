package screret.screenjs;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import screret.screenjs.kubejs.key.KeybindingRegisterEventJS;

@Mod.EventBusSubscriber(modid = ScreenJS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ScreenJSClientEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerKeyBinds(final RegisterKeyMappingsEvent event) {
        for (KeybindingRegisterEventJS.KeyBind bind : KeybindingRegisterEventJS.registeredBinds.values()) {
            event.register(new KeyMapping(bind.name(), bind.keyCode(), bind.category()));
        }
    }

    @SubscribeEvent
    private static void keyPress(final InputEvent.Key event) {
        var key = KeybindingRegisterEventJS.registeredBinds.get(event.getKey());
        if(key != null) {
            KeybindingRegisterEventJS.bindsToActions.get(key).run(event.getAction(), event.getModifiers());
        }
    }
}
