package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import screret.screenjs.misc.AbstractContainerMenu;

public class AbstractContainerScreen<T extends AbstractContainerMenu<T>> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    private final int imageX, imageY;

    public AbstractContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        Rect2i backroundPos = menu.builder.backroundPosition;
        this.imageWidth = backroundPos.getWidth();
        this.imageHeight = backroundPos.getHeight();
        this.imageX = backroundPos.getX();
        this.imageY = backroundPos.getY();
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
            var rect = drawable.texturePos();
            this.blit(pPoseStack, position.x, position.y, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
        for (var button : menu.builder.buttons) {
            var drawable = button.drawable();
            this.addRenderableWidget(new Button(drawable.renderPoint().x, drawable.renderPoint().y, drawable.texturePos().getWidth(), drawable.texturePos().getHeight(), button.tooltip(), button.methodToRun()));
        }
    }

    private static float[] decodeARGBToRBGAZeroToOne(int color) {
        float[] array = new float[4];
        array[0] = ((color & 0xFF0000) >> 16) / 255f;   //red
        array[1] = ((color & 0xFF00) >> 8) / 255f;      //green
        array[2] = ((color & 0xFF)) / 255f;             //blue
        array[3] = ((color & 0xFF000000) >> 24) / 255f; //alpha
        return array;
    }
}
