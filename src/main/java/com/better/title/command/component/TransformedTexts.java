package com.better.title.command.component;

import net.minecraft.network.chat.Component;

/**
 * 变换文本工具类 - 简化变换文本的创建
 */
public class TransformedTexts {
    
    /**
     * 创建变换文本
     * @param content 原始文本内容
     * @param offsetX X轴偏移
     * @param offsetY Y轴偏移
     * @param scaleX X轴缩放
     * @param scaleY Y轴缩放
     * @return 变换后的文本组件
     */
    public static TransformedText of(Component content, float offsetX, float offsetY, float scaleX, float scaleY) {
        return new TransformedText(content, offsetX, offsetY, scaleX, scaleY);
    }
    
    /**
     * 创建只带偏移的变换文本（缩放为1.0）
     */
    public static TransformedText offset(Component content, float offsetX, float offsetY) {
        return new TransformedText(content, offsetX, offsetY, 1.0f, 1.0f);
    }
    
    /**
     * 创建只带缩放的变换文本（偏移为0.0）
     */
    public static TransformedText scale(Component content, float scaleX, float scaleY) {
        return new TransformedText(content, 0.0f, 0.0f, scaleX, scaleY);
    }
    
    /**
     * 创建均匀缩放的变换文本
     */
    public static TransformedText uniformScale(Component content, float scale) {
        return new TransformedText(content, 0.0f, 0.0f, scale, scale);
    }
}
