# Better Title Command - 测试指南

## ✅ 构建状态
- **编译**: 成功
- **运行**: 成功
- **模组加载**: 成功

## 🎮 使用方法

### 命令格式
```mcfunction
/enhanced_title <targets> <text> [offsetX] [offsetY] [scaleX] [scaleY]
```

**参数说明：**
- `targets`: 目标玩家选择器（必需），如 @p、@a、@r、@s 或玩家名称
- `text`: JSON格式的文本组件（必需）
- `offsetX`: X轴偏移，单位像素，默认0
- `offsetY`: Y轴偏移，单位像素，默认0
- `scaleX`: X轴缩放倍数，默认1.0
- `scaleY`: Y轴缩放倍数，默认1.0

### 玩家选择器
- `@p` - 最近的玩家
- `@a` - 所有玩家
- `@r` - 随机玩家
- `@s` - 执行命令的玩家自己
- `<player_name>` - 指定玩家名称

## 📝 示例命令

### 给自己显示Title
```mcfunction
/enhanced_title @s {"text":"Hello World","color":"gold"}
```

### 给所有玩家显示Title
```mcfunction
/enhanced_title @a {"text":"Server Event Starting!","color":"red","bold":true}
```

### 给最近的玩家显示带偏移的Title
```mcfunction
/enhanced_title @p {"text":"Shifted Title","color":"blue"} 50 30
```

### 放大显示的Title
```mcfunction
/enhanced_title @s {"text":"BIG TITLE","color":"red","bold":true} 0 0 3.0 3.0
```

### 模拟ActionBar（底部显示）
```mcfunction
/enhanced_title @s {"text":"Action Bar Text","color":"green","italic":true} 0 61 1.0 1.0
```
> 提示：Y偏移61像素可以让文本显示在屏幕底部附近，模拟原版actionbar的位置

### 给指定玩家发送组合效果
```mcfunction
/enhanced_title Steve {"text":"Custom Position","color":"aqua","underlined":true} -100 -50 2.0 2.0
```

### 给随机玩家显示消息
```mcfunction
/enhanced_title @r {"text":"You are chosen!","color":"yellow"} 0 0 2.0 2.0
```

## 💡 使用技巧

### 位置参考
- **居中**: offsetX=0, offsetY=0
- **顶部**: offsetY=-80
- **底部（ActionBar位置）**: offsetY=61
- **左侧**: offsetX=-150
- **右侧**: offsetX=150

### 缩放建议
- **正常大小**: scaleX=1.0, scaleY=1.0
- **小字**: scaleX=0.5, scaleY=0.5
- **大字**: scaleX=2.0, scaleY=2.0
- **超大**: scaleX=3.0, scaleY=3.0

### 文本组件格式
支持Minecraft所有文本组件特性：
```json
{
  "text": "文本内容",
  "color": "颜色名称或#RGB",
  "bold": true/false,
  "italic": true/false,
  "underlined": true/false,
  "strikethrough": true/false,
  "obfuscated": true/false,
  "font": "字体名称"
}
```

## 🔧 技术实现

### 核心组件
1. **CustomTitleRenderer** - 自定义渲染器，支持矩阵变换
2. **TransformNetworkHandler** - 网络包处理，发送JSON序列化的Component
3. **CustomTitleRenderMixin** - Mixin注入到Gui.render方法
4. **EnhancedTitleCommand** - 命令注册和执行

### 工作流程
1. 玩家执行命令
2. 服务器将Component序列化为JSON字符串
3. 通过自定义网络包发送到客户端
4. 客户端解析JSON为Component
5. CustomTitleRenderer在HUD上渲染并应用变换

## ⚠️ 注意事项

- 文本必须使用JSON格式（Minecraft文本组件格式）
- 缩放值建议范围：0.5 - 5.0
- 偏移值单位为像素
- 淡入/停留/淡出时间固定为：10/60/20 ticks
- ActionBar功能已移除，可通过调整offsetY=61来模拟

## 🐛 已知问题

无

## 📊 性能

- 使用矩阵变换，性能良好
- 只在需要时渲染
- 自动清理过期文本
