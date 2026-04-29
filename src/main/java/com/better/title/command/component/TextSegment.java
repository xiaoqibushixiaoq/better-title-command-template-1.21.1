package com.better.title.command.component;

import net.minecraft.network.chat.Component;

/**
 * 单个文本片段 - 基础文本单元
 */
public class TextSegment {
    private final Component text;
    private final float offsetX;
    private final float offsetY;
    private final float scaleX;
    private final float scaleY;
    
    public TextSegment(Component text, float offsetX, float offsetY, float scaleX, float scaleY) {
        this.text = text;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public Component getText() {
        return text;
    }
    
    public float getOffsetX() {
        return offsetX;
    }
    
    public float getOffsetY() {
        return offsetY;
    }
    
    public float getScaleX() {
        return scaleX;
    }
    
    public float getScaleY() {
        return scaleY;
    }
}
