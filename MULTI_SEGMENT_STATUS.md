# 多文本片段功能 - 实现状态

## ✅ 已完成的部分

### 1. TextSegment类
- 位置: `src/main/java/com/better/title/command/component/TextSegment.java`
- 功能: 存储单个文本片段及其变换参数(offsetX, offsetY, scaleX, scaleY)
- 状态: ✅ 编译成功

### 2. 网络包结构
- 位置: `src/main/java/com/better/title/command/network/TransformNetworkHandler.java`
- 功能: TitleTransformPayload支持List<TextSegment>
- 状态: ⚠️ 需要修复Component序列化问题

### 3. 客户端渲染器
- 位置: `src/client/java/com/better/title/command/client/gui/CustomTitleRenderer.java`
- 功能: 支持渲染多个文本片段，每个片段独立变换
- 状态: ✅ 逻辑完成

### 4. 命令系统
- 位置: `src/main/java/com/better/title/command/command/EnhancedTitleCommand.java`
- 功能: 简单模式（单一片段）已实现
- 状态: ✅ 编译成功

## ⚠️ 待解决的问题

### Component序列化问题
Minecraft 1.21.1的`Component.Serializer.toJson()`和`fromJson()`需要Provider参数：
```java
// 需要Provider
Component.Serializer.toJson(component, registryAccess);
Component.Serializer.fromJson(json, registryAccess);
```

**解决方案选项**:

#### 方案A: 使用Codec（推荐）
```java
// 编码
JsonElement json = ComponentSerialization.CODEC.encodeStart(
    NbtOps.INSTANCE, component
).getOrThrow();

// 解码  
Component component = ComponentSerialization.CODEC.parse(
    NbtOps.INSTANCE, jsonElement
).getOrThrow();
```

#### 方案B: 在命令执行时传递registryAccess
修改网络包发送方法，接受registryAccess参数进行序列化。

#### 方案C: 简化为纯文本
暂时只支持纯文本字符串，不支持富文本格式。

## 📋 下一步计划

1. **修复Component序列化** - 选择上述方案之一
2. **测试多片段功能** - 确保多个文本片段能正确显示
3. **添加高级命令** - 创建支持多个片段的命令语法
4. **更新文档** - 编写完整的使用指南

## 💡 建议的实现方式

考虑到复杂性，建议分阶段实现：

### 第一阶段（当前）
- ✅ 单一片段功能正常工作
- 保持现有命令格式

### 第二阶段
- 修复Component序列化
- 添加内部API支持多片段

### 第三阶段
- 创建高级命令语法
- 提供示例和文档

---

**当前状态**: 基础架构已完成，需要解决Component序列化问题才能完全启用多片段功能。
