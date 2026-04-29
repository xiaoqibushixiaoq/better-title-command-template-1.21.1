package com.better.title.command.client.network;

import com.better.title.command.client.gui.CustomTitleRenderer;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;

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
            
            // 检查是否为更新操作（使用特殊标记：fadeIn=-1, stay=-1, fadeOut=-1）
            if (payload.fadeIn() == -1 && payload.stay() == -1 && payload.fadeOut() == -1) {
                // 更新每个组的变换参数或文本内容
                for (Map.Entry<String, com.better.title.command.component.TextGroup> entry : payload.groups().entrySet()) {
                    String groupId = entry.getKey();
                    com.better.title.command.component.TextGroup group = entry.getValue();
                    
                    // 如果组已存在，更新它；否则创建新组
                    if (titleRenderer.hasGroup(groupId)) {
                        // 检查组是否有文本内容
                        if (!group.getSegments().isEmpty()) {
                            // 有文本内容，更新文本（保留变换参数）
                            titleRenderer.updateGroupText(groupId, group);
                        } else {
                            // 没有文本内容，判断是单个参数更新还是多个参数更新
                            // 通过检查是否只有一个非默认值来判断
                            boolean hasOffset = (group.getGroupOffsetX() != 0 || group.getGroupOffsetY() != 0);
                            boolean hasScale = (group.getGroupScaleX() != 1.0f || group.getGroupScaleY() != 1.0f);
                            boolean hasRotation = (group.getGroupRotation() != 0);
                            
                            int paramCount = 0;
                            if (hasOffset) paramCount++;
                            if (hasScale) paramCount++;
                            if (hasRotation) paramCount++;
                            
                            if (paramCount == 1) {
                                // 单个参数更新
                                if (hasOffset) {
                                    if (group.getGroupOffsetX() != 0) {
                                        titleRenderer.updateSingleParam(groupId, "offsetX", group.getGroupOffsetX());
                                    } else {
                                        titleRenderer.updateSingleParam(groupId, "offsetY", group.getGroupOffsetY());
                                    }
                                } else if (hasScale) {
                                    if (group.getGroupScaleX() != 1.0f) {
                                        titleRenderer.updateSingleParam(groupId, "scaleX", group.getGroupScaleX());
                                    } else {
                                        titleRenderer.updateSingleParam(groupId, "scaleY", group.getGroupScaleY());
                                    }
                                } else if (hasRotation) {
                                    titleRenderer.updateSingleParam(groupId, "rotation", group.getGroupRotation());
                                }
                            } else {
                                // 多个参数更新
                                titleRenderer.updateGroupTransform(
                                    groupId,
                                    group.getGroupOffsetX(),
                                    group.getGroupOffsetY(),
                                    group.getGroupScaleX(),
                                    group.getGroupScaleY(),
                                    group.getGroupRotation()
                                );
                            }
                        }
                    } else {
                        // 组不存在，创建新组
                        titleRenderer.addOrUpdateGroups(payload.groups(), 10, 60, 20);
                    }
                }
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
