package screret.screenjs.kubejs.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import screret.screenjs.ScreenJSPlugin;
import screret.screenjs.client.AbstractContainerScreen;
import screret.screenjs.common.AbstractContainerMenu;
import screret.screenjs.misc.OutputSlotSupplier;
import screret.screenjs.misc.SlotSupplier;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class MenuTypeBuilder<M extends AbstractContainerMenu<M>> extends BuilderBase<MenuType<M>> {
    private static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public transient List<SlotSupplier> slots;
    public transient int[] inputSlotIndices;
    public transient List<Text> texts;
    public transient List<Drawable> drawables;
    public transient List<ProgressDrawable> progressDrawables;
    public transient List<Button> buttons;
    public transient ResourceLocation backroundTexture;
    public transient Rectangle backroundPosition;
    public transient int tintColor;
    public transient int playerInvYStart;

    public transient SlotChangedCallback slotChanged;
    public transient QuickMoveFuncJS quickMoveFunction;
    public transient BiFunction<Player, Vec3, Boolean> validFunction;

    public transient boolean addInventorySlots;

    public MenuTypeBuilder(ResourceLocation i) {
        super(i);
        this.inputSlotIndices = new int[] {0};
        slots = new ArrayList<>();
        texts = new ArrayList<>();
        drawables = new ArrayList<>();
        progressDrawables = new ArrayList<>();
        buttons = new ArrayList<>();
        backroundTexture = DEFAULT_BACKGROUND;
        backroundPosition = new Rectangle(0, 0, 176, 166);
        tintColor = SimpleColor.WHITE.getArgbJS();
        this.playerInvYStart = -1;
        this.slotChanged = null;
        this.quickMoveFunction = MenuTypeBuilder::quickMoveStack;
        this.validFunction = ((player, pos) -> player.distanceToSqr(pos) < 8 * 8);
        this.addInventorySlots = true;
    }

    @Override
    public RegistryObjectBuilderTypes<? super MenuType<?>> getRegistryType() {
        return ScreenJSPlugin.MENU_TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract ScreenConstructor getScreenConstructor();

    public MenuTypeBuilder<M> addSlot(int x, int y) {
        slots.add(new SlotSupplier(slots.size(), 0, x, y));
        return this;
    }

    public MenuTypeBuilder<M> addSlot(int x, int y, int slotIndex, int containerIndex) {
        slots.add(new SlotSupplier(slotIndex, containerIndex, x, y));
        return this;
    }

    public MenuTypeBuilder<M> addOutputSlot(int x, int y, int slotIndex) {
        slots.add(new OutputSlotSupplier(slotIndex, 0, 0, x, y, null));
        return this;
    }

    public MenuTypeBuilder<M> addOutputSlot(int x, int y, int slotIndex, int inputIndex, int outputIndex, @Nullable RecipeType<? extends Recipe<Container>> recipeType) {
        slots.add(new OutputSlotSupplier(slotIndex, inputIndex, outputIndex, x, y, recipeType));
        return this;
    }

    public MenuTypeBuilder<M> inputSlotIndices(int... slots) {
        inputSlotIndices = slots;
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

    public MenuTypeBuilder<M> text(int xPos, int yPos, boolean drawCentered, boolean drawShadow, Color color, Function<AbstractContainerMenu<?>, Component> text) {
        texts.add(new Text(new Point(xPos, yPos), drawCentered, drawShadow, color, text));
        return this;
    }

    public MenuTypeBuilder<M> gradient(Rectangle position, int colorStart, int colorEnd) {
        drawables.add(new Drawable(new Point(position.x, position.y), position, null, colorStart, colorEnd));
        return this;
    }

    public MenuTypeBuilder<M> drawable(int xPos, int yPos, Rectangle texturePos, @Nullable ResourceLocation texureLoc, int tintColor) {
        drawables.add(new Drawable(new Point(xPos, yPos), texturePos, texureLoc, tintColor, tintColor));
        return this;
    }

    public MenuTypeBuilder<M> progressDrawable(int xPos, int yPos, Rectangle texturePos, ResourceLocation texureLoc, String direction, String type) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction.toUpperCase(Locale.ROOT)), ProgressDrawableType.valueOf(type.toUpperCase(Locale.ROOT)), -1, null));
        return this;
    }

    public MenuTypeBuilder<M> fluidDrawable(int xPos, int yPos, Rectangle texturePos, ResourceLocation texureLoc, String direction, int tankIndex) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction.toUpperCase(Locale.ROOT)), ProgressDrawableType.FLUID, tankIndex, null));
        return this;
    }

    public MenuTypeBuilder<M> customDrawable(int xPos, int yPos, Rectangle texturePos, ResourceLocation texureLoc, String direction, DrawMethodJS drawer) {
        progressDrawables.add(new ProgressDrawable(new Point(xPos, yPos), texturePos, texureLoc, MoveDirection.valueOf(direction.toUpperCase(Locale.ROOT)), ProgressDrawableType.CUSTOM, -1, drawer));
        return this;
    }

    public MenuTypeBuilder<M> button(Rectangle position, Component tooltip, OnPress methodToRun) {
        buttons.add(new Button(position, tooltip, methodToRun));
        return this;
    }

    public MenuTypeBuilder<M> backroundTexture(ResourceLocation textureLocation, Rectangle texturePosition) {
        this.backroundTexture = textureLocation;
        this.backroundPosition = texturePosition;
        return this;
    }

    public MenuTypeBuilder<M> quickMoveFunc(QuickMoveFuncJS func) {
        quickMoveFunction = func;
        return this;
    }

    public MenuTypeBuilder<M> slotChanged(SlotChangedCallback func) {
        slotChanged = func;
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientRegistry(Supplier<Minecraft> minecraft) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MenuScreens.<M, AbstractContainerScreen<M>>register(get(), (pMenu, pInventory, pTitle) -> (AbstractContainerScreen<M>) getScreenConstructor().create(pMenu, pInventory, pTitle)));
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
        void draw(PoseStack poseStack, int leftPos, int topPos, AbstractContainerMenu<?> menu, AbstractContainerScreen<?> screen, MenuTypeBuilder.ProgressDrawable drawable, MenuTypeBuilder.MoveDirection direction);
    }


    @FunctionalInterface
    public interface SlotChangedCallback {
        void changed(AbstractContainerMenu<?> menu, Level level, Player player, IItemHandler itemHandler);
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

    public enum HandlerType {
        INPUT,
        OUTPUT,
    }

    public record Text(Point position, boolean centered, boolean withShadow, Color color, Function<AbstractContainerMenu<?>, Component> text) {}
    public record Drawable(Point renderPoint, Rectangle texturePos, @Nullable ResourceLocation texture, int colorStart, int colorEnd) {}
    public record ProgressDrawable(Point renderPoint, Rectangle texturePos, ResourceLocation texture, MoveDirection direction, ProgressDrawableType type, int handlerIndex, @Nullable DrawMethodJS drawer) {}
    public record Button(Rectangle position, Component tooltip, OnPress methodToRun) {}

    @FunctionalInterface
    public interface OnPress {
        void onPress(net.minecraft.client.gui.components.Button pButton);
    }

    @FunctionalInterface
    public interface ScreenConstructor {
        default void fromPacket(Component pTitle, MenuType<AbstractContainerMenu<?>> pType, Minecraft pMc, int pWindowId) {
            AbstractContainerScreen<?> u = this.create(pType.create(pWindowId, pMc.player.getInventory()), pMc.player.getInventory(), pTitle);
            pMc.player.containerMenu = u.getMenu();
            pMc.setScreen(u);
        }

        AbstractContainerScreen<? extends AbstractContainerMenu<?>> create(AbstractContainerMenu<?> pMenu, Inventory pInventory, Component pTitle);
    }

}
