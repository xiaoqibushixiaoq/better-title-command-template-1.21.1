package com.better.title.command.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * 自定义Title渲染器 - 支持偏移和缩放
 */
public class CustomTitleRenderer {
    private final Minecraft client;
    private Component titleText;
    private Component subtitleText;
    
    // 变换参数
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    
    // 显示控制
    private int displayTime = 60; // 停留时间（tick）
    private int fadeInTime = 10;  // 淡入时间
    private int fadeOutTime = 20; // 淡出时间
    private int timer = 0;
    private boolean visible = false;
    
    public CustomTitleRenderer(Minecraft client) {
        this.client = client;
    }
    
    /**
     * 设置title文本和变换参数
     */
    public void setTitle(Component title, Component subtitle, 
                        float offsetX, float offsetY, 
                        float scaleX, float scaleY,
                        int fadeIn, int stay, int fadeOut) {
        this.titleText = title;
        this.subtitleText = subtitle;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.fadeInTime = fadeIn;
        this.displayTime = stay;
        this.fadeOutTime = fadeOut;
        this.timer = 0;
        this.visible = true;
    }
    
    /**
     * 设置actionbar文本和变换参数
     */
    public void setActionbar(Component text, 
                            float offsetX, float offsetY,
                            float scaleX, float scaleY,
                            int displayTime) {
        this.titleText = text;
        this.subtitleText = null;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.displayTime = displayTime;
        this.fadeInTime = 0;
        this.fadeOutTime = 0;
        this.timer = 0;
        this.visible = true;
    }
    
    /**
     * 渲染title
     */
    public void render(GuiGraphics guiGraphics) {
        if (!visible || titleText == null) {
            return;
        }
        
        timer++;
        
        // 检查是否应该消失
        if (timer > fadeInTime + displayTime + fadeOutTime) {
            visible = false;
            titleText = null;
            subtitleText = null;
            return;
        }
        
        // 计算透明度
        float alpha = 1.0f;
        if (timer < fadeInTime) {
            alpha = timer / (float)fadeInTime;
        } else if (timer > fadeInTime + displayTime) {
            alpha = 1.0f - (timer - fadeInTime - displayTime) / (float)fadeOutTime;
        }
        
        if (alpha <= 0.0f) {
            return;
        }
        
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        
        Font font = client.font;
        
        // 保存矩阵状态
        guiGraphics.pose().pushPose();
        
        // 应用变换
        guiGraphics.pose().translate(screenWidth / 2.0f + offsetX, screenHeight / 2.0f + offsetY, 0);
        guiGraphics.pose().scale(scaleX, scaleY, 1.0f);
        
        // 渲染title
        if (titleText != null) {
            int titleWidth = font.width(titleText);
            int titleX = -titleWidth / 2;
            int titleY = -10;
            
            int color = Mth.ceil(alpha * 255.0f) << 24 | 0xFFFFFF;
            guiGraphics.drawString(font, titleText, titleX, titleY, color);
        }
        
        // 渲染subtitle
        if (subtitleText != null) {
            int subtitleWidth = font.width(subtitleText);
            int subtitleX = -subtitleWidth / 2;
            int subtitleY = 10;
            
            int color = Mth.ceil(alpha * 255.0f) << 24 | 0xFFFFFF;
            guiGraphics.drawString(font, subtitleText, subtitleX, subtitleY, color);
        }
        
        // 恢复矩阵状态
        guiGraphics.pose().popPose();
    }
    
    /**
     * 清除当前显示的文本
     */
    public void clear() {
        visible = false;
        titleText = null;
        subtitleText = null;
        timer = 0;
    }
    
    public boolean isVisible() {
        return visible;
    }
}
