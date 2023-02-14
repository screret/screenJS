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
            var values = this.menu.blockEntity.getPersistentData();
            for (var drawable : menu.builder.progressDrawables) {
                var point = drawable.renderPoint();
                var size = drawable.texturePos();

                switch (drawable.type()) {
                    case PROGRESS -> {
                        if(values.contains("isProcessing") && values.getBoolean("isProcessing")) {
                            int progress = values.getInt("progress");
                            int maxProgress = values.getInt("totalProgress");
                            RenderSystem.setShaderTexture(0, drawable.texture());
                            switch (drawable.direction()) {
                                case DOWN -> {
                                    int value = progress(maxProgress, progress, size.getHeight());
                                    this.blit(pPoseStack, leftPos + point.x, topPos + point.y - size.getHeight() + value, size.getX(), size.getY() - size.getHeight() + value, size.getWidth(), value - 1);
                                }
                                case UP -> {
                                    int value = progress(maxProgress, progress, size.getHeight());
                                    this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() - value, size.getWidth(), value - 1);

                                }
                                case LEFT -> {
                                    int value = progress(maxProgress, progress, size.getWidth());
                                    this.blit(pPoseStack, leftPos + point.x - size.getWidth() + value, topPos + point.y, size.getX() - size.getWidth() + value, size.getY(), value - 1, size.getHeight());
                                }
                                case RIGHT -> {
                                    int value = progress(maxProgress, progress, size.getWidth());
                                    this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() - value, size.getY(), value - 1, size.getHeight());
                                }

                            }
                        }
                    }

                    case FUEL -> {
                        int fuel = values.getInt("remainingFuel");
                        int maxFuel = values.getInt("fuelDuration");
                        RenderSystem.setShaderTexture(0, drawable.texture());
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxFuel, fuel, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y - size.getHeight() + value, size.getX(), size.getY() - size.getHeight() + value, size.getWidth(), value - 1);
                            }
                            case UP -> {
                                int value = progress(maxFuel, fuel, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() - value, size.getWidth(), value - 1);
                            }
                            case LEFT -> {
                                int value = progress(maxFuel, fuel, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x - size.getWidth() + value, topPos + point.y, size.getX() - size.getWidth() + value, size.getY(), value - 1, size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxFuel, fuel, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() - value, size.getY(), value - 1, size.getHeight());
                            }

                        }
                    }

                }
            }
        }

    }

    public int progress(int max, int current, int width) {
        return max * width / current;
    }

}
