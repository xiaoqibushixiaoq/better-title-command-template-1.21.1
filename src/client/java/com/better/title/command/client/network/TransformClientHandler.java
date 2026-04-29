package com.better.title.command.client.network;

import com.better.title.command.client.gui.CustomTitleRenderer;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;

/**
 * 客户端网络包接收器 - 接收并显示带变换的title
 */
public class TransformClientHandler {
    
    private static CustomTitleRenderer renderer;
    
    /**
     * 获取或创建渲染器实例
     */
    public static CustomTitleRenderer getRenderer() {
        if (renderer == null) {
            renderer = new CustomTitleRenderer(net.minecraft.client.Minecraft.getInstance());
        }
        return renderer;
    }
    
    /**
     * 处理Title变换
     */
    public static void handleTitleTransform(TransformNetworkHandler.TitleTransformPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            // 从JSON字符串解析Component
            Component title = Component.Serializer.fromJson(payload.titleJson(), context.client().player.registryAccess());
            Component subtitle = Component.Serializer.fromJson(payload.subtitleJson(), context.client().player.registryAccess());
            
            CustomTitleRenderer titleRenderer = getRenderer();
            titleRenderer.setTitle(
                title,
                subtitle,
                payload.offsetX(),
                payload.offsetY(),
                payload.scaleX(),
                payload.scaleY(),
                payload.fadeIn(),
                payload.stay(),
                payload.fadeOut()
            );
        });
    }
}
