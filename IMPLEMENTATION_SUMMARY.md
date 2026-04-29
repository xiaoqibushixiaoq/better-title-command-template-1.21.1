# Better Title Command - 实现总结

## 项目概述
这是一个 Minecraft 1.21.1 Fabric 模组，为 title 和 actionbar 命令添加了文本变换功能（偏移和缩放）。

## 已实现的功能

### 1. 网络通信系统
- ✅ **CustomPacketPayload** - 使用 Minecraft 1.21.1 的网络包API
- ✅ **TransformNetworkHandler** - 服务器端网络包发送器
  - `TitleTransformPayload` - Title变换数据包
  - `ActionbarTransformPayload` - Actionbar变换数据包
- ✅ **TransformClientHandler** - 客户端网络包接收器

### 2. 状态管理
- ✅ **TransformState** - 客户端变换状态管理器
  - 存储待应用的title变换参数
  - 存储待应用的actionbar变换参数
  - 线程安全的参数传递

### 3. 命令系统
- ✅ **EnhancedTitleCommand** - 增强的title命令
  - `/enhanced_title title <文本> [offsetX] [offsetY] [scaleX] [scaleY]`
  - `/enhanced_title actionbar <文本> [offsetX] [offsetY] [scaleX] [scaleY]`
  - 支持可选的变换参数（默认为无变换）
  - 先发送变换参数，再发送文本内容

### 4. 客户端渲染Mixin
- ✅ **TitlePacketMixin** - 拦截title渲染并应用变换
  - 注入到 `Gui.renderTitle` 方法
  - 应用偏移和缩放到title文本
- ✅ **ActionbarPacketMixin** - 拦截文本渲染
  - 注入到 `Font.drawInBatch` 方法
  - 可以应用变换到actionbar文本

### 5. 技术细节

#### 网络包格式
```java
public record TitleTransformPayload(
    float offsetX,  // X轴偏移
    float offsetY,  // Y轴偏移  
    float scaleX,   // X轴缩放
    float scaleY    // Y轴缩放
) implements CustomPacketPayload
```

#### 数据传输流程
1. 玩家执行命令
2. 服务器发送变换参数包（TransformNetworkHandler）
3. 客户端接收并存储参数（TransformClientHandler → TransformState）
4. 下一个title/actionbar渲染时应用变换（Mixin）
5. 清除已使用的变换参数

## 文件结构

```
src/
├── main/
│   ├── java/com/better/title/command/
│   │   ├── network/
│   │   │   └── TransformNetworkHandler.java      # 服务器端网络包
│   │   ├── command/
│   │   │   └── EnhancedTitleCommand.java         # 命令注册
│   │   └── BetterTitleCommand.java               # 模组主类
│   └── resources/
│       └── better-title-command.mixins.json
└── client/
    ├── java/com/better/title/command/client/
    │   ├── network/
    │   │   └── TransformClientHandler.java       # 客户端网络接收器
    │   ├── mixin/
    │   │   ├── TitlePacketMixin.java             # Title渲染Mixin
    │   │   └── ActionbarPacketMixin.java         # Actionbar渲染Mixin
    │   ├── TransformState.java                   # 状态管理
    │   └── BetterTitleCommandClient.java         # 客户端初始化
    └── resources/
        └── better-title-command.client.mixins.json
```

## 使用方法

### 基础用法
```mcfunction
# 显示普通title（无变换）
/enhanced_title title "Hello World"

# 显示带偏移的title
/enhanced_title title "Hello World" 10.0 5.0 1.0 1.0

# 显示带缩放的title
/enhanced_title title "Hello World" 0.0 0.0 1.5 1.5

# 显示带完整变换的title
/enhanced_title title "Hello World" 10.0 5.0 1.5 1.5

# Actionbar同理
/enhanced_title actionbar "Score: 100" 0.0 -10.0 1.2 1.2
```

### 文本组件支持
支持所有Minecraft文本组件格式：
- 普通文本：`"Hello"`
- JSON格式：`{"text":"Hello","color":"red"}`
- 翻译文本：`{"translate":"menu.title"}`
- 复合文本：`{"extra":[{"text":"Hello "},{"text":"World","color":"blue"}]}`

## 技术要点

### CustomPacketPayload API
Minecraft 1.21.1 使用新的网络包系统：
```java
// 正确的导入路径
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;

// 定义Payload
public record MyPayload(float value) implements CustomPacketPayload {
    public static final Type<MyPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("modid", "packet_name")
    );
    
    public static final StreamCodec<FriendlyByteBuf, MyPayload> CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeFloat(payload.value()),
        buf -> new MyPayload(buf.readFloat())
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
```

### Mixin注入点
- **Title渲染**: `Gui.renderTitle(GuiGraphics, int, int)`
- **文本渲染**: `Font.drawInBatch(Component, float, float, int, boolean, Matrix4f, ...)`

### 矩阵变换
```java
// 保存当前状态
guiGraphics.pose().pushPose();

// 应用偏移
guiGraphics.pose().translate(offsetX, offsetY, 0);

// 应用缩放
guiGraphics.pose().scale(scaleX, scaleY, 1.0f);

// 渲染完成后恢复
guiGraphics.pose().popPose();
```

## 构建和测试

### 构建命令
```bash
./gradlew clean build
```

### 生成的JAR文件
位置：`build/libs/better-title-command-1.0.0-1.21.1.jar`

### 安装
1. 确保已安装 Fabric Loader 0.19.2+ 和 Fabric API 0.116.11+
2. 将JAR文件放入 `.minecraft/mods` 文件夹
3. 启动游戏

## 已知限制

1. **Actionbar变换** - 由于actionbar的渲染方式特殊，变换效果可能不如title明显
2. **时序问题** - 变换参数必须在title/actionbar显示前发送，否则会被忽略
3. **多次调用** - 如果快速连续发送多个title，只有最后一个会应用变换

## 未来改进方向

1. 添加更多的变换选项（旋转、透明度等）
2. 支持动画效果（渐变、弹跳等）
3. 添加配置文件自定义默认变换参数
4. 支持批量发送多个title形成动画序列
5. 改进actionbar的变换实现

## 依赖项

- Minecraft 1.21.1
- Fabric Loader >= 0.19.2
- Fabric API >= 0.116.11
- Java >= 21

## 许可证

CC0-1.0
