package screret.screenjs.kubejs;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MenuTypeBuilder<M extends AbstractContainerMenu<M>> extends BuilderBase<MenuType<M>> {
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

    /*
    public MenuTypeBuilder<M> setItemHandler(IItemHandler handler) {
        this.itemHandler = () -> handler;
        return this;
    }

    public MenuTypeBuilder<M> setItemHandler(BlockEntity be) {
        if(be instanceof Container container) {
            this.itemHandler = () -> new InvWrapper(container);
        } else if(be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            this.itemHandler = () -> be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalStateException("ItemHandler capability was somehow null!"));
        } else {
            ScriptType.STARTUP.console.error("Given BlockEntity doesn't have an item handler!");
        }
        return this;
    }
    */

    public abstract <U extends Screen & MenuAccess<M>> MenuScreens.ScreenConstructor<M, U> getScreenConstructor();

    public MenuTypeBuilder<M> addSlot(int x, int y) {
        slots.add(new SlotSupplier(slots.size(), x, y));
        return this;
    }

    public MenuTypeBuilder<M> addOutputSlot(int x, int y) {
        slots.add(new OutputSlotSupplier(slots.size(), x, y));
        return this;
    }

    public MenuTypeBuilder<M> loop(Consumer<MenuTypeBuilder<M>> consumer) {
        consumer.accept(this);
        return this;
    }

    public MenuTypeBuilder<M> tintColor(Color color) {
        this.tintColor = color.getArgbJS();
        return this;
    }

    public MenuTypeBuilder<M> drawable(int xPos, int yPos, int x, int y, int u, int v, ResourceLocation texureLoc) {
        drawables.add(new Drawable(new Point(xPos, yPos), new Rect2i(x, y, u, v), texureLoc));
        return this;
    }

    public MenuTypeBuilder<M> progressDrawable(int xPos, int yPos, int x, int y, int u, int v, ResourceLocation texureLoc, String direction, String type) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), new Rect2i(x, y, u, v), texureLoc, MoveDirection.valueOf(direction), ProgressDrawableType.valueOf(type), -1));
        return this;
    }

    public MenuTypeBuilder<M> fluidDrawable(int xPos, int yPos, int x, int y, int u, int v, ResourceLocation texureLoc, String direction, int tankIndex) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), new Rect2i(x, y, u, v), texureLoc, MoveDirection.valueOf(direction), ProgressDrawableType.FLUID, tankIndex));
        return this;
    }

    public MenuTypeBuilder<M> button(Drawable unPressed, Component tooltip, net.minecraft.client.gui.components.Button.OnPress methodToRun) {
        buttons.add(new Button(unPressed, tooltip, methodToRun));
        return this;
    }

    public MenuTypeBuilder<M> backroundTexture(String textureLocation, int x, int y, int u, int v) {
        this.backroundTexture = new ResourceLocation(textureLocation);
        backroundPosition = new Rect2i(x, y, u, v);
        return this;
    }

    public MenuTypeBuilder<M> quickMoveFunc(QuickMoveFuncJS func) {
        quickMoveFunction = func;
        return this;
    }

    public MenuTypeBuilder<M> validityFunc(BiFunction<Player, Vec3, Boolean> func) {
        validFunction = func;
        return this;
    }

    public MenuTypeBuilder<M> disablePlayerInventory() {
        this.addInventorySlots = false;
        return this;
    }

    public MenuTypeBuilder<M> playerInventoryY(int yHeight) {
        playerInvYStart = yHeight;
        return this;
    }


    @Override
    public void clientRegistry(Supplier<Minecraft> minecraft) {
        MenuScreens.register(get(), getScreenConstructor());
    }

    public static ItemStack quickMoveStack(Player pPlayer, int pIndex, AbstractContainerMenu menu) {
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
        ItemStack quickMoveStack(Player player, int slotId, AbstractContainerMenu menu);
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
    }

    public record Drawable(Point renderPoint, Rect2i texturePos, ResourceLocation texture) {}
    public record ProgressDrawable(Point renderPoint, Rect2i texturePos, ResourceLocation texture, MoveDirection direction, ProgressDrawableType type, int handlerIndex) {}
    public record Button(Drawable drawable, Component tooltip, net.minecraft.client.gui.components.Button.OnPress methodToRun) {}
}
