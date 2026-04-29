package com.better.title.command.component;

import net.minecraft.network.chat.Component;

/**
 * 变换文本组件 - 支持X/Y偏移和X/Y缩放
 * 这是一个简单的数据类，用于存储文本及其变换属性
 */
public class TransformedText {
    private final Component content;
    private final float offsetX;
    private final float offsetY;
    private final float scaleX;
    private final float scaleY;

    public TransformedText(Component content, float offsetX, float offsetY, float scaleX, float scaleY) {
        this.content = content;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Component getContent() {
        return content;
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
