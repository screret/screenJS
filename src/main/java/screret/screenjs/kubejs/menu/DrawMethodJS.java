package screret.screenjs.kubejs.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import screret.screenjs.client.AbstractContainerScreen;
import screret.screenjs.common.AbstractContainerMenu;

@FunctionalInterface
public interface DrawMethodJS {
    void draw(PoseStack poseStack, int leftPos, int topPos, AbstractContainerMenu<?> menu, AbstractContainerScreen<?> screen, MenuTypeBuilder.ProgressDrawable drawable, MenuTypeBuilder.MoveDirection direction);
}
