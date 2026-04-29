package com.better.title.command.client.network;

import com.better.title.command.client.gui.CustomTitleRenderer;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
            CustomTitleRenderer titleRenderer = getRenderer();
            
            // 如果groups为空，清除所有
            if (payload.groups().isEmpty()) {
                titleRenderer.clearAll();
                return;
            }
            
            // 使用累加模式，保留现有的组
            titleRenderer.addOrUpdateGroups(
                payload.groups(),
                payload.fadeIn(),
                payload.stay(),
                payload.fadeOut()
            );
        });
    }
}
