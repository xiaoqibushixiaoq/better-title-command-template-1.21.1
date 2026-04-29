# 文本组功能使用指南

## 🎯 功能概述

文本组系统允许你：
1. **同时显示多段文本** - 每段文本有自己的位置、缩放参数
2. **分组管理** - 将相关文本组织成组
3. **组级别控制** - 对整个组应用统一的偏移和缩放
4. **独立清除** - 可以清除特定组或所有组的文本

## 📋 命令列表

### 1. 基础命令（向后兼容）
```mcfunction
/enhanced_title <targets> <text> [offsetX] [offsetY] [scaleX] [scaleY]
```

**示例**:
```mcfunction
# 给自己显示标题
/enhanced_title @s {"text":"Hello World","color":"gold"}

# 给所有玩家显示放大标题
/enhanced_title @a {"text":"Event!","color":"red","bold":true} 0 -50 2.0 2.0
```

### 2. 文本组命令

#### 添加文本到组
```mcfunction
/title_group <targets> <groupId> <text> [offsetX] [offsetY] [scaleX] [scaleY]
```

**参数**:
- `targets`: 目标玩家（@p, @a, @r, @s, 或玩家名）
- `groupId`: 组ID（字符串，用于标识和管理组）
- `text`: JSON格式的文本组件
- `offsetX/Y`: 相对于屏幕中心的偏移（像素）
- `scaleX/Y`: 缩放倍数

**示例**:
```mcfunction
# 添加标题到"header"组
/title_group @s header {"text":"Server Title","color":"gold","bold":true} 0 -80 2.5 2.5

# 添加副标题到同一组
/title_group @s header {"text":"Welcome!","color":"aqua"} 0 -40 1.5 1.5

# 添加底部信息到"footer"组
/title_group @s footer {"text":"Press F to pay respects","color":"gray","italic":true} 0 61 1.0 1.0
```

#### 设置组级别变换
```mcfunction
/title_group_transform <targets> <groupId> <groupOffsetX> <groupOffsetY> <groupScaleX> <groupScaleY>
```

**功能**: 对整个组应用额外的偏移和缩放（会叠加到组内每个文本上）

**示例**:
```mcfunction
# 将"header"组整体向上移动20像素并放大1.2倍
/title_group_transform @s header 0 -20 1.2 1.2
```

#### 清除指定组
```mcfunction
/title_group_clear <targets> <groupId>
```

**示例**:
```mcfunction
# 清除"header"组的所有文本
/title_group_clear @s header

# 清除所有玩家的"footer"组
/title_group_clear @a footer
```

#### 清除所有组
```mcfunction
/title_group_clear_all <targets>
```

**示例**:
```mcfunction
# 清除自己的所有文本组
/title_group_clear_all @s

# 清除所有玩家的所有文本组
/title_group_clear_all @a
```

## 💡 使用场景示例

### 场景1: 创建多层UI界面
```mcfunction
# 第一层：背景标题
/title_group @s background {"text":"════════════════════","color":"dark_gray"} 0 -100 3.0 1.0

# 第二层：主标题
/title_group @s main {"text":"BOSS BATTLE","color":"red","bold":true} 0 -80 3.0 3.0

# 第三层：副标题
/title_group @s main {"text":"Defeat the Dragon!","color":"gold"} 0 -40 1.5 1.5

# 第四层：提示文字
/title_group @s hint {"text":"You have 5 minutes","color":"yellow","italic":true} 0 0 1.2 1.2
```

### 场景2: 动态更新组内容
```mcfunction
# 先显示初始文本
/title_group @s score {"text":"Score: 0","color":"green"} -150 -50 1.5 1.5

# ...游戏进行中...

# 更新分数（新文本会替换旧文本）
/title_group @s score {"text":"Score: 100","color":"green"} -150 -50 1.5 1.5

# 清除旧的提示
/title_group_clear @s hint

# 添加新提示
/title_group @s hint {"text":"Bonus Round!","color":"light_purple","bold":true} 0 0 2.0 2.0
```

### 场景3: 组合效果
```mcfunction
# 创建顶部信息栏
/title_group @s topbar {"text":"⚔ PvP Arena ⚔","color":"dark_red","bold":true} 0 -90 2.0 2.0
/title_group @s topbar {"text":"Players: 8/16","color":"gray"} 0 -70 1.0 1.0

# 创建底部状态栏
/title_group @s statusbar {"text":"Health: ❤❤❤❤❤","color":"red"} -100 70 1.0 1.0
/title_group @s statusbar {"text":"Mana: ✦✦✦","color":"blue"} 100 70 1.0 1.0

# 当需要隐藏时，只需清除对应组
/title_group_clear @s topbar
/title_group_clear @s statusbar
```

## 🔧 技术细节

### 变换计算方式
最终显示位置 = 组级别偏移 + 片段级别偏移  
最终显示缩放 = 组级别缩放 × 片段级别缩放

**示例**:
```
组级别: offsetX=10, offsetY=20, scaleX=1.5, scaleY=1.5
片段级别: offsetX=5, offsetY=-5, scaleX=2.0, scaleY=2.0

最终结果:
- 位置: (10+5, 20-5) = (15, 15)
- 缩放: (1.5×2.0, 1.5×2.0) = (3.0, 3.0)
```

### 组ID命名建议
- 使用有意义的名称：`header`, `footer`, `score`, `timer`
- 避免特殊字符
- 保持简短但清晰

### 性能考虑
- 每个玩家可以有多个组
- 每组可以有多个文本片段
- 建议在不需要时及时清除组以释放资源

## 🎨 文本组件格式

支持完整的Minecraft文本组件格式：

```json
{
  "text": "Hello",
  "color": "gold",
  "bold": true,
  "italic": false,
  "underlined": false,
  "strikethrough": false,
  "obfuscated": false
}
```

**颜色选项**: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white

## ⚠️ 注意事项

1. **组ID区分大小写** - "Header" 和 "header" 是不同的组
2. **添加文本到已存在的组** - 会替换该组的内容
3. **清除不存在的组** - 不会产生错误，只是无操作
4. **淡入淡出时间** - 目前固定为 fadeIn=10, stay=60, fadeOut=20 tick

## 🚀 开发者API

其他mod可以通过API直接使用：

```java
// 创建文本组
TextGroup group = new TextGroup("myGroup");

// 添加多个文本片段
group.addSegment(new TextSegment(
    Component.literal("Line 1").withStyle(ChatFormatting.GOLD),
    0, -50, 2.0f, 2.0f
));
group.addSegment(new TextSegment(
    Component.literal("Line 2").withStyle(ChatFormatting.AQUA),
    0, 0, 1.5f, 1.5f
));

// 设置组级别变换
group.setGroupOffset(10, 20);
group.setGroupScale(1.2f, 1.2f);

// 发送给玩家
Map<String, TextGroup> groups = new HashMap<>();
groups.put("myGroup", group);
TransformNetworkHandler.sendTitle(player, groups, 10, 60, 20);

// 清除组
CustomTitleRenderer renderer = TransformClientHandler.getRenderer();
renderer.clearGroup("myGroup");
```

---

**版本**: 1.0.0  
**Minecraft**: 1.21.1  
**最后更新**: 2026-04-29
