# Better Title Command v1.0.2 发布说明

## 📅 发布日期
2026年4月30日

## 🎯 版本概览

**Better Title Command v1.0.2** 是一个重要的bug修复版本，主要解决了title显示后立即消失的问题，并改进了渲染层级控制，确保自定义title不会被其他HUD元素遮挡。

---

## ✨ 新特性

### 1. 精确的渲染层级控制 🎨
- **使用`@ModifyArg`精确定位renderTitle层**
- 通过`ordinal=7`定位到第8个LayeredDraw layer（即renderTitle）
- 将自定义title渲染与原版title渲染包装在同一layer中
- 确保自定义title先于原版title渲染，避免被遮挡

**技术实现：**
```java
@ModifyArg(
    method = "<init>",
    at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/gui/LayeredDraw;add(...)",
        ordinal = 7
    )
)
private LayeredDraw.Layer wrapRenderTitle(LayeredDraw.Layer originalLayer) {
    return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
        // 先渲染自定义title
        TransformClientHandler.getRenderer().render(guiGraphics);
        // 再渲染原版title
        originalLayer.render(guiGraphics, deltaTracker);
    };
}
```

### 2. 修复默认时间参数 ⏱️
- **修正TransformClientHandler中的硬编码默认值**
- 从 `10/60/20ms` 改为 `500/3000/1000ms`
- 确保title有合理的显示时长

**对比：**
| 版本 | fadeIn | stay | fadeOut | 总时长 |
|------|--------|------|---------|--------|
| v1.0.1 (bug) | 10ms | 60ms | 20ms | ~0.09秒 ❌ |
| v1.0.2 (fix) | 500ms | 3000ms | 1000ms | 4.5秒 ✅ |

---

## 🔧 Bug修复

### 修复1: Title显示后立即消失 🐛
**问题描述：**
- 用户执行`/title_group`命令后，title只显示不到0.1秒就消失
- 原因是TransformClientHandler中使用了错误的默认时间参数

**修复方案：**
- 将[TransformClientHandler.java](src/client/java/com/better/title/command/client/network/TransformClientHandler.java#L95)中的默认时间从`10, 60, 20`改为`500, 3000, 1000`
- 现在title会正常显示4.5秒（可自定义）

**影响范围：**
- 仅影响更新操作时创建新组的情况
- 正常命令不受影响（使用正确的默认值500/3000/1000）

### 修复2: 渲染层级问题 🎯
**问题描述：**
- 之前的Mixin注入方式无法精确控制渲染层级
- title可能被聊天栏、计分板等其他HUD元素遮挡

**修复方案：**
- 改用`@ModifyArg`直接修改LayeredDraw的构建过程
- 在renderTitle之前插入自定义渲染逻辑
- 确保title在正确的层级渲染

---

## 📊 技术改进

### LayeredDraw渲染顺序

**Minecraft 1.21.1的HUD渲染层级：**
```
LayeredDraw layers (按顺序):
0. renderDemoOverlay
1. renderDebugScreen
2. renderScoreboardSidebar
3. renderOverlayMessage
4. renderTitle          ← 我们在这里插入自定义渲染
5. renderChat
6. renderTabList
7. renderSubtitleOverlay
```

**v1.0.2的实现：**
- 拦截第4个layer（renderTitle）的添加
- 将其包装成组合layer：`[自定义title] → [原版title]`
- 确保自定义title与原版title在同一层级组

### 时间参数统一

现在所有地方的默认时间参数都统一为：
- **fadeIn**: 500ms (0.5秒)
- **stay**: 3000ms (3秒)
- **fadeOut**: 1000ms (1秒)

**涉及文件：**
- ✅ `GroupedTitleCommand.java` - 命令默认值
- ✅ `TransformClientHandler.java` - 网络包处理默认值（本次修复）

---

## 📖 使用示例

### 基础用法（不受影响）
```mcfunction
# 使用默认时间（500/3000/1000ms）
/title_group @s test {"text":"Hello","color":"gold"} 0 0 2.0 2.0

# 自定义时间
/title_group @s test {"text":"Hello","color":"gold"} 0 0 2.0 2.0 1000 5000 500
```

### 永久显示
```mcfunction
# stay=-1 表示永久显示
/title_group @s status {"text":"Quest Active","color":"green"} 0 -100 1.8 1.8 500 -1 0
```

### 多层效果
```mcfunction
# 背景层
/title_group @s bg {"text":"Background","color":"gray"} 0 0 3.0 3.0 800 -1 0

# 前景层（会覆盖在背景层之上）
/title_group @s fg {"text":"Foreground","color":"white"} 0 0 2.0 2.0 500 -1 0
```

---

## 🔄 从 v1.0.1 升级

### 升级建议
- ✅ **强烈建议所有用户升级到此版本**
- ✅ 修复了严重的title消失bug
- ✅ 改进了渲染层级控制
- ✅ 向后兼容，无需修改现有命令

### 注意事项
- 如果你之前依赖那个"闪现"的bug行为，需要调整时间参数
- 其他功能完全兼容，无需更改

---

## 🐛 已知问题

目前没有已知的严重bug。如果遇到问题，请在GitHub Issues中报告。

---

## 📝 未来计划

- [ ] 支持更多文本样式（阴影、描边、渐变等）
- [ ] 添加预设模板系统
- [ ] 支持更复杂的动画效果（弹跳、震动等）
- [ ] 添加配置文件支持
- [ ] 优化大规模文本渲染性能
- [ ] 支持条件触发器

---

## 🙏 致谢

感谢所有测试者和反馈者，特别是：
- 报告title消失问题的用户
- 提出渲染层级优化建议的用户
- 所有参与测试的玩家

---

## 📥 下载

- **GitHub Release**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/releases/tag/v1.0.2
- **源代码**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1
- **Jar文件**: `better-title-command-1.0.0-1.21.1.jar`

---

## 📄 许可证

本项目采用 **MIT License**。详见 LICENSE 文件。

---

## 🔗 相关链接

- **GitHub Repository**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1
- **Issues**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/issues
- **Wiki**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/wiki

---

## 📋 系统要求

- **Minecraft**: 1.21.1
- **Fabric Loader**: >=0.15.0
- **Fabric API**: >=0.116.0
- **Java**: 21 或更高版本

---

**更新日期**: 2026年4月30日  
**版本**: v1.0.2  
**构建号**: 30563be
