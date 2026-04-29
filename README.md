# Better Title Command

一个Minecraft 1.21.1 Fabric模组，提供增强的title命令功能，支持文本组管理、变换效果和永久显示。

## 功能特性

- ✅ **多文本组支持** - 同时显示和管理多个文本组
- ✅ **自定义变换** - 支持位置偏移、缩放和旋转
- ✅ **时间控制** - 可配置淡入、显示、淡出时间
- ✅ **永久显示** - 文本可以永久显示直到手动清除
- ✅ **运行时修改** - 可在运行时修改组的参数而不影响其他设置
- ✅ **独立计时器** - 每个组有独立的显示时间和动画

## 命令列表

### 1. `/title_group` - 创建或添加文本组

```mcfunction
/title_group <targets> <groupId> <text> [offsetX] [offsetY] [scaleX] [scaleY] [fadeIn] [stay] [fadeOut] [rotation]
```

**参数说明：**
- `targets`: 目标玩家（如 @a, @s, @p）
- `groupId`: 组的唯一标识符（字符串）
- `text`: 要显示的文本组件
- `offsetX/offsetY`: X/Y轴偏移量（默认: 0）
- `scaleX/scaleY`: X/Y轴缩放比例（默认: 1.0）
- `fadeIn`: 淡入时间（ticks，默认: 10）
- `stay`: 显示时间（ticks，-1表示永久，默认: -1）
- `fadeOut`: 淡出时间（ticks，默认: 20）
- `rotation`: 旋转角度（度，默认: 0）

**示例：**
```mcfunction
# 创建一个永久显示的金色标题
/title_group @s header {"text":"Welcome!","color":"gold"} 0 -80 2.0 2.0 10 -1 0

# 创建一个带旋转的文本
/title_group @s rotated {"text":"Spinning","color":"aqua"} 0 0 1.5 1.5 10 60 20 45
```

### 2. `/title_group_transform` - 修改组的变换参数

**修改所有参数：**
```mcfunction
/title_group_transform <targets> <groupId> <offsetX> <offsetY> <scaleX> <scaleY> [rotation]
```

**修改单个参数：**
```mcfunction
/title_group_transform <targets> <groupId> offsetX <value>
/title_group_transform <targets> <groupId> offsetY <value>
/title_group_transform <targets> <groupId> scaleX <value>
/title_group_transform <targets> <groupId> scaleY <value>
/title_group_transform <targets> <groupId> rotation <value>
```

**示例：**
```mcfunction
# 只修改X轴偏移
/title_group_transform @s header offsetX 100

# 只修改旋转角度
/title_group_transform @s header rotation 90

# 修改所有参数
/title_group_transform @s header 50 -30 2.5 2.5 45
```

### 3. `/title_group_modify` - 修改组的文本内容

```mcfunction
/title_group_modify <targets> <groupId> <text> [offsetX] [offsetY] [scaleX] [scaleY] [rotation]
```

**注意：** 此命令保留原有的时间参数（fadeIn, stay, fadeOut）

**示例：**
```mcfunction
# 修改文本但保持原有变换和时间设置
/title_group_modify @s header {"text":"New Text","color":"red"}
```

### 4. `/title_group_clear` - 清除指定组

```mcfunction
/title_group_clear <targets> <groupId>
```

**示例：**
```mcfunction
/title_group_clear @s header
```

### 5. `/title_group_clear_all` - 清除所有组

```mcfunction
/title_group_clear_all <targets>
```

**示例：**
```mcfunction
/title_group_clear_all @s
```

## 使用技巧

### 1. 永久显示文本
设置 `stay=-1` 让文本永久显示：
```mcfunction
/title_group @s permanent {"text":"Always Visible","color":"green"} 0 0 2.0 2.0 10 -1 0
```

### 2. 多层文本效果
使用不同的groupId创建多层文本：
```mcfunction
/title_group @s bg {"text":"Background","color":"gray"} 0 0 3.0 3.0 10 -1 0
/title_group @s fg {"text":"Foreground","color":"white"} 0 0 2.0 2.0 10 -1 0
```

### 3. 动态更新
在不改变时间的情况下更新文本：
```mcfunction
# 先创建
/title_group @s counter {"text":"Count: 1","color":"gold"} 0 0 2.0 2.0 10 -1 0

# 然后修改文本（保持永久显示）
/title_group_modify @s counter {"text":"Count: 2","color":"gold"}
```

### 4. 动画效果
通过快速连续修改参数实现简单动画：
```mcfunction
# 创建初始文本
/title_group @s anim {"text":"Moving","color":"aqua"} 0 0 2.0 2.0 10 -1 0

# 在命令方块中循环执行
/title_group_transform @s anim offsetX 10
/title_group_transform @s anim offsetX 20
/title_group_transform @s anim offsetX 30
```

## 技术细节

### 时间参数
- 所有时间以ticks为单位（1秒 = 20 ticks）
- `fadeIn=0`: 立即显示，无淡入效果
- `stay=-1`: 永久显示，不会自动消失
- `fadeOut=0`: 立即消失，无淡出效果

### 变换参数
- 偏移量以像素为单位
- 缩放比例为倍数（1.0 = 原始大小）
- 旋转角度以度为单位（顺时针）

### 更新机制
- 使用特殊标记 `(-1, -1, -1)` 来区分更新操作和创建操作
- 更新操作不会重置计时器或时间参数
- 客户端智能检测单参数vs多参数更新

## 安装要求

- Minecraft 1.21.1
- Fabric Loader
- Fabric API

## 开发信息

本项目使用Fabric MDK模板开发。

**源代码：** [GitHub Repository](https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1)

## 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。

## 版本历史

### v1.0.0 (首次发布)
- ✅ 基础文本组功能
- ✅ 变换效果（偏移、缩放、旋转）
- ✅ 时间控制（淡入、显示、淡出）
- ✅ 永久显示模式
- ✅ 运行时参数修改
- ✅ 子命令支持单独参数修改
