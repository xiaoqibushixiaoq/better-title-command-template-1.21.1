package com.better.title.command.client.mixin;

import com.better.title.command.client.network.TransformClientHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Gui Mixin - 在HUD渲染的最后阶段渲染自定义title
 */
@Mixin(Gui.class)
public class CustomTitleRenderMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // 在render方法的最后渲染
        TransformClientHandler.getRenderer().render(guiGraphics);
    }
}
