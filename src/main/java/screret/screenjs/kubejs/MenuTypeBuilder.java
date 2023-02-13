package screret.screenjs.kubejs;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import screret.screenjs.ScreenJSPlugin;
import screret.screenjs.basic.AbstractContainerMenu;
import screret.screenjs.misc.OutputSlot;
import screret.screenjs.misc.OutputSlotSupplier;
import screret.screenjs.misc.SlotSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class MenuTypeBuilder<M extends AbstractContainerMenu<M>> extends BuilderBase<MenuType<M>> {
    private static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public transient Supplier<IItemHandler> itemHandler;
    public transient List<SlotSupplier> slots;
    public transient HashMap<Pair<Point, Rect2i>, ResourceLocation> drawables;
    public transient HashMap<Pair<Point, Rect2i>, Pair<MoveDirection, ResourceLocation>> progressDrawables;
    public transient ResourceLocation backroundTexture;
    public transient Rect2i backroundPosition;
    public transient int tintColor;
    public transient int playerInvYStart;

    public transient QuickMoveFuncJS quickMoveFunction;
    public transient BiFunction<Player, Vec3, Boolean> validFunction;

    public transient boolean addInventorySlots;

    public MenuTypeBuilder(ResourceLocation i) {
        super(i);
        this.itemHandler = null;
        slots = new ArrayList<>();
        drawables = new HashMap<>();
        backroundTexture = DEFAULT_BACKGROUND;
        backroundPosition = new Rect2i(0, 0, 176, 166);
        tintColor = SimpleColor.WHITE.getArgbJS();
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

    public MenuTypeBuilder<M> tintColor(Color color) {
        this.tintColor = color.getArgbJS();
        return this;
    }

    public MenuTypeBuilder<M> drawable(int xPos, int yPos, int x, int y, int u, int v, String texureLoc) {
        drawables.put(Pair.of(new Point(xPos, yPos), new Rect2i(x, y, u, v)), new ResourceLocation(texureLoc));
        return this;
    }

    public MenuTypeBuilder<M> progressDrawable(int xPos, int yPos, int x, int y, int u, int v, String texureLoc, String direction) {
        progressDrawables.put(Pair.of(new Point(xPos, yPos), new Rect2i(x, y, u, v)), Pair.of(MoveDirection.valueOf(direction), new ResourceLocation(texureLoc)));
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
}