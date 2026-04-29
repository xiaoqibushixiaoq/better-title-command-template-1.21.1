# 多组同时显示功能 - 测试指南

## ✅ 修复内容

之前的问题：每次发送新的文本组会**替换**掉之前的所有组  
现在的行为：每次发送新的文本组会**累加**到现有组中，实现多组同时显示

## 🧪 测试步骤

### 测试1: 基础累加功能

在游戏中依次执行以下命令，应该看到三个文本**同时**显示：

```mcfunction
# 第一个文本 - 顶部
/title_group @s header {"text":"BOSS BATTLE","color":"red","bold":true} 0 -80 3.0 3.0

# 第二个文本 - 中间（应该与第一个同时显示）
/title_group @s subtitle {"text":"Defeat the Dragon!","color":"gold"} 0 -40 1.5 1.5

# 第三个文本 - 底部（三个文本应该同时可见）
/title_group @s footer {"text":"Press F to pay respects","color":"gray","italic":true} 0 61 1.0 1.0
```

**预期结果**: 屏幕上同时显示三个文本，分别在不同位置

### 测试2: 更新已有组

```mcfunction
# 先创建组
/title_group @s score {"text":"Score: 0","color":"green"} -150 -50 1.5 1.5

# 等待几秒后更新（应该替换旧的score组，但保留其他组）
/title_group @s score {"text":"Score: 100","color":"green"} -150 -50 1.5 1.5
```

**预期结果**: score文本更新为新内容，header和footer仍然显示

### 测试3: 清除单个组

```mcfunction
# 假设有三个组在显示
# 清除其中一个
/title_group_clear @s footer
```

**预期结果**: 只有footer消失，header和subtitle仍然显示

### 测试4: 清除所有组

```mcfunction
/title_group_clear_all @s
```

**预期结果**: 所有文本立即消失

### 测试5: 独立计时器

```mcfunction
# 创建两个不同时间的组
/title_group @s quick {"text":"Quick Message","color":"yellow"} 0 -60 2.0 2.0
/title_group @s long {"text":"Long Message","color":"cyan"} 0 0 1.5 1.5
```

**预期结果**: 
- 两个文本同时显示
- 每个组有自己的淡入淡出时间
- 一个组消失后，另一个组继续显示

## 🎮 实际使用场景

### 场景1: Boss战UI

```mcfunction
# 创建完整的Boss战UI
/title_group @s boss_name {"text":"⚔ DRAGON KING ⚔","color":"dark_red","bold":true} 0 -90 3.0 3.0
/title_group @s boss_health {"text":"Health: ██████████","color":"red"} 0 -70 1.5 1.5
/title_group @s timer {"text":"Time: 5:00","color":"yellow"} 150 -90 1.2 1.2
/title_group @s hint {"text":"Attack when glowing!","color":"aqua","italic":true} 0 40 1.0 1.0

# ...战斗进行中...

# 更新血量
/title_group @s boss_health {"text":"Health: █████░░░░░","color":"red"} 0 -70 1.5 1.5

# 更新时间
/title_group @s timer {"text":"Time: 3:45","color":"yellow"} 150 -90 1.2 1.2

# Boss击败后清除所有
/title_group_clear_all @s
```

### 场景2: PvP竞技场

```mcfunction
# 顶部信息栏
/title_group @s arena_title {"text":"⚔ PvP Arena ⚔","color":"dark_red","bold":true} 0 -90 2.0 2.0
/title_group @s player_count {"text":"Players: 8/16","color":"gray"} 0 -70 1.0 1.0

# 左下角状态
/title_group @s health {"text":"❤❤❤❤❤","color":"red"} -150 70 1.2 1.2
/title_group @s mana {"text":"✦✦✦","color":"blue"} -150 85 1.0 1.0

# 右下角状态
/title_group @s kills {"text":"Kills: 3","color":"gold"} 150 70 1.0 1.0
/title_group @s deaths {"text":"Deaths: 1","color":"dark_gray"} 150 85 1.0 1.0

# 玩家退出时更新
/title_group @s player_count {"text":"Players: 7/16","color":"gray"} 0 -70 1.0 1.0

# 游戏结束时清除
/title_group_clear_all @s
```

### 场景3: 任务提示系统

```mcfunction
# 主线任务
/title_group @s main_quest {"text":"Main Quest: Find the Artifact","color":"gold","bold":true} 0 -80 1.8 1.8

# 支线任务
/title_group @s side_quest {"text":"Side Quest: Collect 10 Herbs","color":"green"} 0 -60 1.2 1.2

# 完成主线后
/title_group_clear @s main_quest
/title_group @s main_quest {"text":"Main Quest: Return to Village","color":"gold","bold":true} 0 -80 1.8 1.8

# 完成支线后
/title_group_clear @s side_quest
```

## 🔍 调试技巧

### 检查当前显示的组

目前还没有查看命令，但你可以通过观察屏幕来确认：
- 如果多个文本同时显示 → ✅ 累加功能正常
- 如果新文本替换了旧文本 → ❌ 仍有问题

### 测试组ID区分

```mcfunction
# 注意：组ID区分大小写
/title_group @s Test {"text":"Uppercase","color":"red"} 0 -50 1.5 1.5
/title_group @s test {"text":"Lowercase","color":"blue"} 0 0 1.5 1.5
```

**预期结果**: 两个文本都显示（因为是不同的组ID）

## ⚠️ 已知限制

1. **淡入淡出时间固定** - 目前所有组使用相同的fadeIn=10, stay=60, fadeOut=20
2. **无持久化** - 重新登录游戏后所有组都会消失
3. **无组列表查询** - 无法查看当前有哪些活跃的组

## 📝 命令速查

| 命令 | 功能 | 示例 |
|------|------|------|
| `/title_group` | 添加/更新组 | `/title_group @s header {"text":"Title"} 0 -50 2.0 2.0` |
| `/title_group_clear` | 清除指定组 | `/title_group_clear @s header` |
| `/title_group_clear_all` | 清除所有组 | `/title_group_clear_all @s` |
| `/enhanced_title` | 基础命令（向后兼容） | `/enhanced_title @s {"text":"Hello"} 0 0 1.0 1.0` |

---

**测试日期**: 2026-04-29  
**Minecraft版本**: 1.21.1  
**模组版本**: 1.0.0
