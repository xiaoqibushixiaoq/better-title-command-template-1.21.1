# Better Title Command

一个 Minecraft 1.21.1 Fabric 模组，为文本组件添加 X/Y 偏移和 X/Y 缩放功能。

## 功能特性

✨ **变换文本组件**
- X轴偏移 (offsetX)
- Y轴偏移 (offsetY) 
- X轴缩放 (scaleX)
- Y轴缩放 (scaleY)

🎮 **增强命令**
- `/enhanced_title title` - 显示带变换的标题
- `/enhanced_title actionbar` - 显示带变换的动作栏文本

💻 **开发者 API**
- 完整的 Java API
- 工具类简化使用
- 支持序列化和网络传输

## 快速开始

### 安装

1. 确保已安装 Fabric Loader 0.19.2+ 和 Fabric API
2. 将 mod jar 文件放入 `.minecraft/mods` 文件夹
3. 启动游戏

### 使用命令

```mcfunction
# 基础用法 - 支持普通文本、本地化键名、样式等
/enhanced_title title "Hello World"

# 本地化文本
/enhanced_title title {"translate":"menu.title"}

# 带样式的文本
/enhanced_title title {"text":"警告","color":"red","bold":true}

# 带偏移和缩放
/enhanced_title title "Hello World" 10.0 5.0 1.5 1.5
# 参数: 文本组件, offsetX, offsetY, scaleX, scaleY

# Actionbar
/enhanced_title actionbar {"text":"连击!","color":"yellow"} 0.0 0.0 1.2 1.2
```

**支持的文本组件类型：**
- ✅ 普通文本字符串
- ✅ 本地化键名（翻译）
- ✅ 带样式的文本（颜色、粗体、斜体等）
- ✅ 复合文本（多个部分组合）
- ✅ 点击事件和悬停提示
- ✅ 分数、选择器、按键绑定等特殊文本

### 代码示例

```java
import com.better.title.command.component.TransformedText;
import com.better.title.command.component.TransformedTexts;
import net.minecraft.text.Text;

// 创建变换文本
Text text = TransformedTexts.of(
    Text.literal("Hello!"),
    10.0f,   // X偏移
    5.0f,    // Y偏移
    1.5f,    // X缩放
    1.5f     // Y缩放
);
```

## 文档

- [详细使用指南](USAGE.md) - 完整的功能说明和示例
- [文本组件示例](TEXT_COMPONENT_EXAMPLES.md) - 各种文本组件格式的详细示例
- [快速参考](QUICK_REFERENCE.md) - 命令语法和常用示例速查

## 项目结构

```
src/
├── main/
│   ├── java/com/better/title/command/
│   │   ├── component/           # 文本组件核心类
│   │   │   ├── TransformedText.java
│   │   │   ├── TransformedTexts.java
│   │   │   └── BetterTitleCommandTextTypes.java
│   │   ├── command/             # 命令注册
│   │   │   └── EnhancedTitleCommand.java
│   │   └── BetterTitleCommand.java
│   └── resources/
└── client/
    ├── java/com/better/title/command/client/
    │   ├── mixin/               # 客户端 Mixin
    │   │   └── TransformedTextRendererMixin.java
    │   └── BetterTitleCommandClient.java
    └── resources/
```

## 构建

```bash
# 构建项目
./gradlew build

# 运行开发环境
./gradlew runClient
```

构建后的 jar 文件位于 `build/libs/` 目录。

## 技术细节

- **Minecraft 版本**: 1.21.1
- **Fabric Loader**: 0.19.2+
- **Fabric API**: 0.116.11+
- **Java 版本**: 21+

## 许可证

本项目采用 CC0-1.0 许可证。详情请查看 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

- 项目主页: [GitHub](https://github.com/yourusername/better-title-command)
- 问题反馈: [Issues](https://github.com/yourusername/better-title-command/issues)
