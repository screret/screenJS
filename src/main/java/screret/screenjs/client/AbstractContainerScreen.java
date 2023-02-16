package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

        for(var drawable : menu.builder.drawables.entrySet()) {
            RenderSystem.setShaderTexture(0, drawable.getValue());
            var position = drawable.getKey().getFirst();
            var rect = drawable.getKey().getSecond();
            this.blit(pPoseStack, position.x, position.y, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
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
