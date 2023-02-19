package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import screret.screenjs.common.AbstractContainerMenu;

import java.awt.*;
import java.util.Map;

public class AbstractContainerScreen<T extends AbstractContainerMenu<T>> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    private final int imageX, imageY;

    protected static Map<Rectangle, Rect2i> rectCache = new Object2ObjectOpenHashMap<>();

    public AbstractContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        Rectangle backroundPos = menu.builder.backroundPosition;
        this.imageWidth = (int) backroundPos.getWidth();
        this.imageHeight = (int) backroundPos.getHeight();
        this.imageX = (int) backroundPos.getX();
        this.imageY = (int) backroundPos.getY();

        for(var drawable : menu.builder.drawables) {
            rectCache.computeIfAbsent(drawable.texturePos(), rect -> new Rect2i((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight()));
        }
        for(var drawable : menu.builder.progressDrawables) {
            rectCache.computeIfAbsent(drawable.texturePos(), rect -> new Rect2i((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight()));
        }

        for (var button : menu.builder.buttons) {
            var position = button.position();
            this.addRenderableWidget(new Button((int) position.getX(), (int) position.getY(), (int) position.getWidth(), (int) position.getHeight(), button.tooltip(), pButton -> button.methodToRun().onPress(pButton)));
        }
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        float[] color = decodeARGBToRBGAZeroToOne(menu.builder.tintColor);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        RenderSystem.setShaderTexture(0, menu.builder.backroundTexture);
        this.blit(pPoseStack, this.leftPos, this.topPos, this.imageX, this.imageY, this.imageWidth, this.imageHeight);

        for(var drawable : menu.builder.drawables) {
            RenderSystem.setShaderTexture(0, drawable.texture());
            var position = drawable.renderPoint();
            var rect = rectCache.get(drawable.texturePos());
            this.blit(pPoseStack, position.x, position.y, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
    }

    private static float[] decodeARGBToRBGAZeroToOne(int color) {
        float[] array = new float[4];
        array[3] = ((color & 0xFF000000) >> 24) / 255f; //alpha
        array[0] = ((color & 0x00FF0000) >> 16) / 255f; //red
        array[1] = ((color & 0x0000FF00) >> 8 ) / 255f; //green
        array[2] = ((color & 0x000000FF)      ) / 255f; //blue
        return array;
    }
}
