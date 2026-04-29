package com.better.title.command.command;

import com.better.title.command.component.TextGroup;
import com.better.title.command.component.TextSegment;
import com.better.title.command.network.TransformNetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 增强的title命令 - 支持文本组管理
 */
public class GroupedTitleCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        // 添加文本到组的命令
        dispatcher.register(
            Commands.literal("title_group")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .then(Commands.argument("text", ComponentArgument.textComponent(registryAccess))
                            .executes(context -> executeAddText(context, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 10, 60, 20))
                            .then(Commands.argument("offsetX", FloatArgumentType.floatArg())
                                .then(Commands.argument("offsetY", FloatArgumentType.floatArg())
                                    .then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                                        .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                            .executes(context -> executeAddText(
                                                context,
                                                FloatArgumentType.getFloat(context, "offsetX"),
                                                FloatArgumentType.getFloat(context, "offsetY"),
                                                FloatArgumentType.getFloat(context, "scaleX"),
                                                FloatArgumentType.getFloat(context, "scaleY"),
                                                0.0f, // rotation
                                                10, 60, 20
                                            ))
                                            .then(Commands.argument("fadeIn", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("stay", IntegerArgumentType.integer(-1))
                                                    .then(Commands.argument("fadeOut", IntegerArgumentType.integer(0))
                                                        .executes(context -> executeAddText(
                                                            context,
                                                            FloatArgumentType.getFloat(context, "offsetX"),
                                                            FloatArgumentType.getFloat(context, "offsetY"),
                                                            FloatArgumentType.getFloat(context, "scaleX"),
                                                            FloatArgumentType.getFloat(context, "scaleY"),
                                                            0.0f, // rotation
                                                            IntegerArgumentType.getInteger(context, "fadeIn"),
                                                            IntegerArgumentType.getInteger(context, "stay"),
                                                            IntegerArgumentType.getInteger(context, "fadeOut")
                                                        ))
                                                        .then(Commands.argument("rotation", FloatArgumentType.floatArg())
                                                            .executes(context -> executeAddText(
                                                                context,
                                                                FloatArgumentType.getFloat(context, "offsetX"),
                                                                FloatArgumentType.getFloat(context, "offsetY"),
                                                                FloatArgumentType.getFloat(context, "scaleX"),
                                                                FloatArgumentType.getFloat(context, "scaleY"),
                                                                FloatArgumentType.getFloat(context, "rotation"),
                                                                IntegerArgumentType.getInteger(context, "fadeIn"),
                                                                IntegerArgumentType.getInteger(context, "stay"),
                                                                IntegerArgumentType.getInteger(context, "fadeOut")
                                                            ))
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
        
        // 设置组级别变换的命令
        dispatcher.register(
            Commands.literal("title_group_transform")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        // 设置所有参数的快捷方式
                        .then(Commands.argument("groupOffsetX", FloatArgumentType.floatArg())
                            .then(Commands.argument("groupOffsetY", FloatArgumentType.floatArg())
                                .then(Commands.argument("groupScaleX", FloatArgumentType.floatArg())
                                    .then(Commands.argument("groupScaleY", FloatArgumentType.floatArg())
                                        .executes(context -> executeSetGroupTransform(
                                            context,
                                            FloatArgumentType.getFloat(context, "groupOffsetX"),
                                            FloatArgumentType.getFloat(context, "groupOffsetY"),
                                            FloatArgumentType.getFloat(context, "groupScaleX"),
                                            FloatArgumentType.getFloat(context, "groupScaleY"),
                                            null // 不改变rotation
                                        ))
                                        .then(Commands.argument("groupRotation", FloatArgumentType.floatArg())
                                            .executes(context -> executeSetGroupTransform(
                                                context,
                                                FloatArgumentType.getFloat(context, "groupOffsetX"),
                                                FloatArgumentType.getFloat(context, "groupOffsetY"),
                                                FloatArgumentType.getFloat(context, "groupScaleX"),
                                                FloatArgumentType.getFloat(context, "groupScaleY"),
                                                FloatArgumentType.getFloat(context, "groupRotation")
                                            ))
                                        )
                                    )
                                )
                            )
                        )
                        // 单独设置offsetX
                        .then(Commands.literal("offsetX")
                            .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> executeSetSingleParam(
                                    context,
                                    "offsetX",
                                    FloatArgumentType.getFloat(context, "value")
                                ))
                            )
                        )
                        // 单独设置offsetY
                        .then(Commands.literal("offsetY")
                            .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> executeSetSingleParam(
                                    context,
                                    "offsetY",
                                    FloatArgumentType.getFloat(context, "value")
                                ))
                            )
                        )
                        // 单独设置scaleX
                        .then(Commands.literal("scaleX")
                            .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> executeSetSingleParam(
                                    context,
                                    "scaleX",
                                    FloatArgumentType.getFloat(context, "value")
                                ))
                            )
                        )
                        // 单独设置scaleY
                        .then(Commands.literal("scaleY")
                            .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> executeSetSingleParam(
                                    context,
                                    "scaleY",
                                    FloatArgumentType.getFloat(context, "value")
                                ))
                            )
                        )
                        // 单独设置rotation
                        .then(Commands.literal("rotation")
                            .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> executeSetSingleParam(
                                    context,
                                    "rotation",
                                    FloatArgumentType.getFloat(context, "value")
                                ))
                            )
                        )
                    )
                )
        );
        
        // 清除组的命令
        dispatcher.register(
            Commands.literal("title_group_clear")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .executes(GroupedTitleCommand::executeClearGroup)
                    )
                )
        );
        
        // 清除所有组的命令
        dispatcher.register(
            Commands.literal("title_group_clear_all")
                .then(Commands.argument("targets", EntityArgument.players())
                    .executes(GroupedTitleCommand::executeClearAll)
                )
        );
        
        // 修改组文本内容的命令（保留变换参数和时间参数）
        dispatcher.register(
            Commands.literal("title_group_modify")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .then(Commands.argument("text", ComponentArgument.textComponent(registryAccess))
                            .executes(context -> executeModifyText(
                                context,
                                0.0f, 0.0f, 1.0f, 1.0f, 0.0f
                            ))
                            .then(Commands.argument("offsetX", FloatArgumentType.floatArg())
                                .then(Commands.argument("offsetY", FloatArgumentType.floatArg())
                                    .then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                                        .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                            .executes(context -> executeModifyText(
                                                context,
                                                FloatArgumentType.getFloat(context, "offsetX"),
                                                FloatArgumentType.getFloat(context, "offsetY"),
                                                FloatArgumentType.getFloat(context, "scaleX"),
                                                FloatArgumentType.getFloat(context, "scaleY"),
                                                0.0f // rotation
                                            ))
                                            .then(Commands.argument("rotation", FloatArgumentType.floatArg())
                                                .executes(context -> executeModifyText(
                                                    context,
                                                    FloatArgumentType.getFloat(context, "offsetX"),
                                                    FloatArgumentType.getFloat(context, "offsetY"),
                                                    FloatArgumentType.getFloat(context, "scaleX"),
                                                    FloatArgumentType.getFloat(context, "scaleY"),
                                                    FloatArgumentType.getFloat(context, "rotation")
                                                ))
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
    }
    
    /**
     * 添加文本到组
     */
    private static int executeAddText(CommandContext<CommandSourceStack> context, float offsetX, float offsetY, float scaleX, float scaleY, float rotation, int fadeIn, int stay, int fadeOut) throws CommandSyntaxException {
        Component text = ComponentArgument.getComponent(context, "text");
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建文本片段和组
        TextSegment segment = new TextSegment(text, offsetX, offsetY, scaleX, scaleY, rotation);
        TextGroup group = new TextGroup(groupId);
        group.addSegment(segment);
        
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new HashMap<>();
            groups.put(groupId, group);
            TransformNetworkHandler.sendTitle(player, groups, fadeIn, stay, fadeOut);
        }
        
        return 1;
    }
    
    /**
     * 设置组级别变换
     */
    private static int executeSetGroupTransform(CommandContext<CommandSourceStack> context, float groupOffsetX, float groupOffsetY, float groupScaleX, float groupScaleY, Float groupRotation) throws CommandSyntaxException {
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建一个空的组，只包含要更新的变换参数
        TextGroup group = new TextGroup(groupId);
        group.setGroupOffset(groupOffsetX, groupOffsetY);
        group.setGroupScale(groupScaleX, groupScaleY);
        if (groupRotation != null) {
            group.setGroupRotation(groupRotation);
        }
        
        // 发送空文本的组来更新变换参数
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new HashMap<>();
            groups.put(groupId, group);
            // 使用特殊标记(-1,-1,-1)表示这是更新操作，不是创建新title
            TransformNetworkHandler.sendTitle(player, groups, -1, -1, -1);
        }
        
        return 1;
    }
    
    /**
     * 设置单个参数
     */
    private static int executeSetSingleParam(CommandContext<CommandSourceStack> context, String paramName, float value) throws CommandSyntaxException {
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建一个空的组，只包含要更新的单个参数
        TextGroup group = new TextGroup(groupId);
        
        // 根据参数名设置对应的值（其他值保持默认，客户端会保留原有值）
        switch (paramName) {
            case "offsetX":
                group.setGroupOffset(value, 0); // offsetY设为0，但客户端会保留原值
                break;
            case "offsetY":
                group.setGroupOffset(0, value);
                break;
            case "scaleX":
                group.setGroupScale(value, 1);
                break;
            case "scaleY":
                group.setGroupScale(1, value);
                break;
            case "rotation":
                group.setGroupRotation(value);
                break;
        }
        
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new HashMap<>();
            groups.put(groupId, group);
            TransformNetworkHandler.sendTitle(player, groups, -1, -1, -1);
        }
        
        return 1;
    }
    
    /**
     * 清除指定组
     */
    private static int executeClearGroup(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 发送空的组来清除指定组（客户端需要特殊处理）
        // 目前先使用clearAll的方式
        for (ServerPlayer player : targets) {
            TransformNetworkHandler.clearGroup(player, groupId);
        }
        
        return 1;
    }
    
    /**
     * 清除所有组
     */
    private static int executeClearAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 发送空的Map来清除所有组
        for (ServerPlayer player : targets) {
            TransformNetworkHandler.clearAllGroups(player);
        }
        
        return 1;
    }
    
    /**
     * 修改组文本内容（保留变换参数和时间参数）
     */
    private static int executeModifyText(CommandContext<CommandSourceStack> context, float offsetX, float offsetY, float scaleX, float scaleY, float rotation) throws CommandSyntaxException {
        Component text = ComponentArgument.getComponent(context, "text");
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建新的文本片段和组
        TextSegment segment = new TextSegment(text, offsetX, offsetY, scaleX, scaleY, rotation);
        TextGroup group = new TextGroup(groupId);
        group.addSegment(segment);
        
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new HashMap<>();
            groups.put(groupId, group);
            // 使用特殊标记(-1,-1,-1)表示这是更新操作，不改变时间设置
            TransformNetworkHandler.sendTitle(player, groups, -1, -1, -1);
        }
        
        return 1;
    }
}
