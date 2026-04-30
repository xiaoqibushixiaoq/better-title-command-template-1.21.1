package com.better.title.command.client.network;

import com.better.title.command.client.gui.CustomTitleRenderer;
import com.better.title.command.component.TextGroup;
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
     * 从渲染器中获取现有的TextGroup
     */
    private static TextGroup getExistingGroupFromRenderer(CustomTitleRenderer renderer, String groupId) {
        return renderer.getGroup(groupId);
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
                for (Map.Entry<String, TextGroup> entry : payload.groups().entrySet()) {
                    String groupId = entry.getKey();
                    TextGroup group = entry.getValue();
                    
                    // 如果组已存在，更新它；否则创建新组
                    if (titleRenderer.hasGroup(groupId)) {
                        // 检查组是否有文本内容
                        if (!group.getSegments().isEmpty()) {
                            // 有文本内容，更新文本（保留变换参数）
                            titleRenderer.updateGroupText(groupId, group);
                        } else {
                            // 没有文本内容，检查是否是单参数更新
                            String updateParamName = group.getUpdateParamName();
                            
                            if (updateParamName != null && !updateParamName.isEmpty()) {
                                // 单参数更新：直接调用updateSingleParam
                                titleRenderer.updateSingleParam(groupId, updateParamName, group.getUpdateParamValue());
                            } else {
                                // 多参数更新：使用updateGroupTransform
                                TextGroup existingGroup = titleRenderer.getGroup(groupId);
                                if (existingGroup != null) {
                                    float offsetX = group.getGroupOffsetX();
                                    float offsetY = group.getGroupOffsetY();
                                    float scaleX = group.getGroupScaleX();
                                    float scaleY = group.getGroupScaleY();
                                    float rotation = group.getGroupRotation();
                                    
                                    // 只更新非MAX_VALUE的参数
                                    if (offsetX != Float.MAX_VALUE || offsetY != Float.MAX_VALUE) {
                                        float finalOffsetX = (offsetX == Float.MAX_VALUE) ? existingGroup.getGroupOffsetX() : offsetX;
                                        float finalOffsetY = (offsetY == Float.MAX_VALUE) ? existingGroup.getGroupOffsetY() : offsetY;
                                        existingGroup.setGroupOffset(finalOffsetX, finalOffsetY);
                                    }
                                    
                                    if (scaleX != Float.MAX_VALUE || scaleY != Float.MAX_VALUE) {
                                        float finalScaleX = (scaleX == Float.MAX_VALUE) ? existingGroup.getGroupScaleX() : scaleX;
                                        float finalScaleY = (scaleY == Float.MAX_VALUE) ? existingGroup.getGroupScaleY() : scaleY;
                                        existingGroup.setGroupScale(finalScaleX, finalScaleY);
                                    }
                                    
                                    if (rotation != Float.MAX_VALUE) {
                                        existingGroup.setGroupRotation(rotation);
                                    }
                                }
                            }
                        }
                    } else {
                        // 组不存在，创建新组（使用合理的默认时间）
                        titleRenderer.addOrUpdateGroups(payload.groups(), 500, 3000, 1000);
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
