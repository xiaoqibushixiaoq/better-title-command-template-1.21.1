# Better Title Command v1.0.3 发布说明

## 📅 发布日期
2026年4月30日

## 🎯 版本概览

**Better Title Command v1.0.3** 是一个重要的bug修复版本，彻底解决了单参数更新时影响其他参数的问题。通过在网络包中显式传递参数名，确保了参数更新的精确性和可靠性。

---

## ✨ 新特性

### 1. 可靠的单参数更新机制 🎯
- **使用显式参数名标记而非特殊值**
- 在TextGroup中添加`updateParamName`和`updateParamValue`字段
- 网络包序列化时包含参数名信息
- 客户端直接调用`updateSingleParam`方法
- **100%可靠，不会误判**

**技术实现：**
```java
// 服务器端
TextGroup group = new TextGroup(groupId);
group.setUpdateParam("offsetX", 100.0f);

// 网络传输
buf.writeUtf(group.getUpdateParamName());
buf.writeFloat(group.getUpdateParamValue());

// 客户端
if (updateParamName != null && !updateParamName.isEmpty()) {
    titleRenderer.updateSingleParam(groupId, updateParamName, updateParamValue);
}
```

---

## 🔧 Bug修复

### 修复：单参数更新重置其他参数 🐛

**问题描述：**
- 执行`/title_group_transform @s group offsetX 100`后，再执行`/title_group_transform @s group rotation 0`
- 发现rotation被设置为0的同时，offsetX也被重置了
- 所有子命令（offsetX、offsetY、scaleX、scaleY、rotation）都会相互影响

**根本原因：**
- 之前使用Float.MAX_VALUE作为"不更新"的标记
- 但TextGroup的setter会接收这些值，导致参数被错误覆盖
- 客户端无法区分哪些参数应该更新，哪些应该保留

**修复方案：**
1. 在TextGroup中添加`updateParamName`和`updateParamValue`字段
2. 服务器端明确设置要更新的参数名
3. 网络包序列化时包含这两个字段
4. 客户端检查`updateParamName`，直接调用对应的更新方法

**测试结果：**
```mcfunction
# 测试1：设置offsetX不影响其他参数
/title_group @s test {"text":"Test","color":"gold"} 50 50 2.0 2.0 45
/title_group_transform @s test offsetX 100
# ✅ 结果：offsetX=100, offsetY=50, scaleX=2.0, scaleY=2.0, rotation=45

# 测试2：设置rotation不影响其他参数
/title_group_transform @s test rotation 90
# ✅ 结果：offsetX=100, offsetY=50, scaleX=2.0, scaleY=2.0, rotation=90

# 测试3：设置为0也不会影响其他参数
/title_group_transform @s test offsetX 0
# ✅ 结果：offsetX=0, offsetY=50, scaleX=2.0, scaleY=2.0, rotation=90
```

---

## 📊 技术改进

### 网络协议扩展

**TitleTransformPayload序列化格式：**
```
对于每个TextGroup：
1. 组ID (String)
2. 组级别变换 (5个float)
   - groupOffsetX
   - groupOffsetY
   - groupScaleX
   - groupScaleY
   - groupRotation
3. 单参数更新标记 (新增)
   - updateParamName (String, 空字符串表示无)
   - updateParamValue (float)
4. 片段数量 (int)
5. 片段数据...
6. 时间参数 (3个int)
```

### 代码结构优化

**TextGroup新增字段：**
```java
private String updateParamName = null;
private float updateParamValue = 0.0f;

public void setUpdateParam(String paramName, float value);
public String getUpdateParamName();
public float getUpdateParamValue();
```

**客户端处理逻辑：**
```java
String updateParamName = group.getUpdateParamName();
if (updateParamName != null && !updateParamName.isEmpty()) {
    // 单参数更新
    titleRenderer.updateSingleParam(groupId, updateParamName, group.getUpdateParamValue());
} else {
    // 多参数更新或创建新组
    // ...
}
```

---

## 📖 使用示例

### 单独修改各个参数

```mcfunction
# 创建一个测试组
/title_group @s ui {"text":"UI Element","color":"aqua"} 0 0 1.5 1.5 0

# 只修改X偏移（不影响Y偏移、缩放、旋转）
/title_group_transform @s ui offsetX 100

# 只修改Y偏移（不影响X偏移、缩放、旋转）
/title_group_transform @s ui offsetY -50

# 只修改X缩放（不影响偏移、Y缩放、旋转）
/title_group_transform @s ui scaleX 2.0

# 只修改Y缩放（不影响偏移、X缩放、旋转）
/title_group_transform @s ui scaleY 2.0

# 只修改旋转（不影响偏移、缩放）
/title_group_transform @s ui rotation 45

# 可以设置为0，不会影响其他参数
/title_group_transform @s ui offsetX 0
```

### 组合使用

```mcfunction
# 创建BOSS战UI
/title_group @s boss_name {"text":"⚔ DRAGON KING ⚔","color":"dark_red","bold":true} 0 -90 3.0 3.0 0
/title_group @s boss_health {"text":"Health: ██████████","color":"red"} 0 -70 1.5 1.5 0
/title_group @s timer {"text":"Time: 5:00","color":"yellow"} 150 -90 1.2 1.2 0

# 战斗中动态调整位置
/title_group_transform @s boss_name offsetY -80
/title_group_transform @s boss_health offsetY -60

# 更新时间（保持位置不变）
/title_group_modify @s timer {"text":"Time: 3:45","color":"yellow"} 150 -90 1.2 1.2 0
```

---

## 🔄 从 v1.0.2 升级

### 升级建议
- ✅ **强烈建议所有用户升级到此版本**
- ✅ 彻底修复单参数更新bug
- ✅ 向后兼容，无需修改现有命令
- ✅ 网络协议扩展，但不破坏兼容性

### 注意事项
- 新版本使用了扩展的网络协议
- 确保客户端和服务器都使用v1.0.3或更高版本
- 旧版本的jar文件可能无法正确处理单参数更新

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
- 报告单参数更新问题的用户
- 提供详细测试用例的用户
- 所有参与测试的玩家

---

## 📥 下载

- **GitHub Release**: https://github.com/xiaoqibushixiaoq/better-title-command-template-1.21.1/releases/tag/v1.0.3
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
**版本**: v1.0.3  
**构建号**: f2b03e7
