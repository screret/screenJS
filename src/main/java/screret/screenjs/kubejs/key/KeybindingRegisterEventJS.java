package screret.screenjs.kubejs.key;

import dev.latvian.mods.kubejs.event.EventJS;

import java.util.HashMap;
import java.util.Map;

public class KeybindingRegisterEventJS extends EventJS {
    public static final Map<Integer, KeyBind> registeredBinds = new HashMap<>();
    public static final Map<KeyBind, KeyAction> bindsToActions = new HashMap<>();

    public static void register(KeyBind binding, KeyAction action) {
        registeredBinds.putIfAbsent(binding.keyCode(), binding);
        bindsToActions.putIfAbsent(binding, action);
    }

    public record KeyBind(String name, int keyCode, String category) {}

    @FunctionalInterface
    public interface KeyAction {
        void run(int action, int modifiers);
    }
}
