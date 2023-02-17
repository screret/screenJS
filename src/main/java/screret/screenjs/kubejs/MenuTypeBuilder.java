package screret.screenjs.kubejs;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import screret.screenjs.ScreenJSPlugin;
import screret.screenjs.misc.AbstractContainerMenu;
import screret.screenjs.misc.OutputSlotSupplier;
import screret.screenjs.misc.SlotSupplier;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class MenuTypeBuilder<M extends AbstractContainerMenu<M>, S extends AbstractContainerScreen<M>> extends BuilderBase<MenuType<M>> {
    private static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public transient List<SlotSupplier> slots;
    public transient List<Drawable> drawables;
    public transient List<ProgressDrawable> progressDrawables;
    public transient List<Button> buttons;
    public transient ResourceLocation backroundTexture;
    public transient Rect2i backroundPosition;
    public transient int tintColor;
    public transient int playerInvYStart;

    public transient QuickMoveFuncJS quickMoveFunction;
    public transient BiFunction<Player, Vec3, Boolean> validFunction;

    public transient boolean addInventorySlots;

    public MenuTypeBuilder(ResourceLocation i) {
        super(i);
        slots = new ArrayList<>();
        drawables = new ArrayList<>();
        progressDrawables = new ArrayList<>();
        buttons = new ArrayList<>();
        backroundTexture = DEFAULT_BACKGROUND;
        backroundPosition = new Rect2i(0, 0, 176, 166);
        tintColor = SimpleColor.WHITE.getArgbJS();
        this.playerInvYStart = -1;
        this.quickMoveFunction = MenuTypeBuilder::quickMoveStack;
        this.validFunction = ((player, pos) -> player.distanceToSqr(pos) < 8 * 8);
        this.addInventorySlots = true;
    }

    @Override
    public RegistryObjectBuilderTypes<? super MenuType<?>> getRegistryType() {
        return ScreenJSPlugin.MENU_TYPE;
    }

    public abstract MenuScreens.ScreenConstructor<M, S> getScreenConstructor();

    public MenuTypeBuilder<M, S> addSlot(int x, int y) {
        slots.add(new SlotSupplier(slots.size(), x, y));
        return this;
    }

    public MenuTypeBuilder<M, S> addOutputSlot(int x, int y) {
        slots.add(new OutputSlotSupplier(slots.size(), x, y));
        return this;
    }

    public MenuTypeBuilder<M, S> loop(Consumer<MenuTypeBuilder<M, S>> consumer) {
        consumer.accept(this);
        return this;
    }

    public MenuTypeBuilder<M, S> tintColor(Color color) {
        this.tintColor = color.getArgbJS();
        return this;
    }

    public MenuTypeBuilder<M, S> drawable(int xPos, int yPos, int x, int y, int u, int v, ResourceLocation texureLoc) {
        drawables.add(new Drawable(new Point(xPos, yPos), new Rect2i(x, y, u, v), texureLoc));
        return this;
    }

    public MenuTypeBuilder<M, S> progressDrawable(int xPos, int yPos, Rect2i texturePos, ResourceLocation texureLoc, String direction, String type) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction), ProgressDrawableType.valueOf(type), -1, null));
        return this;
    }

    public MenuTypeBuilder<M, S> fluidDrawable(int xPos, int yPos, Rect2i texturePos, ResourceLocation texureLoc, String direction, int tankIndex) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction), ProgressDrawableType.FLUID, tankIndex, null));
        return this;
    }

    public MenuTypeBuilder<M, S> customDrawable(int xPos, int yPos, Rect2i texturePos, ResourceLocation texureLoc, String direction, DrawMethodJS drawer) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction), ProgressDrawableType.CUSTOM, -1, drawer));
        return this;
    }

    public MenuTypeBuilder<M, S> button(Rect2i position, Component tooltip, net.minecraft.client.gui.components.Button.OnPress methodToRun) {
        buttons.add(new Button(position, tooltip, methodToRun));
        return this;
    }

    public MenuTypeBuilder<M, S> backroundTexture(String textureLocation, int x, int y, int u, int v) {
        this.backroundTexture = new ResourceLocation(textureLocation);
        backroundPosition = new Rect2i(x, y, u, v);
        return this;
    }

    public MenuTypeBuilder<M, S> quickMoveFunc(QuickMoveFuncJS func) {
        quickMoveFunction = func;
        return this;
    }

    public MenuTypeBuilder<M, S> validityFunc(BiFunction<Player, Vec3, Boolean> func) {
        validFunction = func;
        return this;
    }

    public MenuTypeBuilder<M, S> disablePlayerInventory() {
        this.addInventorySlots = false;
        return this;
    }

    public MenuTypeBuilder<M, S> playerInventoryY(int yHeight) {
        playerInvYStart = yHeight;
        return this;
    }


    @Override
    public void clientRegistry(Supplier<Minecraft> minecraft) {
        MenuScreens.register(get(), getScreenConstructor());
    }

    public static ItemStack quickMoveStack(Player pPlayer, int pIndex, AbstractContainerMenu<?> menu) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = menu.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < menu.containerSlotCount) {
                if (!menu.moveItemStackTo(itemstack1, menu.containerSlotCount, menu.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!menu.moveItemStackTo(itemstack1, 0, menu.containerSlotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @FunctionalInterface
    public interface QuickMoveFuncJS {
        ItemStack quickMoveStack(Player player, int slotId, AbstractContainerMenu<?> menu);
    }

    @FunctionalInterface
    public interface DrawMethodJS {
        void draw(AbstractContainerMenu<?> menu, screret.screenjs.client.AbstractContainerScreen<?> screen, ProgressDrawable drawable, MoveDirection direction);
    }

    public enum MoveDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public enum ProgressDrawableType {
        PROGRESS,
        FUEL,
        ENERGY,
        FLUID,
        CUSTOM,
    }

    public record Drawable(Point renderPoint, Rect2i texturePos, ResourceLocation texture) {}
    public record ProgressDrawable(Point renderPoint, Rect2i texturePos, ResourceLocation texture, MoveDirection direction, ProgressDrawableType type, int handlerIndex, @Nullable DrawMethodJS drawer) {}
    public record Button(Rect2i position, Component tooltip, net.minecraft.client.gui.components.Button.OnPress methodToRun) {}

}
