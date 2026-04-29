# Better Title Command v1.0.0 发布说明

## 🎉 首次正式发布

我们很高兴地宣布 **Better Title Command** 模组的第一个正式版本发布！

## ✨ 核心功能

### 1. 文本组管理系统
- 支持同时显示和管理多个文本组
- 每个组有独立的标识符（groupId）
- 可以单独控制每个组的显示/隐藏

### 2. 强大的变换效果
- **位置偏移** - X/Y轴像素级精确控制
- **缩放变换** - 支持X/Y轴独立缩放
- **旋转功能** - 360度任意角度旋转
- **组合变换** - 可以同时应用多种变换

### 3. 灵活的时间控制
- **淡入时间** (fadeIn) - 控制文本出现动画时长
- **显示时间** (stay) - 控制文本停留时长，支持永久显示（-1）
- **淡出时间** (fadeOut) - 控制文本消失动画时长
- 所有时间以ticks为单位（1秒=20 ticks）

### 4. 运行时修改
- 可以在不重置计时器的情况下修改组参数
- 支持单独修改某个参数（offsetX, offsetY, scaleX, scaleY, rotation）
- 支持修改文本内容而保持其他设置不变

## 📋 命令列表

| 命令 | 功能 |
|------|------|
| `/title_group` | 创建或添加文本组 |
| `/title_group_transform` | 修改组的变换参数 |
| `/title_group_modify` | 修改组的文本内容 |
| `/title_group_clear` | 清除指定组 |
| `/title_group_clear_all` | 清除所有组 |

## 🔧 技术亮点

1. **智能更新机制** - 使用特殊标记(-1,-1,-1)区分更新和创建操作
2. **性能优化** - 延迟删除策略避免ConcurrentModificationException
3. **防闪烁设计** - Alpha通道阈值过滤（最小值4）防止极淡文本闪烁
4. **客户端缓存** - 维护组状态以实现高效的参数更新

## 📖 使用示例

### 基础用法
```mcfunction
# 创建一个永久显示的金色标题
/title_group @s header {"text":"Welcome!","color":"gold"} 0 -80 2.0 2.0 10 -1 0
```

### 旋转效果
```mcfunction
# 创建一个旋转45度的文本
/title_group @s rotated {"text":"Spinning","color":"aqua"} 0 0 1.5 1.5 10 60 20 45
```

### 动态修改
```mcfunction
# 只修改X轴偏移
/title_group_transform @s header offsetX 100

# 只修改旋转角度
/title_group_transform @s header rotation 90
```

### 多层文本
```mcfunction
# 背景层
/title_group @s bg {"text":"Background","color":"gray"} 0 0 3.0 3.0 10 -1 0

# 前景层
/title_group @s fg {"text":"Foreground","color":"white"} 0 0 2.0 2.0 10 -1 0
```

## 🐛 已知问题

目前没有已知的严重bug。如果遇到问题，请在GitHub Issues中报告。

## 📝 未来计划

- [ ] 支持更多文本样式（阴影、描边等）
- [ ] 添加预设模板系统
- [ ] 支持更复杂的动画效果
- [ ] 添加配置文件支持
- [ ] 优化大规模文本渲染性能

## 🙏 致谢

感谢所有测试者和反馈者！

## 📥 下载

- **GitHub Releases**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/releases
- **源代码**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1

## 📄 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。

---

**发布日期**: 2026年4月29日  
**Minecraft版本**: 1.21.1  
**Fabric版本**: 0.116.11+1.21.1  
**Java要求**: Java 21+
