package screret.screenjs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import screret.screenjs.ScreenJS;
import screret.screenjs.common.EntityContainerMenu;
import screret.screenjs.common.AbstractContainerMenu;
import screret.screenjs.packets.C2SRequestSync;

import static screret.screenjs.MenuUtils.progress;

public class EntityContainerScreen extends AbstractContainerScreen<EntityContainerMenu> {

    public EntityContainerScreen(EntityContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public EntityContainerScreen(AbstractContainerMenu<?> pMenu, Inventory pPlayerInventory, Component pTitle) {
        super((EntityContainerMenu) pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        ScreenJS.CHANNEL.sendToServer(new C2SRequestSync());

        var values = this.menu.entity.getPersistentData();
        for (var drawable : menu.builder.progressDrawables) {
            var point = drawable.renderPoint();
            var size = rectCache.get(drawable.texturePos());

            switch (drawable.type()) {
                case CUSTOM -> {
                    drawable.drawer().draw(menu, this, drawable, drawable.direction());
                }

                case PROGRESS -> {
                    if (values.contains("progress") && values.contains("totalProgress")) {
                        int progress = values.getInt("progress");
                        int maxProgress = values.getInt("totalProgress");
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxProgress, progress, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), size.getWidth(), value);
                            }
                            case UP -> {
                                int value = progress(maxProgress, progress, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() + size.getHeight() - value, size.getWidth(), value);
                            }
                            case LEFT -> {
                                int value = progress(maxProgress, progress, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), value, size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxProgress, progress, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() + size.getWidth() - value, size.getY(), value, size.getHeight());
                            }

                        }
                    }
                }

                case FUEL -> {
                    if(values.contains("remainingFuel") && values.contains("fuelDuration")) {
                        int fuel = values.getInt("remainingFuel");
                        int maxFuel = values.getInt("fuelDuration");
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxFuel, fuel, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), size.getWidth(), value);
                            }
                            case UP -> {
                                int value = progress(maxFuel, fuel, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() + size.getHeight() - value, size.getWidth(), value);
                            }
                            case LEFT -> {
                                int value = progress(maxFuel, fuel, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), value, size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxFuel, fuel, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() + size.getWidth() - value, size.getY(), value, size.getHeight());
                            }

                        }
                    }
                }

                case ENERGY -> {
                    IEnergyStorage energyCap = this.menu.entity.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                    if (energyCap != null) {
                        int energy = energyCap.getEnergyStored();
                        int maxEnergy = energyCap.getMaxEnergyStored();
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxEnergy, energy, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), size.getWidth(), value);
                            }
                            case UP -> {
                                int value = progress(maxEnergy, energy, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() + size.getHeight() - value, size.getWidth(), value);
                            }
                            case LEFT -> {
                                int value = progress(maxEnergy, energy, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), value, size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxEnergy, energy, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() + size.getWidth() - value, size.getY(), value, size.getHeight());
                            }

                        }
                    }
                }

                case FLUID -> {
                    IFluidHandler fluidCap = this.menu.entity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
                    int tankIndex = drawable.handlerIndex();
                    if (fluidCap != null && tankIndex != -1) {
                        int fluid = fluidCap.getFluidInTank(tankIndex).getAmount();
                        int maxFluid = fluidCap.getTankCapacity(tankIndex);
                        switch (drawable.direction()) {
                            case DOWN -> {
                                int value = progress(maxFluid, fluid, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), size.getWidth(), value);
                            }
                            case UP -> {
                                int value = progress(maxFluid, fluid, size.getHeight());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y + size.getHeight() - value, size.getX(), size.getY() + size.getHeight() - value, size.getWidth(), value);
                            }
                            case LEFT -> {
                                int value = progress(maxFluid, fluid, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x, topPos + point.y, size.getX(), size.getY(), value, size.getHeight());
                            }
                            case RIGHT -> {
                                int value = progress(maxFluid, fluid, size.getWidth());
                                this.blit(pPoseStack, leftPos + point.x + size.getWidth() - value, topPos + point.y, size.getX() + size.getWidth() - value, size.getY(), value, size.getHeight());
                            }

                        }
                    }
                }

            }

        }

    }

}
