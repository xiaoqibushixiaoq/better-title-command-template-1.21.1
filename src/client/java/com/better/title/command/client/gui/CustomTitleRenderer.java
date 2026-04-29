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
 * 
 * 功能特性：
 * - 支持多个文本组同时显示
 * - 每个组有独立的淡入/显示/淡出时间
 * - 支持永久显示模式（stay=-1）
 * - 支持文本旋转、缩放、偏移变换
 * - 支持运行时修改组的参数
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
        long startTime = -1;  // 开始时间（毫秒）
        int fadeInTime;       // 淡入时间（毫秒）
        int displayTime;      // 显示时间（毫秒），-1表示永久
        int fadeOutTime;      // 淡出时间（毫秒）
        boolean active = true;
        
        GroupTimer(int fadeInMs, int stayMs, int fadeOutMs) {
            // 直接使用毫秒值
            this.fadeInTime = fadeInMs;
            this.displayTime = stayMs;
            this.fadeOutTime = fadeOutMs;
        }
        
        /**
         * 初始化计时器
         * @param currentTime 当前时间（毫秒）
         */
        void init(long currentTime) {
            if (startTime == -1) {
                startTime = currentTime;
            }
        }
        
        /**
         * 获取经过的时间（毫秒）
         * @param currentTime 当前时间（毫秒）
         * @return 经过的时间
         */
        long getElapsedTime(long currentTime) {
            if (startTime == -1) {
                return 0;
            }
            return currentTime - startTime;
        }
        
        boolean shouldRemove(long currentTime) {
            // 如果displayTime为-1，表示永久显示，不自动移除
            if (displayTime < 0) {
                return false;
            }
            long elapsed = getElapsedTime(currentTime);
            return elapsed > fadeInTime + displayTime + fadeOutTime;
        }
        
        float getAlpha(long currentTime) {
            long elapsed = getElapsedTime(currentTime);
            
            // 未开始或刚开始时完全透明
            if (elapsed <= 0) {
                return 0.0f;
            }
            
            // 淡入阶段
            if (elapsed < fadeInTime && fadeInTime > 0) {
                return (float)elapsed / (float)fadeInTime;
            }
            
            // 如果是永久显示（displayTime < 0），淡入后一直保持不透明
            if (displayTime < 0) {
                return 1.0f;
            }
            
            // 正常模式：检查是否进入淡出阶段
            if (elapsed > fadeInTime + displayTime && fadeOutTime > 0) {
                return 1.0f - (float)(elapsed - fadeInTime - displayTime) / (float)fadeOutTime;
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
     * 
     * @param newGroups 要添加或更新的文本组映射
     * @param fadeIn 淡入时间（毫秒），0表示立即显示
     * @param stay 显示时间（毫秒），-1表示永久显示
     * @param fadeOut 淡出时间（毫秒），0表示立即消失
     */
    public void addOrUpdateGroups(Map<String, TextGroup> newGroups, int fadeIn, int stay, int fadeOut) {
        // 将新组合并到现有组中
        for (Map.Entry<String, TextGroup> entry : newGroups.entrySet()) {
            String groupId = entry.getKey();
            TextGroup newGroup = entry.getValue();
            
            // 如果组已存在，先清除旧的计时器（避免闪烁）
            if (groups.containsKey(groupId)) {
                groupTimers.remove(groupId);
            }
            
            // 添加或替换组
            groups.put(groupId, newGroup);
            
            // 为该组创建新的计时器
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
     * 更新组的变换参数（不改变文本内容和计时器）
     */
    public void updateGroupTransform(String groupId, float offsetX, float offsetY, float scaleX, float scaleY, float rotation) {
        TextGroup existingGroup = groups.get(groupId);
        if (existingGroup != null) {
            // 更新所有提供的参数
            // 注意：这个方法应该只在明确知道要更新哪些参数时调用
            existingGroup.setGroupOffset(offsetX, offsetY);
            existingGroup.setGroupScale(scaleX, scaleY);
            existingGroup.setGroupRotation(rotation);
        }
    }
    
    /**
     * 更新单个变换参数
     */
    public void updateSingleParam(String groupId, String paramName, float value) {
        TextGroup existingGroup = groups.get(groupId);
        if (existingGroup != null) {
            switch (paramName) {
                case "offsetX":
                    existingGroup.setGroupOffset(value, existingGroup.getGroupOffsetY());
                    break;
                case "offsetY":
                    existingGroup.setGroupOffset(existingGroup.getGroupOffsetX(), value);
                    break;
                case "scaleX":
                    existingGroup.setGroupScale(value, existingGroup.getGroupScaleY());
                    break;
                case "scaleY":
                    existingGroup.setGroupScale(existingGroup.getGroupScaleX(), value);
                    break;
                case "rotation":
                    existingGroup.setGroupRotation(value);
                    break;
            }
        }
    }
    
    /**
     * 更新组的文本内容（不改变变换参数和计时器）
     */
    public void updateGroupText(String groupId, TextGroup newGroup) {
        TextGroup existingGroup = groups.get(groupId);
        if (existingGroup != null) {
            // 保留现有的变换参数
            newGroup.setGroupOffset(
                existingGroup.getGroupOffsetX(),
                existingGroup.getGroupOffsetY()
            );
            newGroup.setGroupScale(
                existingGroup.getGroupScaleX(),
                existingGroup.getGroupScaleY()
            );
            newGroup.setGroupRotation(existingGroup.getGroupRotation());
            
            // 替换组
            groups.put(groupId, newGroup);
        }
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
        
        // 获取当前系统时间（毫秒）- 基于真实时间而非tick
        long currentTime = System.currentTimeMillis();
        
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
            
            // 初始化计时器（如果是第一次）
            timer.init(currentTime);
            
            // 计算alpha值
            float alpha = timer.getAlpha(currentTime);
            
            // 检查组是否应该移除（先收集，稍后统一删除）
            if (timer.shouldRemove(currentTime)) {
                groupsToRemove.add(groupId);
                continue;
            }
            
            // 如果alpha太小或为0，跳过渲染
            // 使用严格大于0的判断，避免极小alpha导致的闪烁
            if (alpha <= 0.0f) {
                hasActiveGroups = true;  // 仍然标记为活跃，但不渲染
                continue;
            }
            
            // 计算颜色，确保alpha通道至少为4才渲染（避免极淡文本闪烁）
            int alphaChannel = Mth.ceil(alpha * 255.0f);
            if (alphaChannel < 4) {
                hasActiveGroups = true;  // 仍然标记为活跃，但不渲染
                continue;
            }
            
            hasActiveGroups = true;  // 标记为活跃
            
            int color = alphaChannel << 24 | 0xFFFFFF;
            
            // 获取组级别变换
            float groupOffsetX = group.getGroupOffsetX();
            float groupOffsetY = group.getGroupOffsetY();
            float groupScaleX = group.getGroupScaleX();
            float groupScaleY = group.getGroupScaleY();
            float groupRotation = group.getGroupRotation();
            
            // 渲染组内的每个文本片段
            for (TextSegment segment : group.getSegments()) {
                // 保存矩阵状态
                guiGraphics.pose().pushPose();
                
                // 应用组级别变换 + 片段级别变换
                float finalOffsetX = groupOffsetX + segment.getOffsetX();
                float finalOffsetY = groupOffsetY + segment.getOffsetY();
                float finalScaleX = groupScaleX * segment.getScaleX();
                float finalScaleY = groupScaleY * segment.getScaleY();
                float finalRotation = groupRotation + segment.getRotation();
                
                guiGraphics.pose().translate(screenWidth / 2.0f + finalOffsetX, screenHeight / 2.0f + finalOffsetY, 0);
                guiGraphics.pose().scale(finalScaleX, finalScaleY, 1.0f);
                // 应用旋转（将角度转换为弧度）
                if (finalRotation != 0) {
                    guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(finalRotation));
                }
                
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
    
    public boolean hasGroup(String groupId) {
        return groups.containsKey(groupId);
    }
}
