package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import screret.screenjs.block.BlockEntityContainerMenu;
import screret.screenjs.kubejs.MenuTypeBuilder;

public class BlockEntityContainerScreen extends AbstractContainerScreen<BlockEntityContainerMenu> {

    public BlockEntityContainerScreen(BlockEntityContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        if(this.menu.doesTick) {
            var values = this.menu.blockEntity.getValues();
            if((Boolean) values.get("isProcessing")) {
                {
                    int progress = (Integer) values.get("progress");
                    for(var kvp : menu.builder.progressDrawables.entrySet()) {
                        var key = kvp.getKey();
                        var point = key.getFirst();
                        var size = key.getSecond();
                        var direction = kvp.getValue().getFirst();
                        var location = kvp.getValue().getSecond();
                        RenderSystem.setShaderTexture(0, location);
                        if(direction == MenuTypeBuilder.MoveDirection.DOWN) {
                            this.blit(pPoseStack, leftPos + point.x, topPos + point.y - size.getHeight() + progress, size.getX(), size.getY() - size.getHeight() + progress, size.getWidth(), progress - 1);
                        } else if(direction == MenuTypeBuilder.MoveDirection.UP) {
                            this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - progress, size.getX(), size.getY() - progress, size.getWidth(), progress - 1);
                        } else if(direction == MenuTypeBuilder.MoveDirection.LEFT) {
                            this.blit(pPoseStack, leftPos + point.x - size.getWidth() + progress, topPos + point.y, size.getX() - size.getWidth() + progress, size.getY(), progress - 1, size.getHeight());
                        } else if(direction == MenuTypeBuilder.MoveDirection.RIGHT) {
                            this.blit(pPoseStack, leftPos + point.x + size.getWidth() - progress, topPos + point.y, size.getX() - progress, size.getY(), progress - 1, size.getHeight());
                        }
                    }
                }

            }
        }
    }
}
