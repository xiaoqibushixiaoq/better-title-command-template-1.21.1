package com.better.title.command.client.gui;

import com.better.title.command.component.TextGroup;
import com.better.title.command.component.TextSegment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义Title渲染器 - 支持多个文本组，每组可独立管理
 */
public class CustomTitleRenderer {
    private final Minecraft client;
    private Map<String, TextGroup> groups;
    
    // 每个组的独立计时器
    private Map<String, GroupTimer> groupTimers;
    
    // 默认显示控制（用于向后兼容）
    private int defaultFadeInTime = 10;
    private int defaultDisplayTime = 60;
    private int defaultFadeOutTime = 20;
    
    private boolean visible = false;
    
    /**
     * 组计时器 - 记录每个组的显示状态
     */
    private static class GroupTimer {
        int timer = 0;
        int fadeInTime;
        int displayTime;
        int fadeOutTime;
        boolean active = true;
        
        GroupTimer(int fadeIn, int stay, int fadeOut) {
            this.fadeInTime = fadeIn;
            this.displayTime = stay;
            this.fadeOutTime = fadeOut;
        }
        
        boolean shouldRemove() {
            return timer > fadeInTime + displayTime + fadeOutTime;
        }
        
        float getAlpha() {
            if (timer < fadeInTime) {
                return timer / (float)fadeInTime;
            } else if (timer > fadeInTime + displayTime) {
                return 1.0f - (timer - fadeInTime - displayTime) / (float)fadeOutTime;
            }
            return 1.0f;
        }
    }
    
    public CustomTitleRenderer(Minecraft client) {
        this.client = client;
        this.groups = new HashMap<>();
        this.groupTimers = new HashMap<>();
    }
    
    /**
     * 添加或更新文本组（累加模式）
     */
    public void addOrUpdateGroups(Map<String, TextGroup> newGroups, int fadeIn, int stay, int fadeOut) {
        // 将新组合并到现有组中
        for (Map.Entry<String, TextGroup> entry : newGroups.entrySet()) {
            String groupId = entry.getKey();
            TextGroup newGroup = entry.getValue();
            
            // 添加或替换组
            groups.put(groupId, newGroup);
            
            // 为该组创建或更新计时器
            groupTimers.put(groupId, new GroupTimer(fadeIn, stay, fadeOut));
        }
        
        this.visible = true;
    }
    
    /**
     * 设置文本组（替换模式 - 用于向后兼容）
     */
    public void setTitle(Map<String, TextGroup> groups, int fadeIn, int stay, int fadeOut) {
        // 清除所有现有组和计时器
        this.groups.clear();
        this.groupTimers.clear();
        
        // 添加新组和计时器
        for (Map.Entry<String, TextGroup> entry : groups.entrySet()) {
            this.groups.put(entry.getKey(), entry.getValue());
            this.groupTimers.put(entry.getKey(), new GroupTimer(fadeIn, stay, fadeOut));
        }
        
        this.visible = true;
    }
    
    /**
     * 清除指定组
     */
    public void clearGroup(String groupId) {
        groups.remove(groupId);
        groupTimers.remove(groupId);
        
        // 如果没有组了，隐藏渲染器
        if (groups.isEmpty()) {
            visible = false;
        }
    }
    
    /**
     * 清除所有组
     */
    public void clearAll() {
        groups.clear();
        groupTimers.clear();
        visible = false;
    }
    
    /**
     * 渲染title
     */
    public void render(GuiGraphics guiGraphics) {
        if (!visible || groups.isEmpty()) {
            return;
        }
        
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        Font font = client.font;
        
        boolean hasActiveGroups = false;
        
        // 收集需要移除的组ID（避免在遍历时修改Map）
        List<String> groupsToRemove = new ArrayList<>();
        
        // 渲染每个文本组
        for (Map.Entry<String, TextGroup> entry : groups.entrySet()) {
            String groupId = entry.getKey();
            TextGroup group = entry.getValue();
            GroupTimer timer = groupTimers.get(groupId);
            
            if (group.isEmpty() || timer == null) {
                continue;
            }
            
            // 更新计时器
            timer.timer++;
            
            // 检查组是否应该移除（先收集，稍后统一删除）
            if (timer.shouldRemove()) {
                groupsToRemove.add(groupId);
                continue;
            }
            
            hasActiveGroups = true;
            
            // 计算透明度
            float alpha = timer.getAlpha();
            if (alpha <= 0.0f) {
                continue;
            }
            
            int color = Mth.ceil(alpha * 255.0f) << 24 | 0xFFFFFF;
            
            // 获取组级别变换
            float groupOffsetX = group.getGroupOffsetX();
            float groupOffsetY = group.getGroupOffsetY();
            float groupScaleX = group.getGroupScaleX();
            float groupScaleY = group.getGroupScaleY();
            
            // 渲染组内的每个文本片段
            for (TextSegment segment : group.getSegments()) {
                // 保存矩阵状态
                guiGraphics.pose().pushPose();
                
                // 应用组级别变换 + 片段级别变换
                float finalOffsetX = groupOffsetX + segment.getOffsetX();
                float finalOffsetY = groupOffsetY + segment.getOffsetY();
                float finalScaleX = groupScaleX * segment.getScaleX();
                float finalScaleY = groupScaleY * segment.getScaleY();
                
                guiGraphics.pose().translate(screenWidth / 2.0f + finalOffsetX, screenHeight / 2.0f + finalOffsetY, 0);
                guiGraphics.pose().scale(finalScaleX, finalScaleY, 1.0f);
                
                // 渲染文本
                Component text = segment.getText();
                int textWidth = font.width(text);
                int textX = -textWidth / 2;
                int textY = 0;
                
                guiGraphics.drawString(font, text, textX, textY, color);
                
                // 恢复矩阵状态
                guiGraphics.pose().popPose();
            }
        }
        
        // 在遍历结束后统一删除超时的组
        for (String groupId : groupsToRemove) {
            groups.remove(groupId);
            groupTimers.remove(groupId);
        }
        
        // 如果没有活跃的组，隐藏渲染器
        if (!hasActiveGroups) {
            visible = false;
        }
    }
    
    public boolean isVisible() {
        return visible;
    }
}
