# Better Title Command - 使用指南

## 🎮 安装说明

1. 确保已安装：
   - Minecraft 1.21.1
   - Fabric Loader 0.19.2+
   - Fabric API 0.116.11+

2. 将 `better-title-command-1.0.0-1.21.1.jar` 放入 `.minecraft/mods` 文件夹

3. 启动游戏

## 📝 命令用法

### 基础命令格式

```mcfunction
/enhanced_title <类型> <文本> [offsetX] [offsetY] [scaleX] [scaleY]
```

**参数说明：**
- `<类型>`: `title` 或 `actionbar`
- `<文本>`: 要显示的文本（支持JSON文本组件）
- `[offsetX]`: X轴偏移量（可选，默认0.0）
- `[offsetY]`: Y轴偏移量（可选，默认0.0）
- `[scaleX]`: X轴缩放比例（可选，默认1.0）
- `[scaleY]`: Y轴缩放比例（可选，默认1.0）

### 示例

#### Title命令

```mcfunction
# 基础title（无变换）
/enhanced_title title "Hello World"

# 带偏移的title
/enhanced_title title "Hello World" 10.0 5.0 1.0 1.0

# 带缩放的title（放大1.5倍）
/enhanced_title title "Hello World" 0.0 0.0 1.5 1.5

# 完整变换
/enhanced_title title "Hello World" 10.0 5.0 1.5 1.5

# 向下偏移并缩小
/enhanced_title title "Subtitle" 0.0 20.0 0.8 0.8
```

#### Actionbar命令

```mcfunction
# 基础actionbar
/enhanced_title actionbar "Score: 100"

# 向上偏移的actionbar
/enhanced_title actionbar "Combo!" 0.0 -10.0 1.2 1.2

# 带颜色的actionbar
/enhanced_title actionbar {"text":"警告!","color":"red"} 0.0 0.0 1.3 1.3
```

### 文本组件格式

支持所有Minecraft文本组件格式：

#### 1. 普通文本
```mcfunction
/enhanced_title title "简单文本"
```

#### 2. JSON格式
```mcfunction
/enhanced_title title {"text":"彩色文本","color":"red","bold":true}
```

#### 3. 翻译文本
```mcfunction
/enhanced_title title {"translate":"menu.title"}
```

#### 4. 复合文本
```mcfunction
/enhanced_title title {"extra":[{"text":"Hello "},{"text":"World","color":"blue"}]}
```

#### 5. 点击事件
```mcfunction
/enhanced_title title {"text":"点击我","clickEvent":{"action":"run_command","value":"/help"}}
```

## 🔧 技术细节

### 网络通信

模组使用Fabric的网络API在服务器和客户端之间传输变换参数：

1. **服务器端**：发送变换参数包
2. **客户端**：接收并存储参数
3. **渲染时**：应用变换到文本

### Payload类型

- `better-title-command:title_transform` - Title变换数据包
- `better-title-command:actionbar_transform` - Actionbar变换数据包

每个包包含4个float值：
- offsetX (X轴偏移)
- offsetY (Y轴偏移)
- scaleX (X轴缩放)
- scaleY (Y轴缩放)

## ⚠️ 注意事项

1. **时序问题**：变换参数必须在title/actionbar显示前发送
2. **单次有效**：每个变换参数只应用于下一个title/actionbar
3. **默认值**：如果不指定变换参数，默认为无变换（offset=0, scale=1）

## 🐛 故障排除

### 命令不工作？
- 确保你有OP权限或在创造模式
- 检查是否正确安装了Fabric API
- 查看游戏日志是否有错误信息

### 变换效果不明显？
- 尝试更大的数值（如offset=50, scale=2.0）
- 注意负数offset可以让文本向相反方向移动
- scale小于1.0会缩小文本

### 游戏崩溃？
- 检查Minecraft版本是否为1.21.1
- 确保Fabric Loader和Fabric API版本兼容
- 查看crash-reports文件夹中的崩溃报告

## 📦 构建项目

如果你是开发者，想要修改或构建这个项目：

```bash
# 克隆项目
git clone <repository-url>
cd better-title-command-template-1.21.1

# 构建项目
./gradlew build

# 运行开发环境
./gradlew runClient
```

生成的JAR文件位于：`build/libs/better-title-command-1.0.0-1.21.1.jar`

## 📄 许可证

本项目采用 CC0-1.0 许可证。

## 🤝 贡献

欢迎提交Issue和Pull Request！

---

**祝你游戏愉快！** 🎉
