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
 * Gui Mixin - 在HUD上渲染自定义title
 */
@Mixin(Gui.class)
public class CustomTitleRenderMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // 渲染自定义title
        TransformClientHandler.getRenderer().render(guiGraphics);
    }
}
