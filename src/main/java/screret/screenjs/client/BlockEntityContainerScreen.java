package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkDirection;
import screret.bejs.BeJS;
import screret.screenjs.ScreenJS;
import screret.screenjs.common.BlockEntityContainerMenu;
import screret.screenjs.kubejs.MenuTypeBuilder;
import screret.screenjs.misc.AbstractContainerMenu;
import screret.screenjs.packets.C2SRequestCapabilities;

import static screret.screenjs.MenuUtils.progress;

public class BlockEntityContainerScreen extends AbstractContainerScreen<BlockEntityContainerMenu> {

    public BlockEntityContainerScreen(BlockEntityContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public BlockEntityContainerScreen(AbstractContainerMenu<?> pMenu, Inventory pPlayerInventory, Component pTitle) {
        super((BlockEntityContainerMenu) pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        ScreenJS.CHANNEL.sendToServer(new C2SRequestCapabilities());

        var values = this.menu.blockEntity.getPersistentData();
        for (var drawable : menu.builder.progressDrawables) {
            var point = drawable.renderPoint();
            var size = drawable.texturePos();

            switch (drawable.type()) {
                case CUSTOM -> {
                    RenderSystem.setShaderTexture(0, drawable.texture());
                    drawable.drawer().draw(menu, this, drawable, drawable.direction());
                }

                case PROGRESS -> {
                    if (values.contains("isProcessing") && values.getBoolean("isProcessing")) {
                        int progress = values.getInt("progress");
                        int maxProgress = values.getInt("totalProgress");
                        RenderSystem.setShaderTexture(0, drawable.texture());
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxProgress, progress, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y - size.getHeight() + value), (int) size.getX(), (int) (size.getY() - size.getHeight() + value), (int) size.getWidth(), value - 1);
                            }
                            case UP -> {
                                int value = progress(maxProgress, progress, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y + size.getHeight() - value), (int) size.getX(), (int) (size.getY() - value), (int) size.getWidth(), value - 1);

                            }
                            case LEFT -> {
                                int value = progress(maxProgress, progress, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x - size.getWidth() + value), topPos + point.y, (int) (size.getX() - size.getWidth() + value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxProgress, progress, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x + size.getWidth() - value), topPos + point.y, (int) (size.getX() - value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }

                        }
                    }
                }

                case FUEL -> {
                    if(values.contains("remainingFuel") && values.contains("fuelDuration")) {
                        int fuel = values.getInt("remainingFuel");
                        int maxFuel = values.getInt("fuelDuration");
                        RenderSystem.setShaderTexture(0, drawable.texture());
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxFuel, fuel, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y - size.getHeight() + value), (int) size.getX(), (int) (size.getY() - size.getHeight() + value), (int) size.getWidth(), value - 1);
                            }
                            case UP -> {
                                int value = progress(maxFuel, fuel, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y + size.getHeight() - value), (int) size.getX(), (int) (size.getY() - value), (int) size.getWidth(), value - 1);
                            }
                            case LEFT -> {
                                int value = progress(maxFuel, fuel, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x - size.getWidth() + value), topPos + point.y, (int) (size.getX() - size.getWidth() + value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxFuel, fuel, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x + size.getWidth() - value), topPos + point.y, (int) (size.getX() - value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }

                        }
                    }
                }

                case ENERGY -> {
                    IEnergyStorage energyCap = this.menu.blockEntity.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                    if (energyCap != null) {
                        int energy = energyCap.getEnergyStored();
                        int maxEnergy = energyCap.getMaxEnergyStored();
                        RenderSystem.setShaderTexture(0, drawable.texture());
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxEnergy, energy, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y - size.getHeight() + value), (int) size.getX(), (int) (size.getY() - size.getHeight() + value), (int) size.getWidth(), value - 1);
                            }
                            case UP -> {
                                int value = progress(maxEnergy, energy, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y + size.getHeight() - value), (int) size.getX(), (int) (size.getY() - value), (int) size.getWidth(), value - 1);
                            }
                            case LEFT -> {
                                int value = progress(maxEnergy, energy, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x - size.getWidth() + value), topPos + point.y, (int) (size.getX() - size.getWidth() + value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxEnergy, energy, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x + size.getWidth() - value), topPos + point.y, (int) (size.getX() - value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }

                        }
                    }
                }

                case FLUID -> {
                    IFluidHandler fluidCap = this.menu.blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
                    int tankIndex = drawable.handlerIndex();
                    if (fluidCap != null && tankIndex != -1) {
                        int fluid = fluidCap.getFluidInTank(tankIndex).getAmount();
                        int maxFluid = fluidCap.getTankCapacity(tankIndex);
                        RenderSystem.setShaderTexture(0, drawable.texture());
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxFluid, fluid, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y - size.getHeight() + value), (int) size.getX(), (int) (size.getY() - size.getHeight() + value), (int) size.getWidth(), value - 1);
                            }
                            case UP -> {
                                int value = progress(maxFluid, fluid, (int) size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, (int) (topPos + point.y + size.getHeight() - value), (int) size.getX(), (int) (size.getY() - value), (int) size.getWidth(), value - 1);
                            }
                            case LEFT -> {
                                int value = progress(maxFluid, fluid, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x - size.getWidth() + value), topPos + point.y, (int) (size.getX() - size.getWidth() + value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxFluid, fluid, (int) size.getWidth());
                                this.blit(pPoseStack, (int) (leftPos + point.x + size.getWidth() - value), topPos + point.y, (int) (size.getX() - value), (int) size.getY(), value - 1, (int) size.getHeight());
                            }

                        }
                    }
                }

            }
        }

    }

}
