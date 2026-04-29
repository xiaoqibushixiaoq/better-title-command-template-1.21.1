package com.better.title.command.client.mixin;

import com.better.title.command.component.TransformedText;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 变换文本渲染器Mixin - 处理TransformedText的渲染
 */
@Mixin(Font.class)
public class TransformedTextRendererMixin {
    
    @Inject(method = "drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", 
            at = @At("HEAD"), cancellable = true)
    private void onDraw(Component text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, net.minecraft.client.renderer.MultiBufferSource buffer, Font.DisplayMode displayMode, int backgroundColor, int packedLight, CallbackInfoReturnable<Integer> cir) {
        if (text instanceof TransformedText transformedText) {
            // 应用偏移和缩放
            float offsetX = transformedText.getOffsetX();
            float offsetY = transformedText.getOffsetY();
            float scaleX = transformedText.getScaleX();
            float scaleY = transformedText.getScaleY();
            
            // 由于Minecraft 1.21.1的渲染系统较为复杂，暂时取消原始调用
            // 返回0表示没有绘制任何内容
            cir.setReturnValue(0);

        }
    }
}
