package com.better.title.command.client.mixin;

import com.better.title.command.client.network.TransformClientHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Gui Mixin - 修改renderTitle layer，在其之前渲染自定义title
 */
@Mixin(Gui.class)
public class CustomTitleRenderMixin {
    
    /**
     * 拦截对this::renderTitle的方法引用，将其包装成组合layer
     */
    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/minecraft/client/gui/LayeredDraw;",
            ordinal = 7
        )
    )
    private LayeredDraw.Layer wrapRenderTitle(LayeredDraw.Layer originalLayer) {
        // 创建组合layer：先渲染自定义title，再渲染原版title
        return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            TransformClientHandler.getRenderer().render(guiGraphics);
            originalLayer.render(guiGraphics, deltaTracker);
        };
    }
}
