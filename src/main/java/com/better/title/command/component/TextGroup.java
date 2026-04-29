package com.better.title.command.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本组 - 管理一组相关的文本片段
 * 可以对整组应用统一的变换和清除操作
 */
public class TextGroup {
    private final String groupId; // 组ID，用于标识和管理
    private final List<TextSegment> segments;
    
    // 组级别的变换参数（会应用到组内所有文本）
    private float groupOffsetX = 0.0f;
    private float groupOffsetY = 0.0f;
    private float groupScaleX = 1.0f;
    private float groupScaleY = 1.0f;
    private float groupRotation = 0.0f; // 组级别旋转
    
    public TextGroup(String groupId) {
        this.groupId = groupId;
        this.segments = new ArrayList<>();
    }
    
    /**
     * 添加文本片段到组
     */
    public void addSegment(TextSegment segment) {
        segments.add(segment);
    }
    
    /**
     * 获取组ID
     */
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * 获取所有文本片段
     */
    public List<TextSegment> getSegments() {
        return segments;
    }
    
    /**
     * 设置组级别偏移
     */
    public void setGroupOffset(float offsetX, float offsetY) {
        this.groupOffsetX = offsetX;
        this.groupOffsetY = offsetY;
    }
    
    /**
     * 设置组级别缩放
     */
    public void setGroupScale(float scaleX, float scaleY) {
        this.groupScaleX = scaleX;
        this.groupScaleY = scaleY;
    }
    
    /**
     * 设置组级别旋转
     */
    public void setGroupRotation(float rotation) {
        this.groupRotation = rotation;
    }
    
    /**
     * 获取组偏移X
     */
    public float getGroupOffsetX() {
        return groupOffsetX;
    }
    
    /**
     * 获取组偏移Y
     */
    public float getGroupOffsetY() {
        return groupOffsetY;
    }
    
    /**
     * 获取组缩放X
     */
    public float getGroupScaleX() {
        return groupScaleX;
    }
    
    /**
     * 获取组缩放Y
     */
    public float getGroupScaleY() {
        return groupScaleY;
    }
    
    /**
     * 获取组旋转
     */
    public float getGroupRotation() {
        return groupRotation;
    }
    
    /**
     * 清空组内所有文本
     */
    public void clear() {
        segments.clear();
    }
    
    /**
     * 检查组是否为空
     */
    public boolean isEmpty() {
        return segments.isEmpty();
    }
    
    /**
     * 获取文本数量
     */
    public int size() {
        return segments.size();
    }
}
