# Better Title Command v1.0.1 发布说明

## 📅 发布日期
2026年4月29日

## 🎯 版本概览

**Better Title Command v1.0.1** 是一个重要的性能和质量更新，专注于提升时间控制的精度和用户体验。此版本将时间单位从游戏ticks改为毫秒，并实现了基于真实时间的计时系统，为高FPS玩家提供更流畅的动画体验。

---

## ✨ 新特性

### 1. 毫秒级时间控制 ⏱️
- **时间参数现在使用毫秒（ms）而非ticks**
- 提供1毫秒级别的精确控制
- 不再受限于50ms的tick间隔
- 支持任意整数毫秒值

**示例：**
```mcfunction
# 精确控制：0.5秒淡入，3秒显示，1秒淡出
/title_group @s test {"text":"Hello","color":"gold"} 0 0 2.0 2.0 500 3000 1000

# 快速动画：250ms淡入，1.5秒显示，500ms淡出
/title_group @s fast {"text":"Quick","color":"red"} 0 0 1.5 1.5 250 1500 500
```

### 2. 基于真实时间的计时系统 🚀
- 使用 `System.currentTimeMillis()` 进行计时
- **完全不受帧率影响** - 60 FPS 和 200 FPS 体验一致
- 动画更加流畅细腻
- 时间准确性不受游戏lag影响

### 3. 高FPS优化 🎮
- 高帧率玩家获得更平滑的淡入淡出动画
- 每帧都根据真实经过的时间计算alpha值
- 200 FPS下每秒更新200次，动画极其流畅
- 低配置玩家也能保持正确的时间控制

---

## 🔧 改进与修复

### 兼容性修复
- ✅ **降低Fabric Loader最低版本要求** 从 `>=0.19.2` 降至 `>=0.15.0`
- ✅ 兼容更多版本的Fabric Loader（包括0.17.2）
- ✅ 解决了在旧版本启动器上的加载问题

### 性能优化
- ✅ 移除tick到毫秒的转换开销
- ✅ 直接使用系统时间，减少计算步骤
- ✅ 更高效的计时器更新逻辑

### 代码质量
- ✅ 更新了所有文档注释，明确标注毫秒单位
- ✅ 改进了变量命名（fadeInMs, stayMs, fadeOutMs）
- ✅ 增强了代码可读性和可维护性

---

## 📊 技术细节

### 时间单位对比

| 特性 | v1.0.0 (Ticks) | v1.0.1 (Milliseconds) |
|------|----------------|----------------------|
| 最小时间单位 | 50ms (1 tick) | 1ms |
| 时间精度 | 低 | 高 |
| 灵活性 | 只能是50的倍数 | 任意整数 |
| 默认淡入 | 10 ticks = 500ms | 500ms |
| 默认显示 | 60 ticks = 3000ms | 3000ms |
| 默认淡出 | 20 ticks = 1000ms | 1000ms |

### 计时机制对比

**v1.0.0 (Tick-based):**
```java
// 每帧增加1，受帧率影响
timer.timer++;
// 60 FPS: 每秒+60
// 200 FPS: 每秒+200 ❌ 不一致！
```

**v1.0.1 (Real-time):**
```java
// 基于真实时间，不受帧率影响
long elapsed = currentTime - startTime;
float alpha = elapsed / fadeInTime;
// 60 FPS: 精确计算经过的毫秒数 ✅
// 200 FPS: 同样精确 ✅
```

---

## 📖 使用指南

### 基本命令

```mcfunction
# 创建文本组（使用毫秒）
/title_group <targets> <groupId> <text> [offsetX] [offsetY] [scaleX] [scaleY] [fadeIn(ms)] [stay(ms)] [fadeOut(ms)] [rotation]

# 修改变换参数
/title_group_transform <targets> <groupId> offsetX <value>
/title_group_transform <targets> <groupId> rotation <value>

# 修改文本内容
/title_group_modify <targets> <groupId> <text>

# 清除组
/title_group_clear <targets> <groupId>
/title_group_clear_all <targets>
```

### 实用示例

#### 1. 标准标题
```mcfunction
/title_group @s header {"text":"Welcome!","color":"gold","bold":true} 0 -80 2.5 2.5 500 5000 1000
```
- 淡入：0.5秒
- 显示：5秒
- 淡出：1秒

#### 2. 快速提示
```mcfunction
/title_group @s hint {"text":"Press E to interact","color":"aqua"} 0 50 1.5 1.5 200 2000 300
```
- 淡入：0.2秒
- 显示：2秒
- 淡出：0.3秒

#### 3. 永久显示
```mcfunction
/title_group @s status {"text":"Quest Active","color":"green"} 0 -100 1.8 1.8 500 -1 0
```
- stay=-1 表示永久显示

#### 4. 立即显示（无动画）
```mcfunction
/title_group @s instant {"text":"NOW!","color":"red"} 0 0 3.0 3.0 0 1000 0
```
- fadeIn=0, fadeOut=0 表示无动画

#### 5. 多层效果
```mcfunction
# 背景层
/title_group @s bg {"text":"Background","color":"gray"} 0 0 3.0 3.0 800 -1 0

# 前景层
/title_group @s fg {"text":"Foreground","color":"white"} 0 0 2.0 2.0 500 -1 0
```

---

## 🔄 从 v1.0.0 升级

### 迁移指南

如果你之前使用的是ticks单位，只需将数值乘以50即可转换为毫秒：

```
旧值 (ticks) → 新值 (milliseconds)
10 ticks → 500ms
60 ticks → 3000ms
20 ticks → 1000ms
```

**示例：**
```mcfunction
# v1.0.0 (ticks)
/title_group @s test {"text":"Hello"} 0 0 2.0 2.0 10 60 20

# v1.0.1 (milliseconds) - 等效
/title_group @s test {"text":"Hello"} 0 0 2.0 2.0 500 3000 1000
```

### 注意事项
- ✅ 命令格式完全兼容
- ✅ 功能保持不变
- ⚠️ 时间参数单位从ticks变为milliseconds
- 💡 建议使用毫秒以获得更精确的控制

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
- 提出时间精度问题的用户
- 报告Fabric Loader兼容性问题的用户
- 所有参与测试的玩家

---

## 📥 下载

- **GitHub Release**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/releases/tag/v1.0.1
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

**更新日期**: 2026年4月29日  
**版本**: v1.0.1  
**构建号**: a8e81fe
